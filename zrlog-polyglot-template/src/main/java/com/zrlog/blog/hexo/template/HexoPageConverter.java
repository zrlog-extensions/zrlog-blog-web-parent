package com.zrlog.blog.hexo.template;

import com.hibegin.common.dao.dto.PageData;
import com.zrlog.blog.hexo.template.util.HexoConvertUtils;
import com.zrlog.blog.web.template.vo.ArticleDetailPageVO;
import com.zrlog.blog.web.template.vo.ArticleListPageVO;
import com.zrlog.blog.web.template.vo.BasePageInfo;
import com.zrlog.common.cache.dto.LinkDTO;
import com.zrlog.common.cache.dto.LogNavDTO;
import com.zrlog.data.dto.ArticleBasicDTO;
import com.zrlog.data.dto.ArticleDetailDTO;

import java.util.*;
import java.util.stream.Collectors;

public class HexoPageConverter {

    public static Map<String, Object> toThemeMap(BasePageInfo pageInfo, String layout, Map<String, Object> config) {
        Map<String, Object> theme = new HashMap<>(config);
        theme.put("title", pageInfo.getWebs().getTitle());
        Map<String, Object> page = new HashMap<>();
        if (Objects.nonNull(layout)) {
            if (layout.equals("detail")) {
                page.put("layout", "/post");
            } else if (layout.equals("archives")) {
                page.put("layout", "/archive");
            } else {
                page.put("layout", "/" + layout);
            }
        }

        if (layout.equals("detail")) {
            Map<String, Object> row = new HashMap<>();
            ArticleDetailDTO log = ((ArticleDetailPageVO) pageInfo).getLog();
            row.put("title", log.getTitle());
            row.put("articleId", log.getId());
            page.put("post", row);
            page.put("title", log.getTitle());
            page.put("content", log.getContent());
            page.put("date", new HexoDateWrapper(log.getReleaseTime()));
            page.put("next_post", HexoConvertUtils.getNextLog((ArticleDetailPageVO) pageInfo));
            page.put("prev_post", HexoConvertUtils.getPrevLog((ArticleDetailPageVO) pageInfo));
            page.put("permalink", ((ArticleDetailPageVO) pageInfo).getLog().getNoSchemeUrl());
            if (Objects.nonNull(log.getComments())) {
                page.put("comments", log.getComments());
            } else {
                page.put("comments", new ArrayList<>());
            }
            page.put("posts", new ArrayList<>());
        } else if (pageInfo instanceof ArticleListPageVO) {
            PageData<ArticleBasicDTO> data = ((ArticleListPageVO) pageInfo).getData();
            List<Map<String, Object>> list = new ArrayList<>();
            if (Objects.nonNull(data)) {
                List<ArticleBasicDTO> rows = data.getRows();
                for (ArticleBasicDTO articleBasicDTO : rows) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("title", articleBasicDTO.getTitle());
                    List<Map<String, Object>> categories = new ArrayList<>();
                    Map<String, Object> cat = new HashMap<>();
                    cat.put("_id", articleBasicDTO.getTypeId());
                    cat.put("name", articleBasicDTO.getTypeName()); // 注意：EJS 有 .trim()，所以前后空格没关系
                    cat.put("path", articleBasicDTO.getTypeUrl());
                    cat.put("parent", null);   // 顶级分类传 null
                    pageInfo.getInit().getTypes().stream().filter(e -> e.getTypeName().equals(articleBasicDTO.getTypeName())).findFirst().ifPresent(type -> {
                        cat.put("length", type.getTypeamount());
                    });
                    categories.add(cat);
                    row.put("categories", categories);
                    row.put("tags", articleBasicDTO.getTags().stream().map(e -> Map.of("name", e.getName(), "path", e.getUrl())).collect(Collectors.toList()));
                    row.put("path", articleBasicDTO.getUrl());
                    row.put("date", new HexoDateWrapper(articleBasicDTO.getReleaseTime()));
                    row.put("index_img", articleBasicDTO.getThumbnail());
                    row.put("description", articleBasicDTO.getContent());
                    row.put("excerpt", articleBasicDTO.getContent());
                    list.add(row);
                }
            }
            page.put("posts", list);
            if (Objects.nonNull(pageInfo.getWebs())) {
                page.put("subtitle", pageInfo.getWebs().getSecond_title());
            }
            if (Objects.nonNull(((ArticleListPageVO) pageInfo).getPager())) {
                page.put("total", ((ArticleListPageVO) pageInfo).getPager().getPageList().size());
            } else {
                page.put("total", 1);
            }
            page.put("title", pageInfo.getWebs().getTitle());
        } else {

        }
        if (Objects.nonNull(pageInfo.getInit().getLogNavs())) {
            List<Map<String, Object>> list = new ArrayList<>();
            List<LogNavDTO> logNavs = pageInfo.getInit().getLogNavs();
            for (LogNavDTO logNavDTO : logNavs) {
                Map<String, Object> row = new HashMap<>();
                row.put("link", logNavDTO.getUrl());
                row.put("icon", logNavDTO.getIcon());
                row.put("key", logNavDTO.getNavName());
                list.add(row);
            }
            theme.put("navbar", Map.of("menu", list, "blog_title", pageInfo.getWebs().getTitle()));
        }

        if (Objects.nonNull(pageInfo.getInit().getLinks())) {
            List<Map<String, Object>> list = new ArrayList<>();
            List<LinkDTO> logNavs = pageInfo.getInit().getLinks();
            for (LinkDTO linkDTO : logNavs) {
                Map<String, Object> row = new HashMap<>();
                row.put("link", linkDTO.getUrl());
                row.put("title", linkDTO.getLinkName());
                row.put("intro", linkDTO.getAlt());
                row.put("icon", linkDTO.getIcon());
                row.put("image", linkDTO.getIcon());
                row.put("avatar", linkDTO.getIcon());
                list.add(row);
            }
            Map<String, Object> links = new HashMap<>(Map.of("items", list));
            theme.put("links", links);
            links.put("comments", Map.of("type", ""));
        }

        theme.put("language", pageInfo.getLang());
        theme.put("config", config);
        config.put("root", pageInfo.getBaseWithHostPath().substring(0, pageInfo.getBaseWithHostPath().lastIndexOf("/")));
        config.put("title", pageInfo.getWebs().getTitle());
        config.put("language", pageInfo.getLang());
        config.put("page", page);
        theme.put("apple_touch_icon", "/favicon.ico");
        theme.put("favicon", "/favicon.png");
        page.put("categories", new ArrayList<>());
        page.put("tags", new ArrayList<>());
        theme.put("site", page);
        theme.put("page", page);
        page.put("description", pageInfo.getDescription());
        page.put("keywords", pageInfo.getKeywords());
        return theme;
    }
}
