package com.zrlog.blog.hexo.template.impl;

import com.zrlog.blog.hexo.template.HexoTemplate;
import com.zrlog.blog.hexo.template.ZrLogResourceLoader;
import com.zrlog.blog.hexo.template.util.YamlLoader;

import java.util.*;

public class HexoI18nHelperImpl {

    private final Map<String, Object> languagesMap = new TreeMap<>();

    public HexoI18nHelperImpl(HexoTemplate hexoTemplate, String lang) {
        for (String langAlias : getLangFiles(lang)) {
            String path = hexoTemplate.getRootPath() + "/languages/" + langAlias + ".yml";
            if (ZrLogResourceLoader.exists(path)) {
                languagesMap.putAll(YamlLoader.loadConfig(ZrLogResourceLoader.read(path)));
                break;
            }
        }
    }

    private List<String> getLangFiles(String lang) {
        return Arrays.asList(lang.split("-")[0], lang, lang.replace("_", "-"));
    }

    /**
     * 对应 Hexo 的 __() 方法
     *
     * @param path 翻译的键值，如 "menu.home"
     * @param args 动态替换参数 (可选)
     */
    public String i18n(String path, Object... args) {
        Object nestedValue = YamlLoader.getNestedValue(languagesMap, path);
        if (Objects.isNull(nestedValue)) {
            return path;
        }
        return String.format(nestedValue.toString(), args);
    }
}