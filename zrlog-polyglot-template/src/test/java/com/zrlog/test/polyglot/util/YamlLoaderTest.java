package com.zrlog.test.polyglot.util;

import com.zrlog.blog.polyglot.util.YamlLoader;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class YamlLoaderTest {

    @Test
    public void shouldLoadYamlAndReadNestedValue() {
        Map<String, Object> config = YamlLoader.loadConfig("menu:\n  home:\n    title: Home\n");
        assertEquals("Home", YamlLoader.getNestedValue(config, "menu.home.title"));
        assertNull(YamlLoader.getNestedValue(config, "menu.archive.title"));
    }
}
