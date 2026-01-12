package com.zrlog.blog.hexo.template.ejs;

import com.zrlog.blog.hexo.template.HexoTemplateEngine;
import com.zrlog.blog.web.template.vo.BasePageInfo;

public class HexoHelperImpl {
    private final HexoTemplateEngine engine;
    private final TemplateResolver resolver;

    public HexoHelperImpl(HexoTemplateEngine engine, TemplateResolver resolver) {
        this.engine = engine;
        this.resolver = resolver;
    }

    // 对应 EJS 中的 partial(path, locals)
    public String partial(String path, BasePageInfo locals) throws Exception {
        // 1. 解析真实路径 (处理 ../ 等)
        String absolutePath = resolver.resolve(path);
        // 2. 读取内容
        String content = EjsResourceLoader.read(absolutePath);
        // 3. 递归渲染 (保持当前的上下文)
        return engine.render(content, locals);
    }

    // 对应 url_for(path)
    public String url_for(String path) {
        String root = "/"; // 实际应从 config 中读取
        if (path.startsWith("/")) return root + path.substring(1);
        return root + path;
    }
}