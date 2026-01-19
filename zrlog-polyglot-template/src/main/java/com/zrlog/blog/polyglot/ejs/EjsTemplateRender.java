package com.zrlog.blog.polyglot.ejs;

import com.hibegin.common.util.LoggerUtil;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.blog.polyglot.JsTemplateRender;
import com.zrlog.blog.polyglot.resource.ZrLogResourceLoader;
import com.zrlog.blog.polyglot.util.GraalDataUtils;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

public class EjsTemplateRender implements JsTemplateRender {

    private static final Logger LOGGER = LoggerUtil.getLogger(EjsTemplateRender.class);

    private final Context context;
    private final Value jsBindings;
    private final Value ejs;
    private final String templateExt = ".ejs";
    private final String template;

    public EjsTemplateRender(String template) {
        this.template = template;
        this.context = Context.newBuilder("js")
                .allowHostAccess(HostAccess.ALL)
                .allowExperimentalOptions(true)
                .allowHostClassLookup(s -> true)
                .logHandler(LOGGER.getHandlers()[0])
                .option("engine.WarnVirtualThreadSupport", "false")
                .build();
        try {
            context.eval("js", "var global = globalThis;");
            context.eval("js", new String(PathUtil.getConfInputStream("base/scripts/ejs.min.js").readAllBytes()));
            context.eval("js", new String(PathUtil.getConfInputStream("base/scripts/hooks.js").readAllBytes()));
            context.eval("js", new String(PathUtil.getConfInputStream("base/scripts/require.js").readAllBytes()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.jsBindings = context.getBindings("js");
        this.ejs = jsBindings.getMember("ejs");
        if (ejs == null || ejs.getMember("render").isNull()) {
            throw new RuntimeException("EJS 引擎未初始化，请先加载 ejs.min.js");
        }

    }

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public String getTemplateExt() {
        return templateExt;
    }

    @Override
    public String render(String page, Map<String, Object> locals) {
        for (Map.Entry<String, Object> entry : locals.entrySet()) {
            jsBindings.putMember(entry.getKey(), GraalDataUtils.makeJsFriendly(entry.getValue()));
        }
        Object jsFriendlyLocals = GraalDataUtils.makeJsFriendly(locals);
        String path = (template + "/" + page + (page.endsWith(templateExt) ? "" : templateExt)).replaceAll("//", "/");
        Value options = context.eval("js", "({ async: false, cache: false })");
        String read = ZrLogResourceLoader.read(path);
        Value result = ejs.getMember("render").execute(read, jsFriendlyLocals, options);
        return result.asString();
    }
}
