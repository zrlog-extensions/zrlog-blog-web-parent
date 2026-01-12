package com.zrlog.blog.hexo.template.ejs;

public class HexoI18nHelperImpl {
    /**
     * 对应 Hexo 的 __() 方法
     *
     * @param key  翻译的键值，如 "menu.home"
     * @param args 动态替换参数 (可选)
     */
    public String i18n(String key, Object... args) {
        return key;
    }
}