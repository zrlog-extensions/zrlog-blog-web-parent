package com.zrlog.blog.hexo.template.util;

import com.hibegin.common.util.ObjectUtil;
import com.zrlog.blog.web.template.vo.ArticleListPageVO;
import com.zrlog.blog.web.template.vo.BasePageInfo;
import com.zrlog.data.dto.ArticleBasicDTO;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import java.util.*;

public class HexoPageConverter {

    public static Map<String, Object> toIndexMap(BasePageInfo pageInfo, String layout, Context context) {
        Map<String, Object> map = new HashMap<>();
        map.put("title", pageInfo.getTitle());
        if (Objects.nonNull(pageInfo.getWebs())) {
            map.put("sub_title", pageInfo.getWebs().getSecond_title());
        }
        Map<String, Object> page = new HashMap<>();
        if (Objects.nonNull(layout)) {
            page.put("layout", layout);
        }
        Map<String, Object> theme = Objects.nonNull(pageInfo.getTheme()) ? pageInfo.getTheme() : new HashMap<>();
        if (Objects.nonNull(layout)) {
            Map<String, Object> themeMap = new HashMap<>();
            theme.put("title", pageInfo.getTitle());
            theme.put("sub_title", "");
            theme.put("page404", themeMap);
        }
        if (pageInfo instanceof ArticleListPageVO) {
            List<ArticleBasicDTO> rows = ((ArticleListPageVO) pageInfo).getData().getRows();
            List<Map<String, Object>> list = new ArrayList<>();
            for (ArticleBasicDTO articleBasicDTO : rows) {
                Map<String, Object> row = new HashMap<>();
                row.put("title", articleBasicDTO.getTitle());
                row.put("categories", ObjectUtil.requireNonNullElse(articleBasicDTO.getTypeName(), ""));
                row.put("tags", new ArrayList<>());
                row.put("path", articleBasicDTO.getUrl());
                row.put("description",articleBasicDTO.getContent());
                row.put("excerpt",articleBasicDTO.getContent());
                list.add(row);
            }
            page.put("posts", toArray(context, list, "posts", page));
        } else {
            page.put("posts", toArray(context, new ArrayList<>(), "posts", page));
        }
        page.put("total", 1);
        map.put("config", pageInfo.getTheme());
        map.put("theme", theme);
        map.put("page", page);
        return map;
    }

    private static Object toArray(Context context, Object arrays, String name, Map<String, Object> page) {
        Value bindings = context.getBindings("js");
        // 渲染方法内
        String json = new com.google.gson.Gson().toJson(arrays);
        context.getBindings("js").putMember("jsonStr", json);
        Value jsPage = context.eval("js", "JSON.parse(jsonStr)"); // 转为纯正 JS 对象
        bindings.putMember(name, jsPage);
        // 将 Java ArrayList 转换为 JS Array
        return jsPage;
    }
}
