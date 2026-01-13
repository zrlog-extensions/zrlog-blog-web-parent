package com.zrlog.blog.hexo.template.fluid;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class FluidConfigExporter {

    public static String getExportConfigJson(Map<String, Object> config, Map<String, Object> theme) {
        try {
            Map<String, Object> exportConfig = new HashMap<>();

            // 1. 基础字段
            String url = (String) config.getOrDefault("url", "");
            exportConfig.put("hostname", parseHostname(url));
            exportConfig.put("root", config.getOrDefault("root", "/"));
            exportConfig.put("version", "1.9.8");

            // 2. 从 theme 获取嵌套配置 (建议使用自定义的 getNested 方法防 NPE)
            exportConfig.put("typing", getNested(theme, "fun_features", "typing"));
            exportConfig.put("anchorjs", getNested(theme, "fun_features", "anchorjs"));
            exportConfig.put("progressbar", getNested(theme, "fun_features", "progressbar"));
            exportConfig.put("code_language", getNested(theme, "code", "language"));
            exportConfig.put("copy_btn", getNested(theme, "code", "copy_btn"));
            exportConfig.put("image_caption", getNested(theme, "post", "image_caption"));
            exportConfig.put("image_zoom", getNested(theme, "post", "image_zoom"));
            exportConfig.put("toc", getNested(theme, "post", "toc"));
            exportConfig.put("lazyload", theme.get("lazyload"));
            exportConfig.put("web_analytics", theme.get("web_analytics"));

            // 3. 路径处理 (对应 JS 的 urlJoin)
            String searchPath = (String) getNested(theme, "search", "path");
            String root = (String) config.getOrDefault("root", "/");
            exportConfig.put("search_path", (root + "/" + searchPath).replaceAll("/+", "/"));
            exportConfig.put("include_content_in_search", getNested(theme, "search", "content"));

            // 核心：直接转为 JSON 字符串，防止 EJS 渲染时二次处理
            return new Gson().toJson(exportConfig);
        } catch (Exception e) {
            e.fillInStackTrace();
            return "{}";
        }
    }

    private static String parseHostname(String url) {
        if (url == null || !url.contains("//")) return url;
        return url.split("//")[1].split("/")[0];
    }

    @SuppressWarnings("unchecked")
    private static Object getNested(Map<String, Object> map, String... keys) {
        Object current = map;
        for (String key : keys) {
            if (current instanceof Map) {
                current = ((Map<String, Object>) current).get(key);
            } else {
                return null;
            }
        }
        return current;
    }
}