package com.zrlog.test.business.service;

import com.hibegin.common.dao.DataSourceWrapper;
import com.zrlog.blog.business.service.CommentService;
import com.zrlog.common.CacheService;
import com.zrlog.common.Constants;
import com.zrlog.common.TokenService;
import com.zrlog.common.ZrLogConfig;
import com.zrlog.common.cache.dto.TagDTO;
import com.zrlog.common.cache.dto.TypeDTO;
import com.zrlog.common.cache.dto.UserBasicDTO;
import com.zrlog.common.cache.vo.BaseDataInitVO;
import com.zrlog.common.vo.PublicWebSiteInfo;
import com.zrlog.data.dto.ArticleBasicDTO;
import com.zrlog.plugin.IPlugin;
import com.zrlog.plugin.Plugins;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CommentServiceTest {

    @Test
    public void shouldValidateEmailAddress() throws Exception {
        Method method = CommentService.class.getDeclaredMethod("isValidEmailAddress", String.class);
        method.setAccessible(true);

        assertTrue((Boolean) method.invoke(null, "user@example.com"));
        assertTrue((Boolean) method.invoke(null, "user.name+tag@example.co.uk"));
        assertTrue((Boolean) method.invoke(null, "user@[192.168.1.1]"));
        assertFalse((Boolean) method.invoke(null, "bad-email"));
        assertFalse((Boolean) method.invoke(null, "user@example"));
        assertFalse((Boolean) method.invoke(null, "@example.com"));
    }

    @Test
    public void shouldRespectGlobalAndArticleCommentSwitches() throws Exception {
        Method method = CommentService.class.getDeclaredMethod("isAllowComment", ArticleBasicDTO.class);
        method.setAccessible(true);
        CommentService commentService = new CommentService();

        withConfig(false, () -> {
            assertTrue((Boolean) method.invoke(commentService, article(true)));
            assertFalse((Boolean) method.invoke(commentService, article(false)));
            assertFalse((Boolean) method.invoke(commentService, article(null)));
        });
        withConfig(true, () -> assertFalse((Boolean) method.invoke(commentService, article(true))));
    }

    private static ArticleBasicDTO article(Boolean canComment) {
        ArticleBasicDTO article = new ArticleBasicDTO();
        article.setCanComment(canComment);
        return article;
    }

    private static void withConfig(boolean disableCommentStatus, ThrowingRunnable runnable) throws Exception {
        ZrLogConfig previousConfig = Constants.zrLogConfig;
        try {
            Constants.zrLogConfig = new TestZrLogConfig(disableCommentStatus);
            runnable.run();
        } finally {
            Constants.zrLogConfig = previousConfig;
        }
    }

    private interface ThrowingRunnable {
        void run() throws Exception;
    }

    private static class TestZrLogConfig extends ZrLogConfig {

        private final boolean disableCommentStatus;

        TestZrLogConfig(boolean disableCommentStatus) {
            super(18080, null, "");
            this.disableCommentStatus = disableCommentStatus;
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
            return new TestCacheService(disableCommentStatus);
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

        private final boolean disableCommentStatus;

        TestCacheService(boolean disableCommentStatus) {
            this.disableCommentStatus = disableCommentStatus;
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
        public BaseDataInitVO getInitData() {
            return new BaseDataInitVO();
        }

        @Override
        public BaseDataInitVO refreshInitData() {
            return getInitData();
        }

        @Override
        public PublicWebSiteInfo getPublicWebSiteInfo() {
            PublicWebSiteInfo publicWebSiteInfo = new PublicWebSiteInfo();
            publicWebSiteInfo.setDisable_comment_status(disableCommentStatus);
            return publicWebSiteInfo;
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
