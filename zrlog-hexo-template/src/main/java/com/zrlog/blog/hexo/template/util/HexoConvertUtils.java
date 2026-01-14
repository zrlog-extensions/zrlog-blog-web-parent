package com.zrlog.blog.hexo.template.util;

import com.zrlog.blog.web.template.vo.ArticleDetailPageVO;

import java.util.Map;

public class HexoConvertUtils {

    public static Map<String, Object> getPrevLog(ArticleDetailPageVO articleDetailPageVO) {
        return Map.of("title", articleDetailPageVO.getLog().getLastLog().getTitle(), "path", articleDetailPageVO.getLog().getLastLog().getUrl());
    }

    public static Map<String, Object> getNextLog(ArticleDetailPageVO articleDetailPageVO) {
        return Map.of("title", articleDetailPageVO.getLog().getNextLog().getTitle(), "path", articleDetailPageVO.getLog().getNextLog().getUrl());
    }
}
