package com.zrlog.blog.hexo.template;

import com.hibegin.common.util.BeanUtil;
import com.zrlog.blog.hexo.template.ejs.EjsResourceLoader;
import com.zrlog.blog.hexo.template.ejs.HexoHelperImpl;
import com.zrlog.blog.hexo.template.ejs.TemplateResolver;
import com.zrlog.blog.web.template.ZrLogTemplate;
import com.zrlog.blog.web.template.vo.BasePageInfo;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyExecutable;

import java.io.File;
import java.util.Map;

public class HexoTemplateEngine implements ZrLogTemplate {
    private Context context;
    private TemplateResolver resolver;
    private HexoHelperImpl helpers;
    private String template;

    private void injectHelpers(Value bindings, BasePageInfo pageInfo) {
        // 映射 partial
        bindings.putMember("partial", (ProxyExecutable) args -> {
            String path = args[0].asString();
            try {
                return helpers.partial(path, pageInfo);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        // 映射 url_for
        bindings.putMember("url_for", (ProxyExecutable) args -> helpers.url_for(args[0].asString()));
    }


    public static void main(String[] args) throws Exception {
        // 使用示例
        HexoTemplateEngine engine = new HexoTemplateEngine();
        engine.initClassTemplate("/include/templates/hexo-theme-fluid/layout/");
        BasePageInfo basePageInfo = new BasePageInfo();
        basePageInfo.setTitle("我的博客");
        String html = engine.render("test.ejs", basePageInfo);

        System.out.println(html);
    }

    @Override
    public void init(File path) throws Exception {

    }

    @Override
    public String render(String page, BasePageInfo pageInfo) throws Exception {
        // 3. 注入辅助函数到 JS 全局作用域
        Value bindings = context.getBindings("js");
        injectHelpers(bindings, pageInfo);

        Map<String, Object> convert = BeanUtil.convert(pageInfo, Map.class);
        // 关键：遍历 Map，将所有 key-value 注入为 JS 全局变量
        if (pageInfo != null) {
            convert.forEach(bindings::putMember);
        }

        // 现在 templateStr 也可以直接作为全局变量访问
        bindings.putMember("templateStr", EjsResourceLoader.read(template + page));

        // 执行渲染，注意这里第二个参数传空对象或 bindings 自身
        return context.eval("js", "ejs.render(templateStr, {})").asString();
    }

    @Override
    public void initClassTemplate(String template) {
        this.template = template;
        // 1. 初始化上下文，允许 Host 访问以进行 Java/JS 互操作
        this.context = Context.newBuilder("js")
                .allowHostAccess(HostAccess.ALL)
                .allowHostClassLookup(s -> true)
                .build();

        this.resolver = new TemplateResolver(template);
        this.helpers = new HexoHelperImpl(this, resolver);

        // 2. 加载 ejs.min.js
        String ejsLib = EjsResourceLoader.read("/include/templates/ejs.min.js");
        context.eval("js", ejsLib);
    }
}