package com.zrlog.blog.hexo.template.support.fluid;

import com.zrlog.blog.hexo.template.HexoObjectBox;
import com.zrlog.blog.polyglot.JsTemplateRender;
import com.zrlog.blog.polyglot.util.YamlLoader;
import com.zrlog.blog.web.template.vo.ArticleDetailPageVO;
import com.zrlog.blog.web.template.vo.BasePageInfo;
import com.zrlog.common.vo.TemplateVO;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyExecutable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class FluidHexoObjectBox extends HexoObjectBox {

    private final InjectionStorage injectionStorage;

    public FluidHexoObjectBox(Map<String, Object> theme, String rootPath, BasePageInfo basePageInfo, TemplateVO templateVO) {
        super(theme, rootPath, basePageInfo, templateVO);
        this.injectionStorage = new InjectionStorage(new ConcurrentHashMap<>(), rootPath);
    }

    @Override
    protected boolean filterRegister(Context context, String name, Value[] values) {
        if (name.equals("theme_inject")) {
            Value callback = values[1];
            // 关键：当脚本运行回调时，传入我们构造的 injects 代理对象
            callback.execute(createInjectsProxy(context));
            return true;
        }

        return false;
    }

    @Override
    protected boolean helperRegister(JsTemplateRender jsTemplateRender, String name, Value[] values) {
        Value bindings = jsTemplateRender.getJsBindings();
        switch (name) {
            case "inject_point" -> {
                bindings.putMember(name, (ProxyExecutable) args -> {
                    if (args.length == 0) return "";
                    String pointName = args[0].asString();

                    // 1. 从之前 setup 阶段填充的 injectionPoints Map 中获取注册的文件路径列表
                    // 这里的 injectionPoints 是你存储 List<String> 路径的那个全局 Map
                    List<String> filePaths = injectionStorage.get(pointName);

                    if (filePaths == null || filePaths.isEmpty()) {
                        return "";
                    }

                    StringBuilder htmlResult = new StringBuilder();
                    for (String filePath : filePaths) {
                        String renderedContent = jsTemplateRender.render(filePath, null);
                        htmlResult.append(renderedContent);
                    }

                    return htmlResult.toString();
                });
                return true;
            }
            case "js_ex" -> {
                jsEx(bindings);
                return true;
            }
            case "css_ex" -> {
                cssEx(bindings);
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> getCompileStyl() {
        return Arrays.asList("/main.styl", "/highlight.styl", "/highlight-dark.styl", "/style.styl");
    }

    @Override
    public void regStyleHooks(Context context) {
        context.eval("js", "renderer.define('hexo-config', function(pathNode) {" + "  return hexo_config_java(pathNode.val);" + "});");
    }

    @Override
    protected void fillConfig() {
        for (String key : Arrays.asList("index", "page", "tag", "about", "page404", "links", "archive", "post", "category")) {
            fixImageUrl(key, "banner_img");
        }

        Map<String, Object> config = (Map<String, Object>) theme.get("config");
        Map<String, Object> indexGen = (Map<String, Object>) config.computeIfAbsent("index_generator", k -> new HashMap<>());
        indexGen.putIfAbsent("order_by", "name");

        if (basePageInfo instanceof ArticleDetailPageVO) {
            Map<String, Object> comments = (Map<String, Object>) YamlLoader.getNestedValue(config, "post.comments");
            if (Objects.equals(((ArticleDetailPageVO) basePageInfo).getLog().getCanComment(), true) && Objects.nonNull(comments)) {
                comments.put("type", basePageInfo.getWebs().getComment_plugin_name());
                comments.put("enable", true);
            }
        }
        Map<String, Object> footer = (Map<String, Object>) YamlLoader.getNestedValue(config, "footer");
        if (Objects.nonNull(footer)) {
            Object content = footer.get("content");
            if (Objects.nonNull(content) && content.toString().contains("Hexo")) {
                footer.put("content", content.toString().replace("Hexo", "hexo").replace("hexo.io", "www.zrlog.com"));
            }
        }
    }

    private void fixImageUrl(String rootKey, String valueName) {
        Map<String, Object> map = (Map<String, Object>) theme.get(rootKey);
        if (Objects.isNull(map)) {
            return;
        }
        Object value = map.get(valueName);
        if (Objects.isNull(value)) {
            return;
        }
        if (value instanceof String) {
            if (((String) value).startsWith("http://") || ((String) value).startsWith("https://")) {
                return;
            }
            if (((String) value).startsWith("/img")) {
                map.put(valueName, basePageInfo.getTemplateUrl() + "/source" + value);
            }
        }
    }

    @Override
    protected void regisConfig(Value bindings) {
        bindings.putMember("hexo_config_java", (Function<String, Object>) key -> {
            if (Objects.equals("injects.variable", key)) {
                return new ArrayList<>();
            }
            if (Objects.equals("injects.mixin", key)) {
                return new ArrayList<>();
            }
            if (Objects.equals("injects.style", key)) {
                return new ArrayList<>();
            }
            return YamlLoader.getNestedValue(theme, key);
        });
        bindings.putMember("fluid_version", templateVO.getVersion());
        Map<String, Object> nestedValue = (Map<String, Object>) YamlLoader.getNestedValue(theme, "index.slogan");
        nestedValue.put("text", basePageInfo.getWebs().getSecond_title());
    }

    private String buildJsScript(Value... args) {
        if (args.length == 0) return "";

        // 1. 获取脚本路径 (例如: /js/main.js)
        String src = args[0].asString();

        // 2. 处理第二个参数 (属性对象，例如 { async: true, defer: true })
        StringBuilder attributes = new StringBuilder();

        if (args.length > 1 && !args[1].isNull()) {
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
            return String.format("<script src=\"%s\"%s></script>", src + args[1], attributes);

        }
        return String.format("<script src=\"%s\"%s></script>", basePageInfo.getTemplateUrl() + "/source" + src + "/" + args[1], attributes);
    }

    private void jsEx(Value bindings) {
        bindings.putMember("js_ex", (ProxyExecutable) args -> {
            return buildJsScript(args[0], args[1]);
        });
    }

    private void cssEx(Value bindings) {
        bindings.putMember("css_ex", (ProxyExecutable) args -> {
            if (args.length == 0 || args[0].isNull()) return "";

            // 1. 获取 CSS 路径 (例如: /css/main.css)
            String href = args[0].asString();

            if (!href.startsWith("http") && !href.startsWith("//")) {
                href = basePageInfo.getTemplateUrl() + "/source" + href;
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
    }

    /**
     * 创建一个动态代理对象，捕获 injects.xxx.file() 的调用
     */
    private Value createInjectsProxy(Context context) {
        // 这是一个“万能插槽”处理器，处理诸如 injects.header.file(...) 的调用
        return context.eval("js", "(function(storage) {" + "  return new Proxy({}, {" + "    get: function(target, slot) {" + "      return {" + "        file: function(name, path) {" + "          storage.add(slot, path);" + // 调用 Java 的 add 方法
                "        }" + "      };" + "    }" + "  });" + "})").execute(injectionStorage);
    }
}
