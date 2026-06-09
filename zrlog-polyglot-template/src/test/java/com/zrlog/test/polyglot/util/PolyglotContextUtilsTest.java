package com.zrlog.test.polyglot.util;

import com.zrlog.blog.polyglot.util.PolyglotContextUtils;
import org.graalvm.polyglot.Context;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PolyglotContextUtilsTest {

    @Test
    public void shouldBuildWorkingJsContext() {
        try (Context context = PolyglotContextUtils.buildJsContext()) {
            assertEquals(3, context.eval("js", "1 + 2").asInt());
        }
    }
}
