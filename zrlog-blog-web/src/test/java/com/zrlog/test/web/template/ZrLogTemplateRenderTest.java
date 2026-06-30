package com.zrlog.test.web.template;

import com.hibegin.common.dao.DataSourceWrapper;
import com.hibegin.http.HttpMethod;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.config.RequestConfig;
import com.hibegin.http.server.config.ServerConfig;
import com.zrlog.blog.web.template.ZrLogTemplateRender;
import com.zrlog.common.CacheService;
import com.zrlog.common.Constants;
import com.zrlog.common.TokenService;
import com.zrlog.common.ZrLogConfig;
import com.zrlog.common.cache.dto.TagDTO;
import com.zrlog.common.cache.dto.TypeDTO;
import com.zrlog.common.cache.dto.UserBasicDTO;
import com.zrlog.common.cache.vo.BaseDataInitVO;
import com.zrlog.common.vo.PublicWebSiteInfo;
import com.zrlog.plugin.BaseStaticSitePlugin;
import com.zrlog.plugin.IPlugin;
import com.zrlog.plugin.Plugins;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ZrLogTemplateRenderTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void shouldNotCacheStaticPluginRequestsAsGeneratedHtml() throws Exception {
        withConfig(true, () -> assertFalse(canGeneratorHtml(
                request("/article.html", BaseStaticSitePlugin.STATIC_USER_AGENT))));
    }

    @Test
    public void shouldNotCacheHtmlWhenStaticGenerationIsDisabled() throws Exception {
        withConfig(false, () -> assertFalse(canGeneratorHtml(request("/article.html", null))));
    }

    @Test
    public void shouldOnlyCacheHtmlRequestsWhenStaticGenerationIsEnabled() throws Exception {
        withConfig(true, () -> {
            assertTrue(canGeneratorHtml(request("/article.html", null)));
            assertFalse(canGeneratorHtml(request("/assets/app.css", null)));
        });
    }

    @Test
    public void shouldInitializeDefaultTemplateAndCheckTemplateFileExistence() throws Exception {
        withConfig(false, () -> {
            ZrLogTemplateRender render = new ZrLogTemplateRender(templateRequest("/missing.html"));

            assertTrue(render.existsByTemplateName("index"));
            assertFalse(render.existsByTemplateName("missing-template-file"));
        });
    }

    @Test
    public void shouldRejectDirectRenderMethods() throws Exception {
        withConfig(false, () -> {
            ZrLogTemplateRender render = new ZrLogTemplateRender(templateRequest("/missing.html"));

            assertThrows(RuntimeException.class, () -> render.render("index"));
            assertThrows(RuntimeException.class, () -> render.render(new ByteArrayInputStream(
                    "index".getBytes(StandardCharsets.UTF_8))));
        });
    }

    @Test
    public void shouldRenderDefaultTemplateAndTransformHtml() throws Exception {
        withConfig(false, () -> {
            ZrLogTemplateRender render = new ZrLogTemplateRender(templateRequest("/empty.html"));

            String html = render.renderByTemplateName("empty");

            assertNotNull(html);
            assertTrue(html.contains("graalvm native image build"));
            assertTrue(html.contains("<!--"));
        });
    }

    private void withConfig(boolean generatorHtmlStatus, ThrowingRunnable runnable) throws Exception {
        ZrLogConfig previousConfig = Constants.zrLogConfig;
        String previousRootPath = System.getProperty("sws.root.path");
        try {
            System.setProperty("sws.root.path", temporaryFolder.newFolder().getAbsolutePath());
            Constants.zrLogConfig = new TestZrLogConfig(generatorHtmlStatus);
            runnable.run();
        } finally {
            Constants.zrLogConfig = previousConfig;
            restoreProperty("sws.root.path", previousRootPath);
        }
    }

    private static boolean canGeneratorHtml(HttpRequest request) throws Exception {
        Method method = ZrLogTemplateRender.class.getDeclaredMethod("catGeneratorHtml", HttpRequest.class);
        method.setAccessible(true);
        return (Boolean) method.invoke(null, request);
    }

    private static HttpRequest request(String uri, String userAgent) {
        return (HttpRequest) Proxy.newProxyInstance(
                ZrLogTemplateRenderTest.class.getClassLoader(),
                new Class[]{HttpRequest.class},
                (proxy, method, args) -> {
                    if ("getUri".equals(method.getName())) {
                        return uri;
                    }
                    if ("getHeader".equals(method.getName()) && "User-Agent".equals(args[0])) {
                        return userAgent;
                    }
                    if ("toString".equals(method.getName())) {
                        return "HttpRequestProxy";
                    }
                    return null;
                });
    }

    private static HttpRequest templateRequest(String uri) {
        Map<String, Object> attrs = new HashMap<>();
        RequestConfig requestConfig = new RequestConfig();
        ServerConfig serverConfig = new ServerConfig();
        return (HttpRequest) Proxy.newProxyInstance(
                ZrLogTemplateRenderTest.class.getClassLoader(),
                new Class[]{HttpRequest.class},
                (proxy, method, args) -> {
                    if ("getUri".equals(method.getName())) {
                        return uri;
                    }
                    if ("getUrl".equals(method.getName())) {
                        return uri;
                    }
                    if ("getFullUrl".equals(method.getName())) {
                        return "https://blog.example.com" + uri;
                    }
                    if ("getQueryStr".equals(method.getName())) {
                        return "";
                    }
                    if ("getContextPath".equals(method.getName())) {
                        return "";
                    }
                    if ("getCreateTime".equals(method.getName())) {
                        return 100L;
                    }
                    if ("getMethod".equals(method.getName())) {
                        return HttpMethod.GET;
                    }
                    if ("getCookies".equals(method.getName())) {
                        return null;
                    }
                    if ("getAttr".equals(method.getName())) {
                        return attrs;
                    }
                    if ("getHeader".equals(method.getName()) && "Host".equals(args[0])) {
                        return "blog.example.com";
                    }
                    if ("getHeaderMap".equals(method.getName())) {
                        return Collections.singletonMap("Host", "blog.example.com");
                    }
                    if ("getRequestConfig".equals(method.getName())) {
                        return requestConfig;
                    }
                    if ("getServerConfig".equals(method.getName())) {
                        return serverConfig;
                    }
                    if ("toString".equals(method.getName())) {
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

    private static class TestZrLogConfig extends ZrLogConfig {

        private final boolean generatorHtmlStatus;

        TestZrLogConfig(boolean generatorHtmlStatus) {
            super(18080, null, "");
            this.generatorHtmlStatus = generatorHtmlStatus;
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
        public CacheService getCacheService() {
            return new TestCacheService(generatorHtmlStatus);
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

    private static class TestCacheService implements CacheService {

        private final boolean generatorHtmlStatus;

        TestCacheService(boolean generatorHtmlStatus) {
            this.generatorHtmlStatus = generatorHtmlStatus;
        }

        @Override
        public long getCurrentSqlVersion() {
            return 0;
        }

        @Override
        public long getWebSiteVersion() {
            return 0;
        }

        @Override
        public PublicWebSiteInfo getPublicWebSiteInfo() {
            PublicWebSiteInfo publicWebSiteInfo = new PublicWebSiteInfo();
            publicWebSiteInfo.setGenerator_html_status(generatorHtmlStatus);
            publicWebSiteInfo.setTemplate(Constants.DEFAULT_TEMPLATE_PATH);
            publicWebSiteInfo.setLanguage(Constants.DEFAULT_LANGUAGE);
            publicWebSiteInfo.setTitle("ZrLog");
            publicWebSiteInfo.setDescription("blog");
            publicWebSiteInfo.setStaticResourceHost("");
            publicWebSiteInfo.setHost("blog.example.com");
            return publicWebSiteInfo;
        }

        @Override
        public BaseDataInitVO getInitData() {
            BaseDataInitVO initData = new BaseDataInitVO();
            initData.setWebSite(getPublicWebSiteInfo());
            return initData;
        }

        @Override
        public BaseDataInitVO refreshInitData() {
            return getInitData();
        }

        @Override
        public List<TypeDTO> getArticleTypes() {
            return Collections.emptyList();
        }

        @Override
        public List<TagDTO> getTags() {
            return Collections.emptyList();
        }

        @Override
        public UserBasicDTO getUserInfoById(Long userId) {
            return null;
        }

        @Override
        public Map<String, Object> getTemplateConfigMapWithCache(String template) {
            return Collections.emptyMap();
        }
    }
}
