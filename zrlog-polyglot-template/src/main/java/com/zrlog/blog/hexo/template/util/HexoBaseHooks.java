package com.zrlog.blog.hexo.template.util;

import com.hibegin.common.util.LoggerUtil;
import com.zrlog.blog.hexo.template.impl.HexoHelperImpl;
import com.zrlog.blog.hexo.template.impl.HexoI18nHelperImpl;
import com.zrlog.blog.hexo.template.impl.HexoPaginator;
import com.zrlog.blog.hexo.template.impl.HexoTagCloud;
import com.zrlog.blog.polyglot.JsTemplateRender;
import com.zrlog.blog.polyglot.util.YamlLoader;
import com.zrlog.blog.web.template.vo.ArticleDetailPageVO;
import com.zrlog.blog.web.template.vo.ArticleListPageVO;
import com.zrlog.blog.web.template.vo.BasePageInfo;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyExecutable;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class HexoBaseHooks {

    private final BasePageInfo basePageInfo;
    private final JsTemplateRender jsTemplateRender;
    private final String rootPath;
    private final Map<String, Object> theme;

    public HexoBaseHooks(String rootPath, JsTemplateRender jsTemplateRender, BasePageInfo basePageInfo, Map<String, Object> theme) {
        this.jsTemplateRender = jsTemplateRender;
        this.rootPath = rootPath;
        this.basePageInfo = basePageInfo;
        this.theme = theme;
    }

    public void inject(Value bindings) {
        // 映射 partial
        for (String fun : Arrays.asList("partial", "partial_lang")) {
            bindings.putMember(fun, (ProxyExecutable) args -> {
                String path = args[0].asString();
                Map<String, Object> locals = (args.length > 1 && !args[1].isNull()) ? args[1].as(Map.class) : null;
                try {
                    return jsTemplateRender.includeRender(path, locals);
                } catch (Exception e) {
                    return "page -> " + path + "\n" + LoggerUtil.recordStackTraceMsg(e);
                }
            });
        }
        bindings.putMember("_p", new HexoI18nHelperImpl(rootPath, basePageInfo.getLocal()));
        bindings.putMember("__", new HexoI18nHelperImpl(rootPath, basePageInfo.getLocal()));
        bindings.putMember("trim", (ProxyExecutable) args -> {
            return args[0].asString().trim();
        });

        if (basePageInfo instanceof ArticleListPageVO) {
            bindings.putMember("paginator", new HexoPaginator(((ArticleListPageVO) basePageInfo).getPager()));
        } else {
            bindings.putMember("paginator", (ProxyExecutable) args -> {
                return "paginator";
            });
        }
        bindings.putMember("tagcloud", new HexoTagCloud(basePageInfo.getInit().getTags()));
        bindings.putMember("url_join", HexoHelperImpl.getUrlJoinProvider());

        HexoHelperImpl hexoHelper = new HexoHelperImpl(basePageInfo);
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
        bindings.putMember("is_home_first_page", (ProxyExecutable) args -> {
            return basePageInfo instanceof ArticleListPageVO;
        });
        bindings.putMember("markdown", (ProxyExecutable) args -> {
            return args[0];
        });
        bindings.putMember("truncate", (ProxyExecutable) args -> {
            return args[0];
        });
        js(bindings);

        bindings.putMember("is_home", (ProxyExecutable) args -> {
            return Objects.equals(YamlLoader.getNestedValue(theme, "page.layout"), "index");
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
            return HexoDateObjUtils.toDateString(args[0].as(Object.class), args[1].as(Object.class), "yyyy-MM-dd HH:mm:ss", basePageInfo.getLocal());
        });

        bindings.putMember("date", (ProxyExecutable) args -> {
            return HexoDateObjUtils.toDateString(args[0].as(Object.class), args[1].as(Object.class), "yyyy-MM-dd", basePageInfo.getLocal());
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

        bindings.putMember("get_cdn_url", (ProxyExecutable) args -> {
            return basePageInfo.getStaticResourceBaseUrl();
        });

        bindings.putMember("is_current", (ProxyExecutable) args -> {
            return false;
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
            return String.format("<script src=\"%s\"%s></script>", basePageInfo.getTemplateUrl() + "/source/" + src + (src.endsWith(".js") ? "" : ".js"), attributes);

        });
    }
}
