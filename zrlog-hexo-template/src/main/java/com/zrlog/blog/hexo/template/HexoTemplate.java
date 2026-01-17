package com.zrlog.blog.hexo.template;

import com.hibegin.common.util.LoggerUtil;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.blog.hexo.template.fluid.FluidHexoObjectBox;
import com.zrlog.blog.hexo.template.util.GraalDataUtils;
import com.zrlog.blog.hexo.template.util.YamlLoader;
import com.zrlog.blog.web.template.ZrLogTemplate;
import com.zrlog.blog.web.template.vo.BasePageInfo;
import com.zrlog.business.type.TemplateType;
import com.zrlog.common.Constants;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

public class HexoTemplate implements ZrLogTemplate {
    private static final Logger LOGGER = LoggerUtil.getLogger(HexoTemplate.class);
    private final Context context;
    private String template;
    private String rootPath;
    private Map<String, Object> locals;
    private final Value jsBindings;
    private final Value ejs;
    private BasePageInfo pageInfo;
    private final String templateExt = ".ejs";

    public HexoTemplate() {
        this.context = Context.newBuilder("js")
                .allowHostAccess(HostAccess.ALL)
                .allowExperimentalOptions(true)
                .allowHostClassLookup(s -> true)
                .logHandler(LOGGER.getHandlers()[0])
                .option("engine.WarnVirtualThreadSupport", "false")
                .build();
        try {
            context.eval("js", "var global = globalThis;");
            context.eval("js", new String(PathUtil.getConfInputStream("hexo/scripts/ejs.min.js").readAllBytes()));
            context.eval("js", new String(PathUtil.getConfInputStream("hexo/scripts/hooks.js").readAllBytes()));
            context.eval("js", new String(PathUtil.getConfInputStream("hexo/scripts/require.js").readAllBytes()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        jsBindings = context.getBindings("js");
        // 2. 获取 JS 环境中的 ejs 对象（前提是你已经加载了 ejs.min.js）
        ejs = jsBindings.getMember("ejs");
        if (ejs == null || ejs.getMember("render").isNull()) {
            throw new RuntimeException("EJS 引擎未初始化，请先加载 ejs.min.js");
        }
        //context.eval("js", "console.log('Hexo runtime ...')");
    }

    public Map<String, Object> getLocals() {
        return locals;
    }

    public String getTemplate() {
        return template;
    }

    public String getRootPath() {
        return rootPath;
    }

    public BasePageInfo getPageInfo() {
        return pageInfo;
    }

    @Override
    public void init(File path) throws Exception {
        this.rootPath = path.getAbsolutePath();
        setup();
    }

    @Override
    public String render(String page, BasePageInfo pageInfo) throws Exception {
        this.pageInfo = pageInfo;
        Map<String, Object> configMap = pageInfo.getInit().getTemplateConfigCacheMap().get(pageInfo.getTemplate());
        Map<String, Object> config;
        if (Objects.nonNull(configMap) && configMap.containsKey(Constants.TEMPLATE_CONFIG_STR_KEY)) {
            config = YamlLoader.loadConfig((String) configMap.get(Constants.TEMPLATE_CONFIG_STR_KEY));
        } else {
            config = YamlLoader.loadConfig(ZrLogResourceLoader.read(rootPath + "/" + TemplateType.NODE_JS.getConfigFile()));
        }
        pageInfo.setTheme(config);
        this.locals = HexoPageConverter.toHexoMap(pageInfo, page);
        new FluidHexoObjectBox(config, rootPath, this).setup();
        this.locals.put("body", doRender((String) YamlLoader.getNestedValue(locals, "page.layout"), locals));
        return doRender("/layout", locals);
    }

    public String doRender(String page, Map<String, Object> locals) {
        for (Map.Entry<String, Object> entry : locals.entrySet()) {
            jsBindings.putMember(entry.getKey(), GraalDataUtils.makeJsFriendly(entry.getValue()));
        }
        Object jsFriendlyLocals = GraalDataUtils.makeJsFriendly(locals);
        String path = (template + "/" + page + (page.endsWith(templateExt) ? "" : templateExt)).replaceAll("//", "/");
        Value options = context.eval("js", "({ async: false, cache: false })");
        String read = ZrLogResourceLoader.read(path);
        Value result = ejs.getMember("render").execute(read, jsFriendlyLocals, options);
        return result.asString();
    }

    private void setup() {
        this.template = (rootPath + "/layout");
    }

    @Override
    public void initClassTemplate(String templateBase) {
        this.rootPath = "classpath:" + templateBase;
        setup();
    }

    public Context getContext() {
        return context;
    }

    public String getTemplateExt() {
        return templateExt;
    }
}