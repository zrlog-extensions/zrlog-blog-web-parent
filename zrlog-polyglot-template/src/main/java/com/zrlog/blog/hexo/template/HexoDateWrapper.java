package com.zrlog.blog.hexo.template;

import org.graalvm.polyglot.HostAccess;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HexoDateWrapper {
    private final Date date;
    private final String rawDate;

    public HexoDateWrapper(String dateStr) {
        try {
            this.date = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
            this.rawDate = dateStr;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    // 兼容新版：当 JS 把对象当字符串处理时（比如 console.log(date) 或 拼接）
    @Override
    @HostAccess.Export
    public String toString() {
        return rawDate;
    }


    @HostAccess.Export
    public String year() {
        return String.valueOf(date.getYear());
    }

    @HostAccess.Export
    public String month() {
        return String.valueOf(date.getMonth() + 1);
    }

    @HostAccess.Export
    public HexoDateWrapper clone() {
        return this;
    }

    // 兼容旧版：支持 date.format('YYYY-MM-DD')
    @HostAccess.Export
    public String format(String pattern) {
        // 注意：JS 的 Moment.js 格式（YYYY-MM-DD）与 Java 的（yyyy-MM-dd）有细微区别
        // 你可以在这里做简单的转换逻辑
        return new SimpleDateFormat(pattern).format(date);
    }

    @HostAccess.Export
    public HexoDateWrapper locale(String lang) {
        return this;
    }
}