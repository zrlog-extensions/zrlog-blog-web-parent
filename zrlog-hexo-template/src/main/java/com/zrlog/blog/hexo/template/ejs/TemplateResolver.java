package com.zrlog.blog.hexo.template.ejs;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Stack;

public class TemplateResolver {
    private final Stack<Path> directoryStack = new Stack<>();
    private final Path baseDir;

    public TemplateResolver(String baseDir) {
        this.baseDir = Paths.get(baseDir).toAbsolutePath();
        this.directoryStack.push(this.baseDir);
    }

    public String resolve(String relativePath) {
        if (!relativePath.endsWith(".ejs")) relativePath += ".ejs";
        
        Path currentDir = directoryStack.peek();
        Path resolvedPath = currentDir.resolve(relativePath).normalize();
        
        return resolvedPath.toString();
    }

    public void pushPath(String filePath) {
        directoryStack.push(Paths.get(filePath).getParent());
    }

    public void popPath() {
        if (directoryStack.size() > 1) directoryStack.pop();
    }
}