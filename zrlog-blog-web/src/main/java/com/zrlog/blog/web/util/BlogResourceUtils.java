package com.zrlog.blog.web.util;

import com.hibegin.common.util.IOUtil;
import com.zrlog.blog.web.BlogWebSetup;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BlogResourceUtils {

    public static List<String> getResources() {
        return Arrays.asList(IOUtil.getStringInputStream(BlogWebSetup.class.getResourceAsStream("/resource.txt")).split("\n"));
    }

    public static List<String> searchResources(String prefix) {
        return getResources().stream().filter(e -> e.startsWith(prefix)).collect(Collectors.toList());
    }
}
