package com.zrlog.blog.hexo.template.impl;

import com.zrlog.blog.hexo.template.HexoTemplate;
import com.zrlog.blog.hexo.template.ZrLogResourceLoader;
import com.zrlog.blog.hexo.template.util.YamlLoader;

import java.util.Map;
import java.util.Objects;

public class HexoI18nHelperImpl {

    private final Map<String, Object> languagesMap;

    public HexoI18nHelperImpl(HexoTemplate hexoTemplate, String lang) {
        if (Objects.equals(lang, "zh_CN")) {
            lang = "zh-CN";
        }
        String path = hexoTemplate.getRootPath() + "/languages/" + lang + ".yml";
        this.languagesMap = YamlLoader.loadConfig(ZrLogResourceLoader.read(path));
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