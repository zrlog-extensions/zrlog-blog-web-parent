package com.zrlog.blog.hexo.template.ejs;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Stack;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class TemplateResolver {
    private final Stack<String> directoryStack = new Stack<>();
    private final String baseDir;
    private static final String CLASSPATH_PREFIX = "classpath:";

    public TemplateResolver(String baseDir) {
        // 确保 baseDir 以协议开头或为绝对路径
        this.baseDir = normalizePath(baseDir);
        this.directoryStack.push(this.baseDir);
    }

    public String resolve(String relativePath) {
        String currentDir = directoryStack.peek();
        String result;

        if (currentDir.startsWith(CLASSPATH_PREFIX)) {
            // Classpath 逻辑：手动拼接路径，避免 Path 对象破坏协议头
            String pathPart = currentDir.substring(CLASSPATH_PREFIX.length());
            // 使用 Paths 处理路径合并逻辑（如 ../ 或 ./）
            Path resolved = Paths.get(pathPart).resolve(relativePath).normalize();
            result = CLASSPATH_PREFIX + resolved.toString().replace("\\", "/");
        } else {
            // 文件系统逻辑
            result = Paths.get(currentDir).resolve(relativePath).normalize().toString();
        }
        return result;
    }

    public void pushPath(String filePath) {
        if (filePath.startsWith(CLASSPATH_PREFIX)) {
            int lastSlash = filePath.lastIndexOf('/');
            if (lastSlash > CLASSPATH_PREFIX.length()) {
                directoryStack.push(filePath.substring(0, lastSlash));
            } else {
                directoryStack.push(CLASSPATH_PREFIX + "/");
            }
        } else {
            directoryStack.push(Paths.get(filePath).getParent().toString());
        }
    }

    public void popPath() {
        if (directoryStack.size() > 1) directoryStack.pop();
    }

    private String normalizePath(String path) {
        if (path.startsWith(CLASSPATH_PREFIX)) return path;
        return Paths.get(path).toAbsolutePath().toString();
    }
}