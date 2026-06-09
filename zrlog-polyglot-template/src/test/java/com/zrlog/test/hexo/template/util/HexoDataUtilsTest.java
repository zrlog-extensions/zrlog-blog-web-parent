package com.zrlog.test.hexo.template.util;

import com.zrlog.blog.hexo.template.util.HexoDataUtils;
import com.zrlog.common.exception.NotImplementException;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyExecutable;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class HexoDataUtilsTest {

    @Test
    public void shouldWrapListAndExposeHelperFunctions() {
        Map<String, Object> wrapper = HexoDataUtils.wrap(List.of("a", "b"), 5);
        assertEquals(List.of("a", "b"), wrapper.get("data"));
        assertEquals(5, wrapper.get("length"));
        assertEquals(5, ((ProxyExecutable) wrapper.get("count")).execute());
        assertSame(wrapper, ((ProxyExecutable) wrapper.get("sort")).execute());
        assertEquals(List.of("a", "b"), ((ProxyExecutable) wrapper.get("toArray")).execute());
    }

    @Test
    public void shouldSupportEachAndForEachCallbacks() throws Exception {
        Map<String, Object> wrapper = HexoDataUtils.wrap(List.of("a", "b"));
        AtomicInteger count = new AtomicInteger();
        try (Context context = Context.newBuilder("js").allowAllAccess(true).build()) {
            context.getBindings("js").putMember("counter", (ProxyExecutable) args -> {
                count.incrementAndGet();
                return null;
            });
            Value callback = context.eval("js", "(item, index) => counter(item, index)");
            ((ProxyExecutable) wrapper.get("each")).execute(callback);
            ((ProxyExecutable) wrapper.get("forEach")).execute(callback);
        }
        assertEquals(4, count.get());
    }

    @Test
    public void shouldLimitAndWrapNullList() {
        Map<String, Object> limited = cast(((ProxyExecutable) HexoDataUtils.wrap(List.of(1, 2, 3)).get("limit")).execute(Value.asValue(2)));
        assertEquals(List.of(1, 2), limited.get("data"));

        Map<String, Object> empty = HexoDataUtils.wrap(null, 0);
        assertEquals(List.of(), empty.get("data"));
    }

    @Test
    public void shouldFilterAndFindByCallbackAndCriteria() {
        try (Context context = Context.newBuilder("js").allowAllAccess(true).build()) {
            List<Map<String, Object>> list = List.of(
                    Map.of("category", "Tech", "visible", true),
                    Map.of("category", "Life", "visible", false)
            );
            Map<String, Object> wrapper = HexoDataUtils.wrap(list);

            Value visibleFilter = context.eval("js", "(item) => item.visible");
            Map<String, Object> filtered = cast(((ProxyExecutable) wrapper.get("filter")).execute(visibleFilter));
            assertEquals(1, ((List<?>) filtered.get("data")).size());

            Value criteria = context.eval("js", "({ category: 'Life' })");
            Map<String, Object> found = cast(((ProxyExecutable) wrapper.get("find")).execute(criteria));
            assertEquals(1, ((List<?>) found.get("data")).size());

            Value missing = context.eval("js", "({ parent: { $exists: false } })");
            Map<String, Object> missingFiltered = cast(((ProxyExecutable) wrapper.get("filter")).execute(missing));
            assertEquals(2, ((List<?>) missingFiltered.get("data")).size());
        }
    }

    @Test
    public void shouldReturnNullWhenFindMissing() {
        try (Context context = Context.newBuilder("js").allowAllAccess(true).build()) {
            Map<String, Object> wrapper = HexoDataUtils.wrap(List.of(Map.of("id", 1)));
            Value criteria = context.eval("js", "({ id: 2 })");
            assertNull(((ProxyExecutable) wrapper.get("find")).execute(criteria));
        }
    }

    @Test
    public void shouldWrapArticleCollections() {
        Map<String, Object> raw = new java.util.HashMap<>();
        raw.put("tags", List.of("a"));
        raw.put("categories", List.of("b"));
        Map<String, Object> wrapped = HexoDataUtils.wrapArticle(raw);
        assertTrue(wrapped.get("tags") instanceof Map);
        assertTrue(wrapped.get("categories") instanceof Map);
    }

    @Test
    public void shouldThrowForUnsupportedCriteriaItem() {
        try (Context context = Context.newBuilder("js").allowAllAccess(true).build()) {
            Map<String, Object> wrapper = HexoDataUtils.wrap(List.of("plain"));
            Value criteria = context.eval("js", "({ id: 1 })");
            assertThrows(NotImplementException.class, () -> ((ProxyExecutable) wrapper.get("filter")).execute(criteria));
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> cast(Object value) {
        assertNotNull(value);
        return (Map<String, Object>) value;
    }
}
