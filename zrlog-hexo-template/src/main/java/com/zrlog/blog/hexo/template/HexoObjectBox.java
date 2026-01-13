package com.zrlog.blog.hexo.template;

import org.graalvm.polyglot.Context;

import java.util.List;

public interface HexoObjectBox {
    void setup(Context context);

    List<String> getInjectionPoints(String partal);
}
