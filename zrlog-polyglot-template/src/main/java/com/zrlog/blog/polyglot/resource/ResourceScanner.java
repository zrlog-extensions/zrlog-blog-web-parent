package com.zrlog.blog.polyglot.resource;

import com.zrlog.business.template.util.BlogResourceUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ResourceScanner {

    private final String themeRootPath;

    public ResourceScanner(String themeRootPath) {
        this.themeRootPath = themeRootPath;
    }

    public List<String> listFiles(String childPath) {
        List<String> scriptFiles = new ArrayList<>();
        if (themeRootPath.startsWith("classpath:")) {
            scriptFiles.addAll(BlogResourceUtils.searchResources(themeRootPath.replaceAll("classpath:/", "") + "/" + childPath).stream().map(e -> "classpath:/" + e).toList());
        } else {
            // IDE 环境：直接操作文件系统
            File scriptsDir = new File(themeRootPath, childPath);
            if (scriptsDir.exists() && scriptsDir.isDirectory()) {
                scanLocalDir(scriptsDir, scriptFiles);
            }
        }

        return scriptFiles;
    }

    private void scanLocalDir(File dir, List<String> result) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                // 构造当前文件的相对路径，用于后续 Classpath 加载
                if (f.isDirectory()) {
                    // 【核心：递归调用】进入子文件夹
                    scanLocalDir(f, result);
                } else if (f.isFile()) {
                    // 如果是 JS 文件，加入结果集
                    result.add(f.toString());
                }
            }
        }
    }


}
