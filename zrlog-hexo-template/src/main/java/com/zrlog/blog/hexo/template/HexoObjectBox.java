package com.zrlog.blog.hexo.template;

import com.hibegin.common.util.LoggerUtil;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.blog.hexo.template.impl.ScriptProvider;
import com.zrlog.blog.hexo.template.util.ResourceScanner;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyExecutable;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public abstract class HexoObjectBox {

    private static final Logger LOGGER = LoggerUtil.getLogger(HexoObjectBox.class);
    protected final Map<String, Object> themeConfig;
    protected final String themeDir;
    protected final Context context;
    protected final Value bindings;
    protected final HexoTemplate hexoTemplate;
    protected final ScriptProvider scriptProvider;

    public HexoObjectBox(Map<String, Object> themeConfig, String themeDir, HexoTemplate hexoTemplate) {
        this.themeConfig = themeConfig;
        this.themeDir = themeDir;
        this.context = hexoTemplate.getContext();
        this.bindings = context.getBindings("js");
        this.hexoTemplate = hexoTemplate;
        this.scriptProvider = new ScriptProvider();
    }

    protected abstract boolean filterRegister(String name, Value[] values);

    protected abstract boolean helperRegister(String name, Value[] values);

    public void setup() {
        // 2. 创建并注入 hexo 全局模型
        Value hexo = context.eval("js", "({})");
        hexo.putMember("theme_dir", themeDir);

        // 注入配置
        Value theme = context.eval("js", "({})");
        theme.putMember("config", themeConfig);
        hexo.putMember("theme", theme);

        // 3. 构建注入逻辑的核心 (filter.register)
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
            if (!filterRegister(type, args)) {
                bindings.putMember(type, callback);
                System.out.println("inject filter = " + type);
            }
            return null;
        });
        helper.putMember("register", (ProxyExecutable) args -> {
            String type = args[0].asString();
            Value callback = args[1];
            if (!helperRegister(type, args)) {
                bindings.putMember(type, callback);
                System.out.println("inject helper = " + type);
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

        // 4. 扫描并执行主题 scripts 目录下的所有脚本
        try {
            ResourceScanner scanner = new ResourceScanner(themeDir);
            List<String> scripts = scanner.listFiles("scripts/");
            scriptProvider.addBaseScript("path", new String(PathUtil.getConfInputStream("hexo/scripts/path.js").readAllBytes()));
            scriptProvider.addBaseScript("hexo-util", new String(PathUtil.getConfInputStream("hexo/scripts/hexo-util.js").readAllBytes()));
            scriptProvider.addBaseScript("url", new String(PathUtil.getConfInputStream("hexo/scripts/url.js").readAllBytes()));
            scriptProvider.addBaseScript("moize", new String(PathUtil.getConfInputStream("hexo/scripts/moize.js").readAllBytes()));
            for (String script : scripts) {
                scriptProvider.addScript(script.substring((themeDir + "/scripts/").length()).replaceAll(".js", ""), ZrLogResourceLoader.read(script));
            }
            for (String scriptPath : scripts) {
                if (scriptPath.contains("/generators/")) {
                    continue;
                }
                String code = ZrLogResourceLoader.read(scriptPath);
                if (code.contains("register('") || code.contains("register(\"")) {
                    try {
                        context.eval("js", "{" + code + "}");
                        LOGGER.info("Exec " + scriptPath + " success");
                    } catch (Exception e) {
                        LOGGER.severe("Exec " + scriptPath + " error " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("跳过脚本加载: " + e.getMessage());
        }
    }
}
