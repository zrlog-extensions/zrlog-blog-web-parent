package com.zrlog.test.business.service;

import com.zrlog.blog.business.service.CommentService;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CommentServiceTest {

    @Test
    public void shouldValidateEmailAddress() throws Exception {
        Method method = CommentService.class.getDeclaredMethod("isValidEmailAddress", String.class);
        method.setAccessible(true);

        assertTrue((Boolean) method.invoke(null, "user@example.com"));
        assertFalse((Boolean) method.invoke(null, "bad-email"));
    }
}
