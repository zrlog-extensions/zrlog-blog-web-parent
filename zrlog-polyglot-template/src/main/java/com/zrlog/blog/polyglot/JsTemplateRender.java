package com.zrlog.blog.polyglot;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import java.util.Map;

public interface JsTemplateRender {


    Value getJsBindings();

    Context getContext();

    String getTemplateExt();

    String render(String page, Map<String, Object> data);

    String includeRender(String page, Map<String, Object> data);

    String getTemplate();

    Map<String, Object> getLocals();
}
