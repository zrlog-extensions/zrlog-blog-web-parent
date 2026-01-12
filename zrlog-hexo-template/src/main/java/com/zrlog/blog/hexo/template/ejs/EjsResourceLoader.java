package com.zrlog.blog.hexo.template.ejs;

import java.io.IOException;

public class EjsResourceLoader {
    public static String read(String fullPath) {
        try {
            return new String(EjsResourceLoader.class.getResourceAsStream(fullPath).readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException("无法读取模板文件: " + fullPath, e);
        }
    }
}