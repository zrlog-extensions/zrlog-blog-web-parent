package com.zrlog.blog.hexo.template.util;

import org.graalvm.polyglot.Value;

public interface HexoHooks {

    void inject(Value bindings);
}
