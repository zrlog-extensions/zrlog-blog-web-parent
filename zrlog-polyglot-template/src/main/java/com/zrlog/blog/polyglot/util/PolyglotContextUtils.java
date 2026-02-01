package com.zrlog.blog.polyglot.util;

import com.hibegin.common.util.LoggerUtil;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;

import java.util.logging.Logger;

public class PolyglotContextUtils {

    private static final Logger LOGGER = LoggerUtil.getLogger(PolyglotContextUtils.class);

    public static Context buildJsContext() {
        return Context.newBuilder("js")
                .allowHostAccess(HostAccess.ALL)
                .allowExperimentalOptions(true)
                .allowHostClassLookup(s -> true)
                .logHandler(LOGGER.getHandlers()[0])
                .option("engine.WarnVirtualThreadSupport", "false")
                .build();
    }
}
