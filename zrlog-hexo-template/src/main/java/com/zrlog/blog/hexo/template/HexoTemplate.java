package com.zrlog.blog.hexo.template;

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
import java.util.Map;

public class HexoTemplate implements ZrLogTemplate {
    private final Context context;
    private TemplateResolver resolver;
    private String template;
    private String rootPath;
    private Map<String, Object> config;
    private Map<String, Object> locals;
    private HexoObjectBox hexoObjectBox;

    public HexoTemplate() {
        this.context = Context.newBuilder("js")
                .allowHostAccess(HostAccess.ALL)
                .allowHostClassLookup(s -> true)
                .option("js.ecmascript-version", "2022") // 建议开启现代语法支持
                .build();
        context.eval("js", ZrLogResourceLoader.read("classpath:/include/templates/ejs.min.js"));
        context.eval("js", ZrLogResourceLoader.read("classpath:/include/templates/hooks.js"));
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

    /*public static void main(String[] args) throws Exception {
        // 使用示例
        HexoTemplate engine = new HexoTemplate();
        engine.initClassTemplate("/include/templates/hexo-theme-fluid/");
        ArticleListPageVO basePageInfo = new ArticleListPageVO();
        basePageInfo.setTitle("我的博客");
        PageData<ArticleBasicDTO> data = new PageData<>();
        List<ArticleBasicDTO> articleBasicDTOList = new ArrayList<>();
        ArticleBasicDTO articleBasicDTO = new ArticleBasicDTO();
        articleBasicDTO.setTitle("Test");
        //articleBasicDTO.setTypeName("Type");
        articleBasicDTO.setUrl("/test.html");
        articleBasicDTO.setContent("Content");
        articleBasicDTOList.add(articleBasicDTO);
        data.setRows(articleBasicDTOList);
        basePageInfo.setData(data);
        basePageInfo.setTemplateUrl("/include/templates/hexo-theme-fluid/");
        String html = engine.render("/index", basePageInfo);
        IOUtil.writeBytesToFile(html.getBytes(), new File(PathUtil.getRootPath() + "/classes/1.html"));
        System.out.println(html);
    }*/

    @Override
    public void init(File path) throws Exception {
        //this.template =
    }

    @Override
    public String render(String page, BasePageInfo pageInfo) throws Exception {
        pageInfo.setTheme(config);
        Value bindings = context.getBindings("js");
        new HexoRegisterHooks(pageInfo, resolver, this).injectHelpers(bindings);
        Map<String, Object> convert = HexoPageConverter.toIndexMap(pageInfo, page, context);

        for (Map.Entry<String, Object> entry : convert.entrySet()) {
            bindings.putMember(entry.getKey(), GraalDataUtils.makeJsFriendly(entry.getValue()));
        }
        this.locals = convert;
        convert.put("body", doRender((String) ((Map<String, Object>) convert.get("page")).get("layout"), convert));
        return doRender("/layout", convert);
    }

    public String doRender(String page, Object locals) {
        if (page.contains("scripts")) {
            System.out.println("locals = " + locals);
        }
        // 1. 将 Java Map 转换为 JS 能够识别的 Proxy 对象
        // 使用之前写的 GraalDataUtils 确保 List/Map 结构正确
        Object jsFriendlyLocals = GraalDataUtils.makeJsFriendly(locals);

        // 2. 获取 JS 环境中的 ejs 对象（前提是你已经加载了 ejs.min.js）
        Value ejs = context.getBindings("js").getMember("ejs");

        if (ejs == null || ejs.getMember("render").isNull()) {
            throw new RuntimeException("EJS 引擎未初始化，请先加载 ejs.min.js");
        }
        String path = (template + page + ".ejs").replaceAll("//", "/");
        //System.out.println("path = " + path);
        try {
            // 3. 调用 JS 的 ejs.render(content, data, options)
            // 注意：第三个参数建议传入 { async: false } 确保同步返回字符串
            Value options = context.eval("js", "({ async: false, cache: false })");
            Value result = ejs.getMember("render").execute(ZrLogResourceLoader.read(path), jsFriendlyLocals, options);
            return result.asString();
        } catch (Exception e) {
            // 捕获 JS 运行时的详细错误堆栈
            System.err.println("EJS 渲染出错: " + path + e.getMessage());
            throw e;
        }
    }

    @Override
    public void initClassTemplate(String templateBase) {
        this.rootPath = "classpath:" + templateBase;
        this.template = (rootPath + "/layout").replace("//", "/");
        // 1. 初始化上下文，允许 Host 访问以进行 Java/JS 互操作
        String configYml = "classpath:" + templateBase + "_config.yml";
        this.config = YamlLoader.loadConfig(ZrLogResourceLoader.read(configYml));
        this.resolver = new TemplateResolver(template);
        this.hexoObjectBox = new FluidHexoObjectBox(config, rootPath);
        this.hexoObjectBox.setup(context);
    }

    public Context getContext() {
        return context;
    }
}