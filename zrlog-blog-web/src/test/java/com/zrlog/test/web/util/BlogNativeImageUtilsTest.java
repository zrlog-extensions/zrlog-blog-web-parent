package com.zrlog.test.web.util;

import com.hibegin.common.dao.DataSourceWrapper;
import com.zrlog.blog.web.util.BlogNativeImageUtils;
import com.zrlog.common.TokenService;
import com.zrlog.common.ZrLogConfig;
import com.zrlog.plugin.IPlugin;
import com.zrlog.plugin.Plugins;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BlogNativeImageUtilsTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void shouldRegisterBlogJsonTypes() {
        BlogNativeImageUtils.nativeJson();
    }

    @Test
    public void shouldCompleteBlogNativeRegistrationWithConfiguredServer() throws Exception {
        String previousRootPath = System.getProperty("sws.root.path");
        String previousPolyglotWarning = System.getProperty("polyglot.engine.WarnInterpreterOnly");
        Logger rootLogger = Logger.getLogger("");
        Level previousRootLevel = rootLogger.getLevel();
        Handler[] handlers = rootLogger.getHandlers();
        Level[] previousHandlerLevels = new Level[handlers.length];
        try {
            System.setProperty("sws.root.path", temporaryFolder.newFolder("zrlog-blog-native").getAbsolutePath());
            System.setProperty("polyglot.engine.WarnInterpreterOnly", "false");
            rootLogger.setLevel(Level.OFF);
            for (int i = 0; i < handlers.length; i++) {
                previousHandlerLevels[i] = handlers[i].getLevel();
                handlers[i].setLevel(Level.OFF);
            }
            BlogNativeImageUtils.reg(new TestZrLogConfig());
        } finally {
            rootLogger.setLevel(previousRootLevel);
            for (int i = 0; i < handlers.length; i++) {
                handlers[i].setLevel(previousHandlerLevels[i]);
            }
            if (previousRootPath == null) {
                System.clearProperty("sws.root.path");
            } else {
                System.setProperty("sws.root.path", previousRootPath);
            }
            if (previousPolyglotWarning == null) {
                System.clearProperty("polyglot.engine.WarnInterpreterOnly");
            } else {
                System.setProperty("polyglot.engine.WarnInterpreterOnly", previousPolyglotWarning);
            }
        }
    }

    private static class TestZrLogConfig extends ZrLogConfig {

        TestZrLogConfig() {
            super(18080, null, "");
        }

        @Override
        public boolean isInstalled() {
            return false;
        }

        @Override
        public DataSourceWrapper configDatabase() {
            return null;
        }

        @Override
        protected TokenService initTokenService() {
            return null;
        }

        @Override
        public List<IPlugin> getBasePluginList() {
            return new Plugins();
        }
    }
}
