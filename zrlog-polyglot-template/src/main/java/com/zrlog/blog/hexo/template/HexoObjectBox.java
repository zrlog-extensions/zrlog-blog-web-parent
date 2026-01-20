package com.zrlog.blog.hexo.template;

import com.hibegin.common.util.EnvKit;
import com.hibegin.common.util.IOUtil;
import com.hibegin.common.util.LoggerUtil;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.blog.hexo.template.util.HexoBaseHooks;
import com.zrlog.blog.polyglot.JsTemplateRender;
import com.zrlog.blog.polyglot.resource.ResourceScanner;
import com.zrlog.blog.polyglot.resource.ScriptProvider;
import com.zrlog.blog.polyglot.resource.ZrLogResourceLoader;
import com.zrlog.blog.polyglot.sytlus.StylusBundler;
import com.zrlog.blog.polyglot.util.GraalDataUtils;
import com.zrlog.blog.web.template.vo.BasePageInfo;
import com.zrlog.common.vo.TemplateVO;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyExecutable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

public abstract class HexoObjectBox {

    private static final Logger LOGGER = LoggerUtil.getLogger(HexoObjectBox.class);
    protected final Map<String, Object> theme;
    protected final ScriptProvider scriptProvider;
    protected final BasePageInfo basePageInfo;
    protected final String rooPath;
    private boolean stylusInit = false;
    protected final TemplateVO templateVO;

    public HexoObjectBox(Map<String, Object> theme, String rootPath, BasePageInfo basePageInfo, TemplateVO templateVO) {
        this.theme = theme;
        this.templateVO = templateVO;
        this.scriptProvider = new ScriptProvider();
        this.basePageInfo = basePageInfo;
        this.rooPath = rootPath;
        this.fillConfig();
    }

    protected abstract boolean filterRegister(Context context, String name, Value[] values);

    protected abstract boolean helperRegister(JsTemplateRender jsTemplateRender, String name, Value[] values);

    public abstract String getStylRoot();

    protected abstract void fillConfig();

    protected abstract void regisConfig(Value bindings);

    public List<String> getCompileStyl() {
        return new ArrayList<>();
    }

    public void regStyleHooks(Context context) {

    }

    private void initStylus(Context context) throws IOException {
        if (stylusInit) {
            return;
        }
        stylusInit = true;
        context.eval("js", new String(PathUtil.getConfInputStream("base/scripts/stylus-renderer.min.js").readAllBytes()));
        context.eval("js", new String(PathUtil.getConfInputStream("base/scripts/stylus-polyfill.js").readAllBytes()));
    }

    public void compileStyl(Context context) throws Exception {
        for (String compileStyl : getCompileStyl()) {
            // 3. 准备 Stylus 代码
            String styleRoot = rooPath + getStylRoot();
            String resourceFile = styleRoot + compileStyl;
            File staticFile = PathUtil.getStaticFile(basePageInfo.getTemplate() + getStylRoot() + compileStyl.replace(".styl", ".css"));
            if (staticFile.exists()) {
                return;
            }
            if (ZrLogResourceLoader.exists(styleRoot + compileStyl)) {
                initStylus(context);
                String stylusCode = new StylusBundler(styleRoot).bundle(compileStyl).replaceAll("(?s)/\\*.*?\\*/", "").trim();
                context.getBindings("js").putMember("myStylusCode", stylusCode.trim());
                context.eval("js", "var renderer = new StylusRenderer(myStylusCode); ");
                context.eval("js", new String(PathUtil.getConfInputStream("base/scripts/stylus-hooks.js").readAllBytes()));
                regStyleHooks(context);
                try {
                    String renderResult = context.eval("js", "renderer.render();").asString();
                    if (Objects.nonNull(renderResult) && !renderResult.trim().isEmpty()) {
                        IOUtil.writeStrToFile(renderResult, staticFile);
                    }
                } catch (Exception e) {
                    LOGGER.warning(resourceFile + " compile error " + e.getMessage());
                }
            } else {
                LOGGER.warning(resourceFile + " not found");
            }
        }

    }

    public void setup(JsTemplateRender jsTemplateRender) throws Exception {
        Value bindings = jsTemplateRender.getJsBindings();
        Context context = jsTemplateRender.getContext();

        Value hexo = context.eval("js", "({})");
        hexo.putMember("theme_dir", rooPath);

        hexo.putMember("config", GraalDataUtils.makeJsFriendly(theme));
        hexo.putMember("theme", GraalDataUtils.makeJsFriendly(theme));

        Value extend = context.eval("js", "({})");
        Value filter = context.eval("js", "({})");
        Value helper = context.eval("js", "({})");
        Value on = context.eval("js", "({})");
        Value tag = context.eval("js", "({})");
        //ignore
        Value generator = context.eval("js", "({})");

        bindings.putMember("scriptProvider", scriptProvider);

        filter.putMember("register", (ProxyExecutable) args -> {
            String type = args[0].asString();
            Value callback = args[1];
            if (!filterRegister(context, type, args)) {
                bindings.putMember(type, callback);
            }
            return null;
        });
        helper.putMember("register", (ProxyExecutable) args -> {
            String type = args[0].asString();
            Value callback = args[1];
            if (!helperRegister(jsTemplateRender, type, args)) {
                bindings.putMember(type, callback);
                if (EnvKit.isDevMode()) {
                    LOGGER.info("inject helper = " + type);
                }
            }

            return null;
        });
        generator.putMember("register", (ProxyExecutable) args -> {
            //ignore
            return null;
        });
        generator.putMember("get", (ProxyExecutable) args -> {
            //ignore
            return null;
        });
        tag.putMember("register", (ProxyExecutable) args -> {
            //ignore
            return null;
        });


        extend.putMember("filter", filter);
        extend.putMember("helper", helper);
        extend.putMember("generator", generator);
        extend.putMember("tag", tag);
        hexo.putMember("extend", extend);
        hexo.putMember("on", on);
        bindings.putMember("hexo", hexo);
        bindings.putMember("ctx", hexo);

        new HexoBaseHooks(rooPath, jsTemplateRender, basePageInfo, theme).inject(bindings);
        scanScripts(context);
        regisConfig(bindings);
        compileStyl(context);
    }

    private void scanScripts(Context context) throws IOException {
        ResourceScanner scanner = new ResourceScanner(rooPath);
        List<String> scripts = scanner.listFiles("scripts/").stream().filter(e -> e.endsWith(".js")).toList();
        scriptProvider.addBaseScript("path", new String(PathUtil.getConfInputStream("base/scripts/path.js").readAllBytes()));
        scriptProvider.addBaseScript("hexo-util", new String(PathUtil.getConfInputStream("hexo/scripts/hexo-util.js").readAllBytes()));
        scriptProvider.addBaseScript("url", new String(PathUtil.getConfInputStream("base/scripts/url.js").readAllBytes()));
        //scriptProvider.addBaseScript("moize", new String(PathUtil.getConfInputStream("hexo/scripts/moize.js").readAllBytes()));
        for (String script : scripts) {
            scriptProvider.addScript(script.substring((rooPath + "/scripts/").length()).replaceAll(".js", ""), ZrLogResourceLoader.read(script));
        }
        for (String scriptPath : scripts) {
            if (scriptPath.contains("/generators/")) {
                continue;
            }
            if (scriptPath.contains("/events/")) {
                continue;
            }
            String code = ZrLogResourceLoader.read(scriptPath);
            if (code.contains("register('") || code.contains("register(\"")) {
                try {
                    context.eval("js", "{" + code + "}");
                    //LOGGER.info("Exec " + scriptPath + " success");
                } catch (Exception e) {
                    LOGGER.severe("Exec " + scriptPath + " error " + e.getMessage());
                }
            }
        }
    }
}
