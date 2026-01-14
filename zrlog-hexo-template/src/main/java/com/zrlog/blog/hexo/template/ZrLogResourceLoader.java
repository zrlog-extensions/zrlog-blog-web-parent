package com.zrlog.blog.hexo.template;

import com.hibegin.common.util.IOUtil;

import java.io.File;

public class ZrLogResourceLoader {
    public static String read(String fullPath) {
        try {
            if (fullPath.startsWith("classpath:")) {
                return new String(ZrLogResourceLoader.class.getResourceAsStream(fullPath.split("classpath:")[1]).readAllBytes());
            }
            return new String(IOUtil.getByteByFile(new File(fullPath)));
        } catch (Exception e) {
            throw new RuntimeException("无法读取模板文件: " + fullPath, e);
        }
    }
}