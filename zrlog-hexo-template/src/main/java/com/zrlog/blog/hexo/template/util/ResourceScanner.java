package com.zrlog.blog.hexo.template.util;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ResourceScanner {
    private final ClassLoader classLoader;

    public ResourceScanner() {
        this.classLoader = Thread.currentThread().getContextClassLoader();
    }


    public List<String> listFiles(String themeRootPath) throws IOException {
        List<String> scriptFiles = new ArrayList<>();

        // 1. 寻找锚点文件（每个主题必有 _config.yml）
        themeRootPath = themeRootPath.replaceAll("classpath:/", "");
        String anchor = (themeRootPath + "_config.yml");
        URL anchorUrl = classLoader.getResource(anchor);

        if (anchorUrl == null) {
            System.err.println("错误：无法在 Classpath 中定位主题锚点文件: " + anchor);
            return scriptFiles;
        }

        String protocol = anchorUrl.getProtocol();
        String scriptsRelPath = themeRootPath + "/scripts";

        if ("file".equals(protocol)) {
            // IDE 环境：直接操作文件系统
            File configFile = new File(anchorUrl.getPath());
            File scriptsDir = new File(configFile.getParentFile(), "scripts");
            if (scriptsDir.exists() && scriptsDir.isDirectory()) {
                scanLocalDir(scriptsDir, scriptsRelPath, scriptFiles);
            }
        } else if ("jar".equals(protocol)) {
            // JAR 环境：解开 JAR 链接进行遍历
            scanJarDir(anchorUrl, scriptsRelPath, scriptFiles);
        }

        return scriptFiles;
    }

    private void scanLocalDir(File dir, String relPath, List<String> result) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                // 构造当前文件的相对路径，用于后续 Classpath 加载
                String currentRelPath = relPath + "/" + f.getName();

                if (f.isDirectory()) {
                    // 【核心：递归调用】进入子文件夹
                    scanLocalDir(f, currentRelPath, result);
                } else if (f.isFile() && f.getName().endsWith(".js")) {
                    // 如果是 JS 文件，加入结果集
                    result.add(currentRelPath);
                }
            }
        }
    }

    private void scanJarDir(URL anchorUrl, String scriptsRelPath, List<String> result) throws IOException {
        JarURLConnection conn = (JarURLConnection) anchorUrl.openConnection();
        try (JarFile jarFile = conn.getJarFile()) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                // 只要路径是以 theme/next/scripts/ 开头且是 .js 文件
                if (name.startsWith(scriptsRelPath + "/") && name.endsWith(".js")) {
                    result.add(name);
                }
            }
        }
    }

}
