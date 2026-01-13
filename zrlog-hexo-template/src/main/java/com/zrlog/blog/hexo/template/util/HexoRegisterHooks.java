package com.zrlog.blog.hexo.template.util;

import com.zrlog.blog.hexo.template.HexoTemplate;
import com.zrlog.blog.hexo.template.ejs.TemplateResolver;
import com.zrlog.blog.hexo.template.fluid.FluidConfigExporter;
import com.zrlog.blog.hexo.template.impl.HexoHelperImpl;
import com.zrlog.blog.hexo.template.impl.HexoI18nHelperImpl;
import com.zrlog.blog.hexo.template.impl.HexoPaginator;
import com.zrlog.blog.web.template.vo.ArticleListPageVO;
import com.zrlog.blog.web.template.vo.BasePageInfo;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyExecutable;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class HexoRegisterHooks {

    private final BasePageInfo basePageInfo;
    private final TemplateResolver templateResolver;
    private final HexoTemplate hexoTemplate;

    public HexoRegisterHooks(BasePageInfo basePageInfo, TemplateResolver templateResolver, HexoTemplate hexoTemplate) {
        this.basePageInfo = basePageInfo;
        this.templateResolver = templateResolver;
        this.hexoTemplate = hexoTemplate;
    }

    private String compileStylus(Value bindings, String stylusContent) {
        Value stylus = bindings.getMember("stylus");
        // 调用 stylus.render(content, options)
        return stylus.getMember("render").execute(stylusContent).asString();
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
            href = basePageInfo.getTemplateUrl() + "source" + href;
        }
        return String.format("<link rel=\"stylesheet\" href=\"%s\"/>\n", href);
    }

    public void injectHelpers(Value bindings) {
        HexoHelperImpl hexoHelper = new HexoHelperImpl(hexoTemplate, templateResolver);
        // 映射 partial
        bindings.putMember("partial", (ProxyExecutable) args -> {
            String path = args[0].asString();
            try {
                Map<String, Object> locals = args.length > 1 ? args[1].as(Map.class) : hexoTemplate.getLocals();
                return hexoHelper.partial(path, locals);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        bindings.putMember("__", (ProxyExecutable) args -> {
            String key = args[0].asString();
            try {
                return new HexoI18nHelperImpl(hexoTemplate, basePageInfo.getLang()).i18n(key);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        if (basePageInfo instanceof ArticleListPageVO) {
            bindings.putMember("paginator", new HexoPaginator(((ArticleListPageVO) basePageInfo).getPager()));
        }
        bindings.putMember("url_join", HexoHelperImpl.getUrlJoinProvider());

        // 映射 url_for
        bindings.putMember("url_for", (ProxyExecutable) args -> {
            if (args.length > 0) {
                return hexoHelper.url_for(args[0].asString());
            }
            return hexoHelper.url_for(null);
        });
        // 在 injectHelpers 方法中
        bindings.putMember("inject_point", (ProxyExecutable) args -> {
            if (args.length == 0) return "";
            String pointName = args[0].asString();

            // 1. 从之前 setup 阶段填充的 injectionPoints Map 中获取注册的文件路径列表
            // 这里的 injectionPoints 是你存储 List<String> 路径的那个全局 Map
            List<String> filePaths = hexoTemplate.getHexoObjectBox().getInjectionPoints(pointName);

            if (filePaths == null || filePaths.isEmpty()) {
                return "";
            }

            StringBuilder htmlResult = new StringBuilder();
            for (String filePath : filePaths) {
                // 2. 重要：注入的内容通常是 .ejs 文件，不能直接 toString()
                // 你必须调用你现有的 EJS 渲染引擎去渲染这个文件
                // 假设你有一个 renderPartial(path) 方法
                //System.out.println("filePath = " + filePath);
                String renderedContent = hexoTemplate.doRender(filePath, basePageInfo);
                htmlResult.append(renderedContent);
            }

            return htmlResult.toString();
        });
        bindings.putMember("is_post", (ProxyExecutable) args -> {
            // 1. 从 JS 全局作用域获取当前的 page 对象
            Value page = bindings.getMember("page");

            // 2. 逻辑判断：如果 page 存在且 layout 属性等于 "post"
            if (page != null && page.hasMember("layout")) {
                return "post".equals(page.getMember("layout").asString());
            }

            // 3. 默认返回 false
            return false;
        });

        bindings.putMember("is_page", (ProxyExecutable) args -> {
            return !(basePageInfo instanceof ArticleListPageVO);
        });

        bindings.putMember("js_ex", (ProxyExecutable) args -> {
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
                return String.format("<script src=\"%s\"%s></script>", src + "/" + args[1], attributes);

            }
            return String.format("<script src=\"%s\"%s></script>", basePageInfo.getTemplateUrl() + "source" + src + "/" + args[1], attributes);

        });

        bindings.putMember("import_js", (ProxyExecutable) args -> {
            if (args.length < 2) return "";

            // 参数 1: base_path (例如 theme.static_prefix.internal_js)
            // 参数 2: file_name (例如 'local-search.js')
            String base = args[0].isNull() ? "" : args[0].asString();
            String file = args[1].asString();

            // 逻辑处理：拼接路径
            String src;
            if (base.isEmpty()) {
                // 如果没有前缀，通常默认在主题的 js 目录下
                src = "/js/" + file;
            } else {
                // 确保 base 和 file 之间有斜杠
                src = (base.endsWith("/") ? base : base + "/") + file;
            }

            // 返回标准的 HTML 标签
            return String.format("<script src=\"%s\"></script>", src);
        });


        bindings.putMember("import_script", (ProxyExecutable) args -> {
            // 2. 从上下文获取 page 对象
            // 注意：这里的 context 或 bindings 取决于你如何初始化渲染引擎的
            Map<String, Object> o = (Map<String, Object>) basePageInfo.getTheme().get("page");
            List<String> s = (List<String>) o.get("script_snippets");
            if (Objects.isNull(s)) {
                s = new ArrayList<>();
                o.put("script_snippets", s);
            }
            s.add(args[0].asString());
            return args[0].asString();
        });

        bindings.putMember("import_css", (ProxyExecutable) args -> {
            if (args.length < 2) return "";

            // 参数 1: base_path (例如 theme.static_prefix.internal_js)
            // 参数 2: file_name (例如 'local-search.js')
            String base = args[0].isNull() ? "" : args[0].asString();
            String file = args[1].asString();

            // 逻辑处理：拼接路径
            String src;
            if (base.isEmpty()) {
                // 如果没有前缀，通常默认在主题的 css 目录下
                src = "/css/" + file;
            } else {
                // 确保 base 和 file 之间有斜杠
                src = (base.endsWith("/") ? base : base + "/") + file;
            }

            String link = String.format("<link  rel=\"stylesheet\" href=\"%s\"/>", src);
            Map<String, Object> o = (Map<String, Object>) basePageInfo.getTheme().get("page");
            List<String> s = (List<String>) o.get("css_snippets");
            if (Objects.isNull(s)) {
                s = new ArrayList<>();
                o.put("css_snippets", s);
            }
            s.add(link);
            return link;
        });

        bindings.putMember("css_ex", (ProxyExecutable) args -> {
            if (args.length == 0 || args[0].isNull()) return "";

            // 1. 获取 CSS 路径 (例如: /css/main.css)
            String href = args[0].asString();

            if (!href.startsWith("http") && !href.startsWith("//")) {
                href = basePageInfo.getTemplateUrl() + "source" + href;
            }
            // 2. 初始化属性字符串
            StringBuilder attrs = new StringBuilder();

            // 3. 处理第二个可选参数 (配置对象，例如 { media: 'print' })
            if (args.length > 1) {
                Value options = args[1];
                href = href + (href.endsWith("/") ? "" : "/") + args[1];

                // 使用 isMemberReadable 安全读取属性
                if (options.hasMember("media")) {
                    String media = options.getMember("media").asString();
                    attrs.append(String.format(" media=\"%s\"", media));
                }

                if (options.hasMember("id")) {
                    attrs.append(String.format(" id=\"%s\"", options.getMember("id").asString()));
                }

                // 甚至可以处理自定义的 rel 属性
                if (options.hasMember("rel")) {
                    attrs.append(String.format(" rel=\"%s\"", options.getMember("rel").asString()));
                } else {
                    attrs.append(" rel=\"stylesheet\"");
                }
            } else {
                attrs.append(" rel=\"stylesheet\"");
            }

            // 4. 返回完整的 HTML 标签
            return String.format("<link %s href=\"%s\"/>", attrs.toString().trim(), href);
        });

        bindings.putMember("deduplicate", (ProxyExecutable) args -> {
            if ((args.length == 0) || args[0].isNull()) {
                return Collections.emptyList();
            }

            Value list = args[0];
            if (list.hasArrayElements()) {
                // 使用 LinkedHashSet 保持原始插入顺序并去重
                Set<Object> seen = new LinkedHashSet<>();
                long size = list.getArraySize();
                for (long i = 0; i < size; i++) {
                    Value element = list.getArrayElement(i);
                    // 根据实际情况，可能需要转换成 String 后去重
                    seen.add(element.as(Object.class));
                }
                return new ArrayList<>(seen);
            }

            return args[0]; // 如果不是数组，原样返回
        });

        bindings.putMember("in_scope", (ProxyExecutable) args -> {
            if (args.length == 0 || args[0].isNull()) return false;

            // 1. 获取当前页面的 layout (例如 "post", "index", "archive")
            Value page = bindings.getMember("page");
            if (page == null || !page.hasMember("layout")) return false;
            String currentLayout = page.getMember("layout").asString();

            // 2. 获取函数传入的作用域范围 (可能是一个字符串，也可能是一个数组)
            Value scope = args[0];

            // 情况 A: 传入的是数组 [ 'post', 'about' ]
            if (scope.hasArrayElements()) {
                long size = scope.getArraySize();
                for (long i = 0; i < size; i++) {
                    if (currentLayout.equals(scope.getArrayElement(i).asString())) {
                        return true;
                    }
                }
            }
            // 情况 B: 传入的是单个字符串 'post'
            else if (scope.isString()) {
                return currentLayout.equals(scope.asString());
            }

            return false;
        });

        bindings.putMember("open_graph", (ProxyExecutable) args -> {
            // 1. 获取全局配置和当前页面数据
            Value config = bindings.getMember("config");
            Value theme = bindings.getMember("theme");
            Value page = bindings.getMember("page");

            // 2. 提取基础信息
            String title = page.hasMember("title") ? page.getMember("title").asString() : config.getMember("title").asString();
            String author = config.hasMember("author") ? config.getMember("author").asString() : "";
            String description = page.hasMember("description") ? page.getMember("description").asString() : "";

            // 3. 构建 Meta 标签
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("<meta name=\"description\" content=\"%s\"/>\n", description));
            sb.append("<meta property=\"og:type\" content=\"website\"/>\n");
            sb.append(String.format("<meta property=\"og:title\" content=\"%s\"/>\n", title));
            sb.append(String.format("<meta property=\"og:author\" content=\"%s\"/>\n", author));
            sb.append("<meta name=\"twitter:card\" content=\"summary_large_image\"/>\n");

            // 如果有自定义参数（args[0] 通常是 options 对象），可以根据需要解析
            // 在基础实现中，直接返回这些核心标签即可
            return sb.toString();
        });

        bindings.putMember("export_config", (ProxyExecutable) args -> {
            // 1. 获取主题配置
            Value theme = bindings.getMember("theme");
            if (theme == null) return "";

            // 3. 将 Map 转换为 JSON 字符串 (建议使用 Jackson 或 Gson)
            String jsonConfig = FluidConfigExporter.getExportConfigJson(basePageInfo.getTheme(), basePageInfo.getTheme());

            // 4. 返回一段 JS 脚本，将配置挂载到全局变量 Fluid.ctx (Fluid 主题的约定)
            return String.format("<script id=\"fluid-configs\">\n" + "    var Fluid = window.Fluid || {};\n" + "    Fluid.ctx = %s;\n" + "    var CONFIG = %s;\n" + "</script>", jsonConfig, jsonConfig);
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

        // 注入 decode_url
        bindings.putMember("decode_url", (ProxyExecutable) args -> {
            if (args.length == 0 || args[0].isNull()) return "";
            String url = args[0].asString();
            try {
                // 使用标准的 URLDecoder 进行解码
                return java.net.URLDecoder.decode(url, java.nio.charset.StandardCharsets.UTF_8);
            } catch (Exception e) {
                return url; // 解码失败则返回原字符串
            }
        });

        // 建议顺便把 encode_url 也实现了，防止后续报错
        bindings.putMember("encode_url", (ProxyExecutable) args -> {
            if (args.length == 0 || args[0].isNull()) return "";
            String url = args[0].asString();
            try {
                return java.net.URLEncoder.encode(url, java.nio.charset.StandardCharsets.UTF_8).replace("+", "%20"); // 兼容 Hexo/Node.js 的空格处理
            } catch (Exception e) {
                return url;
            }
        });

        bindings.putMember("prev_post", (ProxyExecutable) args -> {
            return "";
        });

        bindings.putMember("next_post", (ProxyExecutable) args -> {
            return "";
        });
    }
}
