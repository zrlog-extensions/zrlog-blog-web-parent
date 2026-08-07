package com.zrlog.blog.polyglot.markdown;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.PolyglotAccess;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.io.IOAccess;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class MarkdownJsRenderer {

    public static final String MARKED_VERSION = "16.0.0";

    private static final String MARKED_RESOURCE = "/conf/base/scripts/marked.umd.js";
    private static final Engine ENGINE = Engine.newBuilder()
            .option("engine.WarnInterpreterOnly", "false")
            .build();
    private static final Source MARKED_SOURCE = loadMarkedSource();
    private static final Source RENDER_SOURCE = Source.create("js",
            "markdown => globalThis.marked.parse(markdown, {gfm: true, breaks: true})");

    public String render(String markdown) {
        try (Context context = buildContext()) {
            context.eval(MARKED_SOURCE);
            Value render = context.eval(RENDER_SOURCE);
            return render.execute(markdown == null ? "" : markdown).asString();
        }
    }

    private static Context buildContext() {
        return Context.newBuilder("js")
                .engine(ENGINE)
                .allowHostAccess(HostAccess.NONE)
                .allowHostClassLookup(className -> false)
                .allowPolyglotAccess(PolyglotAccess.NONE)
                .allowIO(IOAccess.NONE)
                .allowCreateThread(false)
                .allowNativeAccess(false)
                .build();
    }

    private static Source loadMarkedSource() {
        try (InputStream inputStream = MarkdownJsRenderer.class.getResourceAsStream(MARKED_RESOURCE)) {
            if (inputStream == null) {
                throw new IllegalStateException("Missing bundled marked script: " + MARKED_RESOURCE);
            }
            String script = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            return Source.newBuilder("js", script, "marked-" + MARKED_VERSION + ".umd.js").buildLiteral();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load bundled marked script", e);
        }
    }
}
