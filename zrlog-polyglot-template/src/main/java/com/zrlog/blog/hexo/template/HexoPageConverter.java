package com.zrlog.blog.hexo.template;

import com.hibegin.common.dao.dto.PageData;
import com.zrlog.blog.hexo.template.article.ArticleInfoUtils;
import com.zrlog.blog.hexo.template.util.HexoConvertUtils;
import com.zrlog.blog.hexo.template.util.HexoDataUtils;
import com.zrlog.blog.web.template.vo.ArticleDetailPageVO;
import com.zrlog.blog.web.template.vo.ArticleListPageVO;
import com.zrlog.blog.web.template.vo.BasePageInfo;
import com.zrlog.common.cache.dto.LinkDTO;
import com.zrlog.common.cache.dto.LogNavDTO;
import com.zrlog.common.cache.dto.TagDTO;
import com.zrlog.common.cache.dto.TypeDTO;
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
                page.put("layout", "post");
            } else if (layout.equals("archives")) {
                page.put("layout", "archive");
            } else {
                page.put("layout", layout);
            }
            if (pageInfo.getReqUriPath().contains("/sort/")) {
                page.put("layout", "category");
                page.put("category", ((ArticleListPageVO) pageInfo).getTipsName());
            } else if (pageInfo.getReqUriPath().contains("/tag/")) {
                page.put("layout", "tag");
                page.put("tag", ((ArticleListPageVO) pageInfo).getTipsName());
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
            page.put("date", new HexoDateWrapper(log.getFullReleaseTime()));
            page.put("next_post", HexoConvertUtils.getNextLog((ArticleDetailPageVO) pageInfo));
            page.put("prev_post", HexoConvertUtils.getPrevLog((ArticleDetailPageVO) pageInfo));
            page.put("permalink", ((ArticleDetailPageVO) pageInfo).getLog().getNoSchemeUrl());
            page.put("tags", HexoDataUtils.wrap(log.getTags().stream().map(e -> Map.of("name", e.getName(), "path", e.getUrl())).collect(Collectors.toList()), pageInfo.getInit().getTags().size()));
            if (Objects.nonNull(log.getComments())) {
                page.put("comments", log.getComments());
            } else {
                page.put("comments", new ArrayList<>());
            }
            Map<String, Object> cat = new HashMap<>();
            cat.put("_id", log.getTypeId());
            cat.put("name", log.getTypeName());
            cat.put("path", log.getTypeUrl());
            //cat.put("parent", null);   // 顶级分类传 null
            List<Map<String, Object>> categories = new ArrayList<>();
            pageInfo.getInit().getTypes().stream().filter(e -> e.getTypeName().equals(log.getTypeName())).findFirst().ifPresent(type -> {
                cat.put("length", type.getTypeamount());
            });

            categories.add(cat);
            page.put("categories", HexoDataUtils.wrap(categories, pageInfo.getInit().getTypes().size()));
            page.put("posts", ArticleInfoUtils.getHotArticle(pageInfo.getInit().getHotLogs(), pageInfo.getInit().getStatistics().getTotalArticleSize()));
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
                    cat.put("name", articleBasicDTO.getTypeName());
                    cat.put("path", articleBasicDTO.getTypeUrl());
                    pageInfo.getInit().getTypes().stream().filter(e -> e.getTypeName().equals(articleBasicDTO.getTypeName())).findFirst().ifPresent(type -> {
                        cat.put("length", type.getTypeamount());
                    });
                    categories.add(cat);
                    row.put("categories", categories);
                    row.put("tags", articleBasicDTO.getTags().stream().map(e -> Map.of("name", e.getName(), "path", e.getUrl())).collect(Collectors.toList()));
                    row.put("path", articleBasicDTO.getUrl());
                    row.put("date", new HexoDateWrapper(articleBasicDTO.getFullReleaseTime()));
                    row.put("index_img", articleBasicDTO.getThumbnail());
                    row.put("description", articleBasicDTO.getContent());
                    row.put("excerpt", articleBasicDTO.getContent());
                    row.put("content", articleBasicDTO.getContent());
                    list.add(HexoDataUtils.wrapArticle(row));
                }
            }
            page.put("posts", HexoDataUtils.wrap(list, (int) data.getTotalElements()));
            if (Objects.nonNull(pageInfo.getWebs())) {
                page.put("subtitle", pageInfo.getWebs().getSecond_title());
            }
            if (Objects.nonNull(((ArticleListPageVO) pageInfo).getPager())) {
                page.put("total", ((ArticleListPageVO) pageInfo).getPager().getPageList().size());
            } else {
                page.put("total", 1);
            }
            page.put("title", pageInfo.getWebs().getTitle());

            if (Objects.nonNull(pageInfo.getInit().getTags())) {
                List<Map<String, Object>> tags = new ArrayList<>();
                for (TagDTO tagDTO : pageInfo.getInit().getTags()) {
                    Map<String, Object> tag = new HashMap<>();
                    tag.put("path", tagDTO.getUrl());
                    tag.put("name", tagDTO.getText());
                    tag.put("_id", tagDTO.getId());
                    tag.put("length", tagDTO.getCount());
                    tag.put("posts", HexoDataUtils.wrap(new ArrayList<>(), Math.toIntExact(tagDTO.getCount())));
                    tags.add(tag);
                }
                page.put("tags", HexoDataUtils.wrap(tags));
            }

            if (Objects.nonNull(pageInfo.getInit().getTypes())) {
                List<Map<String, Object>> types = new ArrayList<>();
                List<TypeDTO> typeDTOS = pageInfo.getInit().getTypes();
                for (TypeDTO typeDTO : typeDTOS) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("path", typeDTO.getUrl());
                    row.put("name", typeDTO.getTypeName());
                    row.put("_id", typeDTO.getId());
                    row.put("length", typeDTO.getTypeamount());
                    row.put("posts", HexoDataUtils.wrap(new ArrayList<>(), Math.toIntExact(typeDTO.getTypeamount())));
                    //row.put("parent", null);
                    types.add(row);
                }
                page.put("categories", HexoDataUtils.wrap(types, types.size()));
            }
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
        theme.put("apple_touch_icon", "/favicon.png");
        theme.put("favicon", "/favicon.ico");
        theme.put("site", page);
        theme.put("page", page);
        page.put("description", pageInfo.getDescription());
        page.put("keywords", pageInfo.getKeywords());
        return theme;
    }
}
