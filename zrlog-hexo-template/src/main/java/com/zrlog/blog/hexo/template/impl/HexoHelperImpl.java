package com.zrlog.blog.hexo.template.impl;

import com.zrlog.blog.hexo.template.HexoTemplate;
import com.zrlog.blog.hexo.template.ejs.TemplateResolver;
import org.graalvm.polyglot.proxy.ProxyExecutable;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
            return engine.doRender(absolutePath.substring(engine.getTemplate().length()).replace(".ejs", ""), data);
        } catch (Exception e) {
            System.err.println("Partial 渲染失败: " + path + " -> " + e.getMessage());
            return e.getMessage();
        } finally {
            // 3. 必须出栈，否则路径上下文会乱
            resolver.popPath();
        }
    }

    // 对应 url_for(path)
    public String url_for(String path) {
        if (Objects.isNull(path) || path.isEmpty()) {
            return "";
        }
        if (path.startsWith("http:") || path.startsWith("https:")) {
            return path;
        }
        String root = "/"; // 实际应从 config 中读取
        if (path.startsWith("/")) return root + path.substring(1);
        return root + path;
    }

    public static ProxyExecutable getUrlJoinProvider() {
        return args -> {
            if (args.length == 0) return "";

            String joined = Arrays.stream(args)
                    .map(v -> v.isNull() ? "" : v.asString())
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.joining("/"));

            // 正则处理：
            // 1. 将三个及以上连续斜杠转为单斜杠 (/// -> /)
            // 2. 将非协议头位置的双斜杠转为单斜杠 (a//b -> a/b)
            // 3. 保护协议头 (http:// 保持不变)
            return joined.replaceAll("(?<!:)/{2,}", "/");
        };
    }
}