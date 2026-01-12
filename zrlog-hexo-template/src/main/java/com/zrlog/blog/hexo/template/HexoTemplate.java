package com.zrlog.blog.hexo.template;

import com.hibegin.common.dao.dto.PageData;
import com.hibegin.common.util.IOUtil;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.blog.hexo.template.ejs.EjsResourceLoader;
import com.zrlog.blog.hexo.template.ejs.TemplateResolver;
import com.zrlog.blog.hexo.template.util.GraalDataUtils;
import com.zrlog.blog.hexo.template.util.HexoPageConverter;
import com.zrlog.blog.hexo.template.util.HexoRegisterHooks;
import com.zrlog.blog.hexo.template.util.YamlLoader;
import com.zrlog.blog.web.template.ZrLogTemplate;
import com.zrlog.blog.web.template.vo.ArticleListPageVO;
import com.zrlog.blog.web.template.vo.BasePageInfo;
import com.zrlog.common.exception.NotImplementException;
import com.zrlog.data.dto.ArticleBasicDTO;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Value;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HexoTemplate implements ZrLogTemplate {
    private Context context;
    private TemplateResolver resolver;
    private String template;
    private Map<String, Object> config;

    public String getTemplate() {
        return template;
    }


    public static void main(String[] args) throws Exception {
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
        String html = engine.render("/index", basePageInfo);

        IOUtil.writeBytesToFile(html.getBytes(), new File(PathUtil.getRootPath() + "/1.html"));
        System.out.println(html);
    }

    @Override
    public void init(File path) throws Exception {
        //this.template =
    }

    @Override
    public String render(String page, BasePageInfo pageInfo) throws Exception {
        pageInfo.setTheme(config);
        Map<String, Object> convert;
        Value bindings = context.getBindings("js");
        new HexoRegisterHooks(pageInfo, resolver, this).injectHelpers(bindings);
        if (page.equals("/index")) {
            convert = HexoPageConverter.toIndexMap(pageInfo, page, context);
        } else if (page.equals("/test")) {
            convert = HexoPageConverter.toIndexMap(pageInfo, page, context);
        } else if (page.equals("/404")) {
            convert = HexoPageConverter.toIndexMap(pageInfo, page, context);
        } else {
            throw new NotImplementException();
        }

        for (Map.Entry<String, Object> entry : convert.entrySet()) {
            bindings.putMember(entry.getKey(), GraalDataUtils.makeJsFriendly(entry.getValue()));
        }
        convert.put("body", doRender(page, convert));
        // 现在 templateStr 也可以直接作为全局变量访问
        //convert.putMember("page", convert);
        //bindings.putMember("body", doRender(page, convert));
        // 3. 注入辅助函数到 JS 全局作用域
        // 关键：遍历 Map，将所有 key-value 注入为 JS 全局变量
        return doRender("/layout", convert);
    }

    public String doRender(String page, Map<String, Object> locals) {
        // 1. 将 Java Map 转换为 JS 能够识别的 Proxy 对象
        // 使用之前写的 GraalDataUtils 确保 List/Map 结构正确
        Object jsFriendlyLocals = GraalDataUtils.makeJsFriendly(locals);

        // 2. 获取 JS 环境中的 ejs 对象（前提是你已经加载了 ejs.min.js）
        Value ejs = context.getBindings("js").getMember("ejs");

        if (ejs == null || ejs.getMember("render").isNull()) {
            throw new RuntimeException("EJS 引擎未初始化，请先加载 ejs.min.js");
        }

        try {
            // 3. 调用 JS 的 ejs.render(content, data, options)
            // 注意：第三个参数建议传入 { async: false } 确保同步返回字符串
            Value options = context.eval("js", "({ async: false, cache: false })");
            Value result = ejs.getMember("render").execute(EjsResourceLoader.read(template + page + ".ejs"), jsFriendlyLocals, options);
            return result.asString();
        } catch (PolyglotException e) {
            // 捕获 JS 运行时的详细错误堆栈
            System.err.println("EJS 渲染出错: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void initClassTemplate(String templateBase) {
        this.template = "classpath:" + templateBase + "/layout";
        // 1. 初始化上下文，允许 Host 访问以进行 Java/JS 互操作
        this.context = Context.newBuilder("js")
                .allowHostAccess(HostAccess.ALL)
                .allowHostClassLookup(s -> true)
                .option("js.ecmascript-version", "2022") // 建议开启现代语法支持
                .build();

        this.resolver = new TemplateResolver(template);
        // 2. 加载 ejs.min.js
        String ejsLib = EjsResourceLoader.read("classpath:/include/templates/ejs.min.js");
        context.eval("js", "Array.prototype.each = Array.prototype.forEach;");
        context.eval("js", ejsLib);
        String configYml = "classpath:" + templateBase + "_config.yml";
        this.config = YamlLoader.loadConfig(EjsResourceLoader.read(configYml));
    }
}