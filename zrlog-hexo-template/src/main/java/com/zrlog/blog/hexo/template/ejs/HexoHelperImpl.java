package com.zrlog.blog.hexo.template.ejs;

import com.zrlog.blog.hexo.template.HexoTemplate;

import java.util.Map;
import java.util.Objects;

public class HexoHelperImpl {
    private final HexoTemplate engine;
    private final TemplateResolver resolver;

    public HexoHelperImpl(HexoTemplate engine, TemplateResolver resolver) {
        this.engine = engine;
        this.resolver = resolver;
    }

    // 对应 EJS 中的 partial(path, locals)
    public String partial(String path, Map<String, Object> data) throws Exception {
        // 1. 解析真实路径 (处理 ../ 等)
        String absolutePath = resolver.resolve(path);
        // 注意：Hexo 的 partial 路径通常是相对于当前模板目录的
        try {
            // 3. 递归渲染
            // 注意：这里需要确保 render 方法不会清空之前的全局 helpers
            return engine.doRender(absolutePath.substring(engine.getTemplate().length() - 1).replace(".ejs", ""), data);
        } catch (Exception e) {
            System.err.println("Partial 渲染失败: " + path + " -> " + e.getMessage());
            return "";
        } finally {
            // 3. 必须出栈，否则路径上下文会乱
            resolver.popPath();
        }
    }

    // 对应 url_for(path)
    public String url_for(String path) {
        if (Objects.isNull(path)) {
            return "";
        }
        String root = "/"; // 实际应从 config 中读取
        if (path.startsWith("/")) return root + path.substring(1);
        return root + path;
    }
}