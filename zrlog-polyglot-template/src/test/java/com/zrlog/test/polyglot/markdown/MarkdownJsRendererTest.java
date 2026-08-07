package com.zrlog.test.polyglot.markdown;

import com.zrlog.blog.polyglot.markdown.MarkdownJsRenderer;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MarkdownJsRendererTest {

    @Test
    public void shouldRenderGfmMarkdownWithFrontendCompatibleLineBreaks() {
        MarkdownJsRenderer renderer = new MarkdownJsRenderer();

        String html = renderer.render("# Title\n\nfirst line\nsecond line\n\n| A | B |\n| - | - |\n| 1 | 2 |");

        assertTrue(html.contains("<h1>Title</h1>"));
        assertTrue(html.contains("first line<br>second line"));
        assertTrue(html.contains("<table>"));
        assertTrue(html.contains("<td>2</td>"));
    }

    @Test
    public void shouldRenderNullMarkdownAsEmptyHtml() {
        assertEquals("", new MarkdownJsRenderer().render(null));
    }

    @Test
    public void shouldRenderConcurrentlyWithIsolatedContexts() throws Exception {
        MarkdownJsRenderer renderer = new MarkdownJsRenderer();
        ExecutorService executor = Executors.newFixedThreadPool(4);
        try {
            List<Future<String>> futures = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                int index = i;
                futures.add(executor.submit(() -> renderer.render("**item-" + index + "**")));
            }
            for (int i = 0; i < futures.size(); i++) {
                assertEquals("<p><strong>item-" + i + "</strong></p>\n", futures.get(i).get());
            }
        } finally {
            executor.shutdownNow();
        }
    }
}
