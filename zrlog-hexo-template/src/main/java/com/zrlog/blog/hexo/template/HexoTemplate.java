package com.zrlog.blog.hexo.template;

import com.hibegin.http.server.util.PathUtil;
import com.zrlog.blog.hexo.template.ejs.TemplateResolver;
import com.zrlog.blog.hexo.template.fluid.FluidHexoObjectBox;
import com.zrlog.blog.hexo.template.util.GraalDataUtils;
import com.zrlog.blog.hexo.template.util.HexoRegisterHooks;
import com.zrlog.blog.hexo.template.util.YamlLoader;
import com.zrlog.blog.web.template.ZrLogTemplate;
import com.zrlog.blog.web.template.vo.BasePageInfo;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class HexoTemplate implements ZrLogTemplate {
    private final Context context;
    private TemplateResolver resolver;
    private String template;
    private String rootPath;
    private Map<String, Object> config;
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
        pageInfo.setTheme(config);
        new HexoRegisterHooks(pageInfo, resolver, this).injectHelpers(jsBindings);
        Map<String, Object> convert = HexoPageConverter.toIndexMap(pageInfo, page);

        for (Map.Entry<String, Object> entry : convert.entrySet()) {
            jsBindings.putMember(entry.getKey(), GraalDataUtils.makeJsFriendly(entry.getValue()));
        }
        this.locals = convert;
        convert.put("body", doRender((String) ((Map<String, Object>) convert.get("page")).get("layout"), convert));
        return doRender("/layout", convert);
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
        // 1. 初始化上下文，允许 Host 访问以进行 Java/JS 互操作
        String configYml = rootPath + "/_config.yml";
        this.config = YamlLoader.loadConfig(ZrLogResourceLoader.read(configYml));
        this.resolver = new TemplateResolver(template);
        this.hexoObjectBox = new FluidHexoObjectBox(config, rootPath);
        this.hexoObjectBox.setup(context);
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