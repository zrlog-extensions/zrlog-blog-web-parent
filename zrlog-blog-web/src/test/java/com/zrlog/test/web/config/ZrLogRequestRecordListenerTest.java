package com.zrlog.test.web.config;

import com.hibegin.common.dao.DataSourceWrapper;
import com.hibegin.http.HttpMethod;
import com.hibegin.http.server.api.HttpRequest;
import com.zrlog.blog.web.config.ZrLogRequestRecordListener;
import com.zrlog.business.plugin.ArticleStatisticsPlugin;
import com.zrlog.business.plugin.RequestInfo;
import com.zrlog.common.Constants;
import com.zrlog.common.TokenService;
import com.zrlog.common.ZrLogConfig;
import com.zrlog.data.dto.ArticleDetailDTO;
import com.zrlog.plugin.IPlugin;
import com.zrlog.plugin.Plugins;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ZrLogRequestRecordListenerTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void shouldRecordHandledArticlePage() throws Exception {
        FakeArticleStatisticsPlugin plugin = new FakeArticleStatisticsPlugin();
        ArticleDetailDTO article = new ArticleDetailDTO();
        article.setLogId(99L);
        Map<String, Object> attrs = new HashMap<>();
        attrs.put("log", article);
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0");
        headers.put("X-Real-IP", "203.0.113.9");
        long createTime = System.currentTimeMillis() - 25L;

        withConfig(true, plugin, () -> new ZrLogRequestRecordListener()
                .onHandled(request(HttpMethod.GET, "/hello-world", attrs, headers, createTime), null));

        assertNotNull(plugin.lastRequestInfo);
        assertEquals(Long.valueOf(99L), plugin.lastRequestInfo.getArticleId());
        assertEquals("/hello-world", plugin.lastRequestInfo.getRequestUri());
        assertEquals("203.0.113.9", plugin.lastRequestInfo.getIp());
        assertEquals("/blog/", plugin.lastRequestInfo.getUrl());
        assertEquals("Mozilla/5.0", plugin.lastRequestInfo.getUserAgent());
        assertTrue(plugin.lastRequestInfo.getUsedTime() >= 0);
    }

    @Test
    public void shouldSkipNonArticleRequests() throws Exception {
        FakeArticleStatisticsPlugin plugin = new FakeArticleStatisticsPlugin();
        ArticleDetailDTO article = new ArticleDetailDTO();
        article.setLogId(99L);
        Map<String, Object> attrs = new HashMap<>();
        attrs.put("log", article);

        withConfig(false, plugin, () -> new ZrLogRequestRecordListener()
                .onHandled(request(HttpMethod.GET, "/hello-world", attrs, Map.of(), System.currentTimeMillis()), null));
        assertNull(plugin.lastRequestInfo);

        withConfig(true, plugin, () -> new ZrLogRequestRecordListener()
                .onHandled(request(HttpMethod.POST, "/hello-world", attrs, Map.of(), System.currentTimeMillis()), null));
        assertNull(plugin.lastRequestInfo);

        withConfig(true, plugin, () -> new ZrLogRequestRecordListener()
                .onHandled(request(HttpMethod.GET, "/assets/app.js", attrs, Map.of(), System.currentTimeMillis()), null));
        assertNull(plugin.lastRequestInfo);
    }

    private void withConfig(boolean installed, FakeArticleStatisticsPlugin plugin,
                            ThrowingRunnable runnable) throws Exception {
        ZrLogConfig previousConfig = Constants.zrLogConfig;
        String previousRootPath = System.getProperty("sws.root.path");
        try {
            System.setProperty("sws.root.path", temporaryFolder.newFolder().getAbsolutePath());
            Constants.zrLogConfig = new RecordZrLogConfig(installed, plugin);
            runnable.run();
        } finally {
            Constants.zrLogConfig = previousConfig;
            restoreProperty("sws.root.path", previousRootPath);
        }
    }

    private static HttpRequest request(HttpMethod method, String uri, Map<String, Object> attrs,
                                       Map<String, String> headers, long createTime) {
        return (HttpRequest) Proxy.newProxyInstance(
                ZrLogRequestRecordListenerTest.class.getClassLoader(),
                new Class[]{HttpRequest.class},
                (proxy, invokedMethod, args) -> {
                    if ("getMethod".equals(invokedMethod.getName())) {
                        return method;
                    }
                    if ("getUri".equals(invokedMethod.getName())) {
                        return uri;
                    }
                    if ("getAttr".equals(invokedMethod.getName())) {
                        return attrs;
                    }
                    if ("getCreateTime".equals(invokedMethod.getName())) {
                        return createTime;
                    }
                    if ("getContextPath".equals(invokedMethod.getName())) {
                        return "/blog";
                    }
                    if ("getHeader".equals(invokedMethod.getName())) {
                        return headers.get(args[0].toString());
                    }
                    if ("getHeaderMap".equals(invokedMethod.getName())) {
                        return headers;
                    }
                    if ("getRemoteHost".equals(invokedMethod.getName())) {
                        return "127.0.0.1";
                    }
                    if ("toString".equals(invokedMethod.getName())) {
                        return "HttpRequestProxy";
                    }
                    return null;
                });
    }

    private static void restoreProperty(String key, String value) {
        if (value == null) {
            System.clearProperty(key);
        } else {
            System.setProperty(key, value);
        }
    }

    private interface ThrowingRunnable {
        void run() throws Exception;
    }

    private static class RecordZrLogConfig extends ZrLogConfig {

        private final boolean installed;
        private final FakeArticleStatisticsPlugin plugin;

        RecordZrLogConfig(boolean installed, FakeArticleStatisticsPlugin plugin) {
            super(18080, null, "");
            this.installed = installed;
            this.plugin = plugin;
        }

        @Override
        public boolean isInstalled() {
            return installed;
        }

        @Override
        public DataSourceWrapper configDatabase() {
            return null;
        }

        @Override
        public <T extends IPlugin> T getPlugin(Class<T> pluginClass) {
            if (pluginClass.isInstance(plugin)) {
                return pluginClass.cast(plugin);
            }
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

    private static class FakeArticleStatisticsPlugin implements ArticleStatisticsPlugin {

        private RequestInfo lastRequestInfo;

        @Override
        public void record(RequestInfo requestInfo) {
            lastRequestInfo = requestInfo;
        }

        @Override
        public boolean start() {
            return true;
        }

        @Override
        public boolean isStarted() {
            return true;
        }

        @Override
        public boolean stop() {
            return true;
        }
    }
}
