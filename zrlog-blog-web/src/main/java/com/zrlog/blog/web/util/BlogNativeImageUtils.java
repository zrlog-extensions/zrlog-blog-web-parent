package com.zrlog.blog.web.util;

import com.hibegin.common.util.LoggerUtil;
import com.hibegin.common.util.StringUtils;
import com.hibegin.http.HttpMethod;
import com.hibegin.http.server.ApplicationContext;
import com.hibegin.http.server.util.FreeMarkerUtil;
import com.hibegin.http.server.util.HttpRequestBuilder;
import com.hibegin.http.server.util.NativeImageUtils;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.blog.hexo.template.support.fluid.InjectionStorage;
import com.zrlog.blog.hexo.template.util.HexoDateObjUtils;
import com.zrlog.blog.polyglot.resource.ScriptProvider;
import com.zrlog.blog.web.BlogWebSetup;
import com.zrlog.blog.web.template.PagerVO;
import com.zrlog.blog.web.template.vo.ArticleDetailPageVO;
import com.zrlog.blog.web.template.vo.ArticleListPageVO;
import com.zrlog.blog.web.template.vo.BasePageInfo;
import com.zrlog.business.template.util.BlogResourceUtils;
import com.zrlog.common.Constants;
import com.zrlog.common.ZrLogConfig;
import com.zrlog.data.dto.ArticleBasicDTO;
import com.zrlog.data.dto.ArticleDetailDTO;
import com.zrlog.data.dto.VisitorCommentDTO;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

public class BlogNativeImageUtils {

    public static void reg(ZrLogConfig zrLogConfig) {
        try {
            Method add = InjectionStorage.class.getMethod("add", String.class, String.class);
            add.invoke(new InjectionStorage(null, null), Constants.DEFAULT_TEMPLATE_PATH, Constants.DEFAULT_TEMPLATE_PATH);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        try {
            Method load = ScriptProvider.class.getMethod("load", String.class);
            load.invoke(new ScriptProvider(), "path");
        } catch (Throwable e) {
            e.printStackTrace();
        }
        try {
            HexoDateObjUtils.getInstance();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        nativeJson();
        List<String> resources = BlogResourceUtils.getInstance().getResources();
        NativeImageUtils.doResourceLoadByResourceNames(resources.stream().filter(StringUtils::isNotEmpty).map(e -> "/" + e).collect(Collectors.toList()));

        try {
            FreeMarkerUtil.init(PathUtil.getStaticFile(Constants.DEFAULT_TEMPLATE_PATH).getPath());
        } catch (Exception e) {
            LoggerUtil.getLogger(BlogWebSetup.class).info("Freemarker init error " + e.getMessage());
        }
        try {
            FreeMarkerUtil.initClassTemplate(Constants.DEFAULT_TEMPLATE_PATH);
        } catch (Exception e) {
            LoggerUtil.getLogger(BlogWebSetup.class).info("Freemarker init error " + e.getMessage());
        }
        try {
            ApplicationContext applicationContext = new ApplicationContext(zrLogConfig.getServerConfig());
            applicationContext.init();
            FreeMarkerUtil.renderToFM("empty", HttpRequestBuilder.buildRequest(HttpMethod.GET, "/", "",
                    "", zrLogConfig.getRequestConfig(),
                    applicationContext));
        } catch (Exception e) {
            LoggerUtil.getLogger(BlogWebSetup.class).info("Freemarker render error " + e.getMessage());
        }

    }


    public static void nativeJson() {
        //freemarker need
        regWithGetMethod(ArticleDetailPageVO.class, ArticleListPageVO.class,
                BasePageInfo.class, ArticleBasicDTO.class, ArticleDetailDTO.class,
                ArticleDetailDTO.LastLogDTO.class, ArticleDetailDTO.NextLogDTO.class,
                ArticleDetailDTO.TagsDTO.class, PagerVO.PageEntry.class, PagerVO.class, PagerVO.PageEntry.class, VisitorCommentDTO.class);
    }

    private static void regWithGetMethod(Class<?>... objects) {
        NativeImageUtils.gsonNativeAgentByClazz(List.of(objects));
        for (Class<?> o : objects) {
            NativeImageUtils.regGetMethodByClassName(o);
        }
    }

    public static void main(String[] args) {
        try {
            Method add = InjectionStorage.class.getMethod("add", String.class, String.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
