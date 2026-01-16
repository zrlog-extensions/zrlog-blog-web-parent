package com.zrlog.blog.hexo.template;

import org.graalvm.polyglot.HostAccess;

import java.util.HashMap;
import java.util.Map;

public class ScriptProvider {
    // 提前把所有脚本读入内存，拒绝文件系统
    private final Map<String, String> cache = new HashMap<>();
    private final Map<String, String> baseLibCache = new HashMap<>();

    public void addScript(String name, String content) {
        cache.put(name, content);
    }

    public void addBaseScript(String name, String content) {
        baseLibCache.put(name, content);
    }

    @HostAccess.Export
    public String load(String id) {
        // 这里可以做路径映射，比如把 'hexo-util' 映射到你准备好的 mock 源码
        String realPath = id.replace("../", "");
        if(baseLibCache.containsKey(realPath)) {
            return baseLibCache.get(realPath);
        }
        return cache.getOrDefault(realPath, "");
    }
}