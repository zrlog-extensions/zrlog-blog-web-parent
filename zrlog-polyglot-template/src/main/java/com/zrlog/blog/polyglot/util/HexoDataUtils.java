package com.zrlog.blog.polyglot.util;

import org.graalvm.polyglot.proxy.ProxyExecutable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HexoDataUtils {

    /**
     * 将普通的 List 封装成带 .data 属性的 Map
     * 适配 Pug 中的 each item in tags.data
     */
    public static Map<String, Object> wrap(List<?> list) {
        Map<String, Object> wrapper = new HashMap<>();
        // 如果 list 为 null，我们也给一个空数组，防止 Pug 报错 Cannot convert null to object
        wrapper.put("data", list != null ? list : List.of());

        // 模拟 Hexo 集合常用的 length 属性（可选）
        wrapper.put("length", list != null ? list.size() : 0);
        wrapper.put("sort", (ProxyExecutable) args -> {
            return list;
        });
        wrapper.put("forEach", (ProxyExecutable) args -> {
            return args[0];
        });

        return wrapper;
    }

    public static Map<String, Object> wrapArticle(Map<String, Object> rawData) {
        // 假设 rawData 里有 tags 和 categories
        if (rawData.get("tags") instanceof List) {
            rawData.put("tags", wrap((List<?>) rawData.get("tags")));
        }
        if (rawData.get("categories") instanceof List) {
            rawData.put("categories", wrap((List<?>) rawData.get("categories")));
        }
        return rawData;
    }
}