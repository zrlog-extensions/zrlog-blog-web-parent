package com.zrlog.blog.polyglot;

import org.graalvm.polyglot.Context;

import java.util.Map;

public interface JsTemplateRender {


    Context getContext();

    String getTemplateExt();

    String render(String page, Map<String, Object> data);
}
