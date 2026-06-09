package com.zrlog.test.polyglot.resource;

import com.zrlog.blog.polyglot.resource.ScriptProvider;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ScriptProviderTest {

    @Test
    public void shouldLoadBaseLibraryScript() throws Exception {
        ScriptProvider scriptProvider = ScriptProvider.getInstance();
        scriptProvider.addBaseScriptByPath("path", "base/scripts/path.js");
        assertTrue(scriptProvider.exists("path"));
        assertFalse(scriptProvider.load("path").isEmpty());
        assertFalse(scriptProvider.load("../path").isEmpty());
    }
}
