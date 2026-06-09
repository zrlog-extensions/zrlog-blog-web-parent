package com.zrlog.test.web.controller;

import com.zrlog.blog.web.controller.page.ArticleController;
import com.zrlog.blog.web.controller.page.ArticleUriInfoVO;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

public class ArticleControllerTest {

    @Test
    public void shouldParseUriInfoWithPageSuffix() throws Exception {
        Method method = ArticleController.class.getDeclaredMethod("parseUriInfo", String.class);
        method.setAccessible(true);

        ArticleUriInfoVO info = (ArticleUriInfoVO) method.invoke(null, "/record/2015-06-3.html");
        assertEquals("2015-06", info.getKey());
        assertEquals(3L, info.getPage());
    }

    @Test
    public void shouldParseUriInfoWithoutPageSuffix() throws Exception {
        Method method = ArticleController.class.getDeclaredMethod("parseUriInfo", String.class);
        method.setAccessible(true);

        ArticleUriInfoVO info = (ArticleUriInfoVO) method.invoke(null, "/tag/java.html");
        assertEquals("java", info.getKey());
        assertEquals(1L, info.getPage());
    }

    @Test
    public void shouldParseSearchUriInfoWithPageSuffix() throws Exception {
        Method method = ArticleController.class.getDeclaredMethod("parseUriInfo", String.class);
        method.setAccessible(true);

        ArticleUriInfoVO info = (ArticleUriInfoVO) method.invoke(null, "/search/java-2.html");
        assertEquals("java", info.getKey());
        assertEquals(2L, info.getPage());
    }
}
