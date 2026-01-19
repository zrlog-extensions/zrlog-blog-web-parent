package com.zrlog.blog.hexo.template.fluid;

import com.hibegin.common.util.EnvKit;
import com.hibegin.common.util.LoggerUtil;
import org.graalvm.polyglot.HostAccess;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

public class InjectionStorage {
    private static final Logger LOGGER = LoggerUtil.getLogger(InjectionStorage.class);
    private final Map<String, List<String>> injectionPoints;
    private final String themeDir;

    public InjectionStorage(Map<String, List<String>> injectionPoints, String themeDir) {
        this.injectionPoints = injectionPoints;
        this.themeDir = themeDir;
    }

    // 必须是 public，且建议显式允许访问
    @HostAccess.Export
    public void add(String slot, String path) {
        if (Objects.isNull(themeDir)) {
            return;
        }
        String rPath = path.substring(this.themeDir.length()).replace("/layout/", "");
        if (EnvKit.isDevMode()) {
            LOGGER.info("成功捕获注入: [" + slot + "] -> " + rPath);
        }
        injectionPoints.computeIfAbsent(slot, k -> new ArrayList<>()).add(rPath);
    }

    public List<String> get(String pointName) {
        return injectionPoints.get(pointName);
    }
}