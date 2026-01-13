package com.zrlog.blog.hexo.template.fluid;

import com.zrlog.blog.hexo.template.HexoObjectBox;
import com.zrlog.blog.hexo.template.InjectionStorage;
import com.zrlog.blog.hexo.template.util.ResourceScanner;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyExecutable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FluidHexoObjectBox implements HexoObjectBox {

    // 用于存放注入的模板路径：SlotName -> List<FilePath>
    private final Map<String, List<String>> injectionPoints = new ConcurrentHashMap<>();
    private final Map<String, Object> themeConfig;
    private final String themeDir;

    public FluidHexoObjectBox(Map<String, Object> themeConfig, String themeDir) {
        this.themeConfig = themeConfig;
        this.themeDir = themeDir;
    }

    @Override
    public void setup(Context context) {
        // 1. 模拟 Node.js 环境的基础工具 (path.join)
        context.eval("js", "var path = { join: function() { " +
                "return Array.from(arguments).join('/').replace(/\\/+/g, '/'); " +
                "} };");

        // 2. 创建并注入 hexo 全局模型
        Value bindings = context.getBindings("js");
        Value hexo = context.eval("js", "({})");
        hexo.putMember("theme_dir", themeDir);

        // 注入配置
        Value theme = context.eval("js", "({})");
        theme.putMember("config", themeConfig);
        hexo.putMember("theme", theme);

        // 3. 构建注入逻辑的核心 (filter.register)
        Value extend = context.eval("js", "({})");
        Value filter = context.eval("js", "({})");

        filter.putMember("register", (ProxyExecutable) args -> {
            String type = args[0].asString();
            if ("theme_inject".equals(type)) {
                Value callback = args[1];
                // 关键：当脚本运行回调时，传入我们构造的 injects 代理对象
                callback.execute(createInjectsProxy(context));
            }
            return null;
        });

        extend.putMember("filter", filter);
        hexo.putMember("extend", extend);
        bindings.putMember("hexo", hexo);

        // 4. 扫描并执行主题 scripts 目录下的所有脚本
        try {
            ResourceScanner scanner = new ResourceScanner();
            // 注意：这里扫描 scripts 目录，让 JS 脚本自己跑
            List<String> scripts = scanner.listFiles(themeDir);
            for (String scriptPath : scripts) {
                String code = readText(scriptPath);
                if (code.contains("('theme_inject")) {
                    try {
                        context.eval("js", code.replace("const path = require('path');", ""));
                    } catch (Exception e) {
                        System.err.println("跳过执行脚本加载: " + scriptPath + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("跳过脚本加载: " + e.getMessage());
        }
    }

    /**
     * 创建一个动态代理对象，捕获 injects.xxx.file() 的调用
     */
    private Value createInjectsProxy(Context context) {
        // 这是一个“万能插槽”处理器，处理诸如 injects.header.file(...) 的调用
        Value handler = context.eval("js", "(function(storage) {" +
                "  return new Proxy({}, {" +
                "    get: function(target, slot) {" +
                "      return {" +
                "        file: function(name, path) {" +
                "          storage.add(slot, path);" + // 调用 Java 的 add 方法
                "        }" +
                "      };" +
                "    }" +
                "  });" +
                "})").execute(new InjectionStorage(injectionPoints,themeDir));
        return handler;
    }

    // 辅助方法：读取 Classpath 文件内容
    private String readText(String path) throws java.io.IOException {
        try (var is = getClass().getClassLoader().getResourceAsStream(path)) {
            if (is == null) return "";
            return new String(is.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        }
    }

    @Override
    public List<String> getInjectionPoints(String partal) {
        return injectionPoints.get(partal);
    }
}
