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

    public static Map<String, Object> toHexoMap(BasePageInfo pageInfo, String layout) {
        Map<String, Object> map = new HashMap<>();
        map.put("title", pageInfo.getWebs().getTitle());

        Map<String, Object> page = (Map<String, Object>) pageInfo.getTheme().get("page");
        if (Objects.isNull(page)) {
            page = new HashMap<>();
            pageInfo.getTheme().put("page", page);
        }
        if (Objects.nonNull(layout)) {
            if (layout.equals("detail")) {
                page.put("layout", "/post");
            } else if (layout.equals("archives")) {
                page.put("layout", "/archive");
            } else {
                page.put("layout", "/" + layout);
            }
        }
        Map<String, Object> theme = Objects.nonNull(pageInfo.getTheme()) ? pageInfo.getTheme() : new HashMap<>();

        if (layout.equals("detail")) {
            Map<String, Object> row = new HashMap<>();
            ArticleDetailDTO log = ((ArticleDetailPageVO) pageInfo).getLog();
            row.put("title", log.getTitle());
            row.put("articleId", log.getId());
            page.put("post", row);
            page.put("title", log.getTitle());
            page.put("content", log.getContent());
            page.put("date", log.getReleaseTime());
            page.put("next_post", HexoConvertUtils.getNextLog((ArticleDetailPageVO) pageInfo));
            page.put("prev_post", HexoConvertUtils.getPrevLog((ArticleDetailPageVO) pageInfo));
            page.put("comment", pageInfo.getWebs().getComment_plugin_name());
            page.put("permalink", ((ArticleDetailPageVO) pageInfo).getLog().getNoSchemeUrl());
            if (Objects.nonNull(log.getComments())) {
                page.put("comments", log.getComments());
            } else {
                page.put("comments", new ArrayList<>());
            }
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
                    row.put("date", articleBasicDTO.getReleaseTime());
                    row.put("index_img", articleBasicDTO.getThumbnail());
                    row.put("description", articleBasicDTO.getContent());
                    row.put("excerpt", articleBasicDTO.getContent());
                    list.add(row);
                }
            }
            page.put("posts", list);
            page.put("categories", new ArrayList<>());
            page.put("tags", new ArrayList<>());
            if (Objects.nonNull(pageInfo.getWebs())) {
                page.put("subtitle", pageInfo.getWebs().getSecond_title());
            }
            if (Objects.nonNull(((ArticleListPageVO) pageInfo).getPager())) {
                page.put("total", ((ArticleListPageVO) pageInfo).getPager().getPageList().size());
            } else {
                page.put("total", 1);
            }
        } else {

        }
        if (Objects.nonNull(pageInfo.getInit().getLogNavs())) {
            List<Map<String, Object>> list = new ArrayList<>();
            List<LogNavDTO> logNavs = pageInfo.getInit().getLogNavs();
            for (LogNavDTO logNavDTO : logNavs) {
                Map<String, Object> row = new HashMap<>();
                row.put("link", logNavDTO.getUrl());
                row.put("key", logNavDTO.getNavName());
                list.add(row);
            }
            pageInfo.getTheme().put("navbar", Map.of("menu", list, "blog_title", pageInfo.getWebs().getTitle()));
        }

        if (Objects.nonNull(pageInfo.getInit().getLinks())) {
            List<Map<String, Object>> list = new ArrayList<>();
            List<LinkDTO> logNavs = pageInfo.getInit().getLinks();
            for (LinkDTO logNavDTO : logNavs) {
                Map<String, Object> row = new HashMap<>();
                row.put("link", logNavDTO.getUrl());
                row.put("title", logNavDTO.getLinkName());
                row.put("intro", logNavDTO.getAlt());
                row.put("avatar", "/favicon.ico");
                list.add(row);
            }
            Map<String, Object> links = new HashMap<>(Map.of("items", list));
            pageInfo.getTheme().put("links", links);
            links.put("comments", Map.of("type", ""));
        }

        pageInfo.getTheme().put("language", pageInfo.getLang());
        pageInfo.getTheme().put("title", pageInfo.getWebs().getTitle());
        map.put("config", pageInfo.getTheme());
        pageInfo.getTheme().put("root", pageInfo.getBaseWithHostPath());
        theme.put("apple_touch_icon", "/favicon.ico");
        theme.put("favicon", "/favicon.png");
        map.put("theme", theme);
        map.put("site", page);
        map.put("page", page);
        map.put("locals", theme);
        page.put("description", pageInfo.getDescription());
        page.put("keywords", pageInfo.getKeywords());
        Map<String, Object> indexGen = (Map<String, Object>) pageInfo.getTheme().computeIfAbsent("index_generator", k -> new HashMap<>());
        indexGen.putIfAbsent("order_by", "name");
        return map;
    }
}
