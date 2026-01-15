package com.zrlog.blog.hexo.template;

import com.hibegin.http.server.util.PathUtil;
import com.zrlog.blog.hexo.template.ejs.TemplateResolver;
import com.zrlog.blog.hexo.template.fluid.FluidHexoObjectBox;
import com.zrlog.blog.hexo.template.fluid.FluidHooks;
import com.zrlog.blog.hexo.template.util.GraalDataUtils;
import com.zrlog.blog.hexo.template.util.HexoBaseHooks;
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

public class HexoTemplate implements ZrLogTemplate {
    private final Context context;
    private String template;
    private String rootPath;
    private Map<String, Object> locals;
    private HexoObjectBox hexoObjectBox;
    private final Value jsBindings;
    private final Value ejs;

    public HexoTemplate() {
        this.context = Context.newBuilder("js")
                .allowHostAccess(HostAccess.ALL)
                .allowHostClassLookup(s -> true)
                .option("js.ecmascript-version", "2022") // 建议开启现代语法支持
                .build();
        try {
            context.eval("js", new String(PathUtil.getConfInputStream("hexo/scripts/ejs.min.js").readAllBytes()));
            context.eval("js", new String(PathUtil.getConfInputStream("hexo/scripts/hooks.js").readAllBytes()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        jsBindings = context.getBindings("js");
        // 2. 获取 JS 环境中的 ejs 对象（前提是你已经加载了 ejs.min.js）
        ejs = jsBindings.getMember("ejs");
        if (ejs == null || ejs.getMember("render").isNull()) {
            throw new RuntimeException("EJS 引擎未初始化，请先加载 ejs.min.js");
        }
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

    public HexoObjectBox getHexoObjectBox() {
        return hexoObjectBox;
    }

    @Override
    public void init(File path) throws Exception {
        this.rootPath = path.getAbsolutePath();
        setup();
    }

    @Override
    public String render(String page, BasePageInfo pageInfo) throws Exception {
        Map<String, Object> configMap = pageInfo.getInit().getTemplateConfigCacheMap().get(pageInfo.getTemplate());
        Map<String, Object> config;
        if (Objects.nonNull(configMap) && configMap.containsKey(Constants.TEMPLATE_CONFIG_STR_KEY)) {
            config = YamlLoader.loadConfig((String) configMap.get(Constants.TEMPLATE_CONFIG_STR_KEY));
        } else {
            config = YamlLoader.loadConfig(ZrLogResourceLoader.read(rootPath + "/" + TemplateType.NODE_JS.getConfigFile()));
        }
        pageInfo.setTheme(config);
        this.hexoObjectBox = new FluidHexoObjectBox(config, rootPath);
        this.hexoObjectBox.setup(context);
        new HexoBaseHooks(pageInfo, new TemplateResolver(template), this).inject(jsBindings);
        new FluidHooks(pageInfo).inject(jsBindings);
        this.locals = HexoPageConverter.toHexoMap(pageInfo, page);
        for (Map.Entry<String, Object> entry : locals.entrySet()) {
            jsBindings.putMember(entry.getKey(), GraalDataUtils.makeJsFriendly(entry.getValue()));
        }
        this.locals.put("body", doRender((String) ((Map<String, Object>) locals.get("page")).get("layout"), locals));
        return doRender("/layout", locals);
    }

    public String doRender(String page, Object locals) {
        Object jsFriendlyLocals = GraalDataUtils.makeJsFriendly(locals);
        String path = (template + page + ".ejs").replaceAll("//", "/");
        try {
            Value options = context.eval("js", "({ async: false, cache: false })");
            String read = ZrLogResourceLoader.read(path);
            Value result = ejs.getMember("render").execute(read, jsFriendlyLocals, options);
            return result.asString();
        } catch (Exception e) {
            System.err.println("EJS 渲染出错: " + path + e.getMessage());
            throw e;
        }
    }

    private void setup() {
        this.template = (rootPath + "/layout").replace("//", "/");
    }

    @Override
    public void initClassTemplate(String templateBase) {
        this.rootPath = "classpath:" + templateBase;
        setup();
    }

    public Context getContext() {
        return context;
    }
}