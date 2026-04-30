package com.zrlog.blog.polyglot.util;

import com.hibegin.common.util.LoggerUtil;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;

import java.util.Objects;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class PolyglotContextUtils {

    private static final Logger LOGGER = LoggerUtil.getLogger(PolyglotContextUtils.class);

    public static Context buildJsContext() {
        Context.Builder builder = Context.newBuilder("js")
                .allowHostAccess(HostAccess.ALL)
                .allowExperimentalOptions(true)
                .allowHostClassLookup(s -> true)
                .option("engine.WarnVirtualThreadSupport", "false");
        Handler[] handlers = LOGGER.getHandlers();
        if (Objects.nonNull(handlers) && handlers.length > 0) {
            builder.logHandler(handlers[0]);
        }
        return builder.build();
    }
}
