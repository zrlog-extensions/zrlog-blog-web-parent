package com.zrlog.blog.hexo.template;

import com.hibegin.common.dao.dto.PageData;
import com.zrlog.blog.hexo.template.util.HexoConvertUtils;
import com.zrlog.blog.web.template.vo.ArticleDetailPageVO;
import com.zrlog.blog.web.template.vo.ArticleListPageVO;
import com.zrlog.blog.web.template.vo.BasePageInfo;
import com.zrlog.common.cache.dto.LogNavDTO;
import com.zrlog.data.dto.ArticleBasicDTO;
import com.zrlog.data.dto.ArticleDetailDTO;
import org.graalvm.polyglot.Context;

import java.util.*;
import java.util.stream.Collectors;

public class HexoPageConverter {

    public static Map<String, Object> toIndexMap(BasePageInfo pageInfo, String layout, Context context) {
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
            } else {
                page.put("layout", "/" + layout);
            }
        }
        Map<String, Object> theme = Objects.nonNull(pageInfo.getTheme()) ? pageInfo.getTheme() : new HashMap<>();
        if (Objects.nonNull(layout)) {
            Map<String, Object> themeMap = new HashMap<>();
            theme.put("title", pageInfo.getTitle());
            theme.put("sub_title", "");
            theme.put("page404", themeMap);
        }
        if (pageInfo instanceof ArticleListPageVO) {
            PageData<ArticleBasicDTO> data = ((ArticleListPageVO) pageInfo).getData();
            if (Objects.nonNull(data)) {
                List<ArticleBasicDTO> rows = data.getRows();
                List<Map<String, Object>> list = new ArrayList<>();
                for (ArticleBasicDTO articleBasicDTO : rows) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("title", articleBasicDTO.getTitle());
                    List<Map<String, Object>> categories = new ArrayList<>();
                    categories.add(Map.of("length", 0, "name", articleBasicDTO.getTypeName(), "path", articleBasicDTO.getTypeUrl()));
                    row.put("categories", categories);
                    row.put("tags", articleBasicDTO.getTags().stream().map(e -> Map.of("name", e.getName(), "path", e.getUrl())).collect(Collectors.toList()));
                    row.put("path", articleBasicDTO.getUrl());
                    row.put("date", articleBasicDTO.getReleaseTime());
                    row.put("index_img", articleBasicDTO.getThumbnail());
                    row.put("description", articleBasicDTO.getContent());
                    row.put("excerpt", articleBasicDTO.getContent());
                    list.add(row);
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
            }
        } else if (pageInfo instanceof ArticleDetailPageVO) {
            Map<String, Object> row = new HashMap<>();
            ArticleDetailDTO log = ((ArticleDetailPageVO) pageInfo).getLog();
            row.put("title", log.getTitle());
            page.put("post", row);
            page.put("title", log.getTitle());
            page.put("content", log.getContent());
            page.put("meta", true);
            page.put("data", log.getReleaseTime());
            page.put("prev_post", HexoConvertUtils.getPrevLog((ArticleDetailPageVO) pageInfo));
            page.put("next_post", HexoConvertUtils.getNextLog((ArticleDetailPageVO) pageInfo));
            /*if (Objects.nonNull(pageInfo.getWebs())) {
                page.put("sub_title", pageInfo.getWebs().getSecond_title());
            }*/
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
        pageInfo.getTheme().put("language", pageInfo.getLang());
        map.put("config", pageInfo.getTheme());
        theme.put("apple_touch_icon", "/favicon.ico");
        theme.put("favicon", "/favicon.png");
        map.put("theme", theme);
        map.put("page", page);

        page.put("banner_img", "https://fluid.s3.bitiful.net/bg/vdysjx.png?w=1920&fmt=webp");
        Map<String, Object> indexGen = (Map<String, Object>) pageInfo.getTheme().computeIfAbsent("index_generator", k -> new HashMap<>());
        indexGen.putIfAbsent("order_by", "name");
        return map;
    }
}
