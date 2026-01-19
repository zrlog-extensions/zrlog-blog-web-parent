package com.zrlog.blog.hexo.template.util;

import com.hibegin.common.util.LoggerUtil;
import com.zrlog.blog.hexo.template.HexoTemplate;
import com.zrlog.blog.hexo.template.impl.HexoHelperImpl;
import com.zrlog.blog.hexo.template.impl.HexoI18nHelperImpl;
import com.zrlog.blog.hexo.template.impl.HexoPaginator;
import com.zrlog.blog.hexo.template.impl.HexoTagCloud;
import com.zrlog.blog.polyglot.resource.TemplateResolver;
import com.zrlog.blog.polyglot.util.YamlLoader;
import com.zrlog.blog.web.template.vo.ArticleDetailPageVO;
import com.zrlog.blog.web.template.vo.ArticleListPageVO;
import com.zrlog.blog.web.template.vo.BasePageInfo;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyExecutable;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

public class HexoBaseHooks {

    private final TemplateResolver templateResolver;
    private final HexoTemplate hexoTemplate;
    private final BasePageInfo basePageInfo;

    public HexoBaseHooks(TemplateResolver templateResolver, HexoTemplate hexoTemplate) {
        this.templateResolver = templateResolver;
        this.hexoTemplate = hexoTemplate;
        this.basePageInfo = hexoTemplate.getPageInfo();
    }

    public void inject(Value bindings) {
        HexoHelperImpl hexoHelper = new HexoHelperImpl(hexoTemplate, templateResolver, hexoTemplate.getPageInfo());
        // 映射 partial
        bindings.putMember("partial", (ProxyExecutable) args -> {
            String path = args[0].asString();
            Map<String, Object> locals = (args.length > 1 && !args[1].isNull()) ? args[1].as(Map.class) : hexoTemplate.getLocals();
            try {
                return hexoTemplate.getJsTemplateRender().render(path, locals);
            } catch (Exception e) {
                /*e.fillInStackTrace();
                throw new RuntimeException(e);*/
                return LoggerUtil.recordStackTraceMsg(e);
            }
        });
        bindings.putMember("_p", (ProxyExecutable) args -> {
            return args[0].asString();
        });

        bindings.putMember("partial_lang", (ProxyExecutable) args -> {
            String path = args[0].asString();
            try {
                Map<String, Object> locals = args.length > 1 ? args[1].as(Map.class) : hexoTemplate.getLocals();
                return hexoHelper.partial("_partial/" + path, locals);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        bindings.putMember("__", new HexoI18nHelperImpl(hexoTemplate, basePageInfo.getLocal()));

        if (basePageInfo instanceof ArticleListPageVO) {
            bindings.putMember("paginator", new HexoPaginator(((ArticleListPageVO) basePageInfo).getPager()));
        }
        bindings.putMember("tagcloud", new HexoTagCloud(basePageInfo.getInit().getTags()));
        bindings.putMember("url_join", HexoHelperImpl.getUrlJoinProvider());

        // 映射 url_for
        bindings.putMember("url_for", (ProxyExecutable) args -> {
            if (args.length > 0) {
                return hexoHelper.url_for(args[0].asString());
            }
            return hexoHelper.url_for(null);
        });
        bindings.putMember("url", (ProxyExecutable) args -> {
            if (args.length > 0) {
                return hexoHelper.url_for(args[0].asString());
            }
            return hexoHelper.url_for(null);
        });

        bindings.putMember("open_graph", (ProxyExecutable) args -> {
            String title = basePageInfo.getTitle();
            String author = "";
            String description = basePageInfo.getDescription();
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("<meta name=\"description\" content=\"%s\"/>\n", description));
            sb.append("<meta property=\"og:type\" content=\"website\"/>\n");
            sb.append(String.format("<meta property=\"og:title\" content=\"%s\"/>\n", title));
            sb.append(String.format("<meta property=\"og:author\" content=\"%s\"/>\n", author));
            sb.append("<meta name=\"twitter:card\" content=\"summary_large_image\"/>\n");

            return sb.toString();
        });

        bindings.putMember("is_post", (ProxyExecutable) args -> {
            return basePageInfo instanceof ArticleDetailPageVO;
        });
        bindings.putMember("is_tag", (ProxyExecutable) args -> {
            return false;
        });
        bindings.putMember("toc", (ProxyExecutable) args -> {
            return false;
        });

        bindings.putMember("is_page", (ProxyExecutable) args -> {
            return basePageInfo instanceof ArticleListPageVO;
        });

        bindings.putMember("is_archive", (ProxyExecutable) args -> {
            return basePageInfo instanceof ArticleListPageVO;
        });
        bindings.putMember("is_month", (ProxyExecutable) args -> {
            return basePageInfo instanceof ArticleListPageVO;
        });
        bindings.putMember("feed_tag", (ProxyExecutable) args -> {
            return "feed_tag";
        });
        bindings.putMember("favicon_tag", (ProxyExecutable) args -> {
            return "favicon_tag";
        });
        bindings.putMember("asyncCss", (ProxyExecutable) args -> {
            return "asyncCss";
        });
        bindings.putMember("vendorCdn", (ProxyExecutable) args -> {
            return "vendorCdn";
        });
        bindings.putMember("vendorCdnIntegrity", (ProxyExecutable) args -> {
            return "vendorCdn";
        });
        js(bindings);

        bindings.putMember("is_home", (ProxyExecutable) args -> {
            return Objects.equals(YamlLoader.getNestedValue(basePageInfo.getTheme(), "page.layout"), "/index");
        });
        bindings.putMember("is_category", (ProxyExecutable) args -> {
            return false;
        });

        bindings.putMember("strip_html", (ProxyExecutable) args -> {
            if (args.length == 0 || args[0].isNull()) {
                return "";
            }

            // 获取传入的内容（通常是渲染后的 HTML 字符串）
            String content = args[0].asString();

            // 使用正则去除所有 HTML 标签
            // <[^>]*> 匹配所有的 <...> 标签
            if (content == null) return "";

            return content.replaceAll("<[^>]*>", "").trim();
        });

        bindings.putMember("full_date", (ProxyExecutable) args -> {
            if (args.length == 0 || args[0].isNull()) return "";

            Object dateObj = args[0].as(Object.class);
            java.time.LocalDateTime dateTime;

            // 1. 处理不同的日期输入类型
            if (dateObj instanceof Date) {
                dateTime = ((Date) dateObj).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            } else if (dateObj instanceof Long) {
                dateTime = java.time.Instant.ofEpochMilli((Long) dateObj).atZone(ZoneId.systemDefault()).toLocalDateTime();
            } else {
                // 如果是字符串或其他，尝试原样返回或解析
                return dateObj.toString();
            }

            // 2. 格式化输出 (Hexo 默认: YYYY-MM-DD)
            // 如果 args.length > 1，通常第二个参数是自定义格式字符串
            String format = (args.length > 1) ? args[1].asString() : "yyyy-MM-dd HH:mm:ss";

            try {
                return dateTime.format(DateTimeFormatter.ofPattern(format));
            } catch (Exception e) {
                return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
        });

        bindings.putMember("date", (ProxyExecutable) args -> {
            if (args.length == 0 || args[0].isNull()) return "";

            // 获取日期对象（可能是 Long 时间戳或 Java Date）
            Object dateObj = args[0].as(Object.class);
            java.time.ZonedDateTime zonedDateTime;

            try {
                if (dateObj instanceof Date) {
                    zonedDateTime = ((Date) dateObj).toInstant().atZone(ZoneId.systemDefault());
                } else if (dateObj instanceof Long) {
                    zonedDateTime = java.time.Instant.ofEpochMilli((Long) dateObj).atZone(ZoneId.systemDefault());
                } else {
                    return dateObj.toString();
                }

                // 第二个参数是可选的格式化字符串，例如 date(post.date, 'MMM D, YYYY')
                String format = (args.length > 1) ? args[1].asString() : "yyyy-MM-dd";

                // 简单处理：将 Hexo 的 YYYY 转换为 Java 的 yyyy
                format = format.replace("YYYY", "yyyy").replace("YY", "yy").replace("DD", "dd");

                return zonedDateTime.format(DateTimeFormatter.ofPattern(format));
            } catch (Exception e) {
                return "Invalid Date";
            }
        });
        bindings.putMember("date_xml", (ProxyExecutable) args -> {
            if (args.length == 0 || args[0].isNull()) return "";

            // 获取日期对象（可能是 Long 时间戳或 Java Date）
            Object dateObj = args[0].as(Object.class);
            java.time.ZonedDateTime zonedDateTime;

            try {
                if (dateObj instanceof Date) {
                    zonedDateTime = ((Date) dateObj).toInstant().atZone(ZoneId.systemDefault());
                } else if (dateObj instanceof Long) {
                    zonedDateTime = java.time.Instant.ofEpochMilli((Long) dateObj).atZone(ZoneId.systemDefault());
                } else {
                    return dateObj.toString();
                }

                // 第二个参数是可选的格式化字符串，例如 date(post.date, 'MMM D, YYYY')
                String format = (args.length > 1) ? args[1].asString() : "yyyy-MM-dd";

                // 简单处理：将 Hexo 的 YYYY 转换为 Java 的 yyyy
                format = format.replace("YYYY", "yyyy").replace("YY", "yy").replace("DD", "dd");

                return zonedDateTime.format(DateTimeFormatter.ofPattern(format));
            } catch (Exception e) {
                return "Invalid Date";
            }
        });

        bindings.putMember("css", (ProxyExecutable) args -> {
            if (args.length == 0 || args[0].isNull()) return "";

            StringJoiner sb = new StringJoiner("/");

            // 循环处理所有传入的参数（css 可能被多次调用或传入数组）
            for (Value arg : args) {
                sb.add(arg.asString());
            }
            return appendCssTag(sb.toString());
        });

        bindings.putMember("titlecase", (ProxyExecutable) args -> {
            return args[0];
        });

        bindings.putMember("list_archives", (ProxyExecutable) args -> {
            return "list_archives";
        });

        bindings.putMember("list_categories", (ProxyExecutable) args -> {
            return "list_categories";
        });

        bindings.putMember("list_tags", (ProxyExecutable) args -> {
            return "list_tags";
        });
    }

    // 辅助拼接方法
    private String appendCssTag(String path) {
        if (path == null || path.isEmpty()) return "";
        // 简单处理：如果没有以 http 或 / 开头，可以自动补充
        String href = path;
        if (!href.contains("://") && !href.startsWith("/")) {
            href = "/" + href;
        }
        // 自动补全后缀
        if (!href.endsWith(".css") && !href.contains("?")) {
            href += ".css";
        }
        if (href.startsWith("/") && !href.startsWith("//")) {
            href = basePageInfo.getTemplateUrl() + "/source" + href;
        }
        return String.format("<link rel=\"stylesheet\" href=\"%s\"/>\n", href);
    }

    private void js(Value bindings) {
        bindings.putMember("js", (ProxyExecutable) args -> {
            if (args.length == 0) return "";

            // 1. 获取脚本路径 (例如: /js/main.js)
            String src = args[0].asString();

            // 2. 处理第二个参数 (属性对象，例如 { async: true, defer: true })
            StringBuilder attributes = new StringBuilder();

            if (args.length > 1) {
                // 简单的属性拼接逻辑
                Value options = args[1];
                if (options.hasMember("async") && options.getMember("async").asBoolean()) {
                    attributes.append(" async");
                }
                if (options.hasMember("defer") && options.getMember("defer").asBoolean()) {
                    attributes.append(" defer");
                }
            }
            // 3. 返回标准的 HTML 标签
            if (src.startsWith("http")) {
                return String.format("<script src=\"%s\"%s></script>", src, attributes);

            }
            return String.format("<script src=\"%s\"%s></script>", hexoTemplate.getPageInfo().getTemplateUrl() + "/source/" + src + ".js", attributes);

        });
    }
}
