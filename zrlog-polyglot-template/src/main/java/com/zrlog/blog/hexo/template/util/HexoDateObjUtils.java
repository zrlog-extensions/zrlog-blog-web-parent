package com.zrlog.blog.hexo.template.util;

import com.zrlog.blog.hexo.template.HexoDateWrapper;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

public class HexoDateObjUtils {

    public static String toDateString(Object dateObj, Object format, String defaultFormat) {
        if (Objects.isNull(dateObj)) return "";

        // 获取日期对象（可能是 Long 时间戳或 Java Date）
        java.time.ZonedDateTime zonedDateTime;

        try {
            if (dateObj instanceof Date) {
                zonedDateTime = ((Date) dateObj).toInstant().atZone(ZoneId.systemDefault());
            } else if (dateObj instanceof Long) {
                zonedDateTime = java.time.Instant.ofEpochMilli((Long) dateObj).atZone(ZoneId.systemDefault());
            } else if (dateObj instanceof HexoDateWrapper) {
                zonedDateTime = java.time.Instant.ofEpochMilli(((HexoDateWrapper) dateObj).getDate().getTime()).atZone(ZoneId.systemDefault());
            } else {
                return dateObj.toString();
            }

            // 第二个参数是可选的格式化字符串，例如 date(post.date, 'MMM D, YYYY')
            String formatStr = Objects.nonNull(format) ? format.toString() : defaultFormat;

            // 简单处理：将 Hexo 的 YYYY 转换为 Java 的 yyyy
            formatStr = formatStr.replace("YYYY", "yyyy").replace("YY", "yy").replace("DD", "dd");

            return zonedDateTime.format(DateTimeFormatter.ofPattern(formatStr));
        } catch (Exception e) {
            return "Invalid Date" + e.getMessage();
        }

    }
}
