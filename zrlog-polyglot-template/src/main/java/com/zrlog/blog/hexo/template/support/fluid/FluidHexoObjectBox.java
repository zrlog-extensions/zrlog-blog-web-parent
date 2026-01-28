package com.zrlog.blog.hexo.template.support.fluid;

import com.hibegin.http.server.util.PathUtil;
import com.zrlog.blog.hexo.template.HexoObjectBox;
import com.zrlog.blog.hexo.template.impl.HexoCssExImpl;
import com.zrlog.blog.hexo.template.impl.HexoJsExImpl;
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

public class FluidHexoObjectBox extends HexoObjectBox {

    private final InjectionStorage injectionStorage;

    public FluidHexoObjectBox(Map<String, Object> theme, String rootPath, BasePageInfo basePageInfo, TemplateVO templateVO, String templateDir) {
        super(theme, rootPath, basePageInfo, templateVO);
        this.injectionStorage = new InjectionStorage(new ConcurrentHashMap<>(), templateDir);
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
                bindings.putMember(name, new HexoJsExImpl(basePageInfo));
                return true;
            }
            case "css_ex" -> {
                bindings.putMember(name, new HexoCssExImpl(basePageInfo));
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> getCompileStyl() {
        return Arrays.asList("main.styl"/*, "highlight.styl", "highlight-dark.styl"*/);
    }

    @Override
    public void regStyleHooks(Context context) throws Exception {
        context.eval("js", new String(PathUtil.getConfInputStream("hexo/support/fluid-stylus.js").readAllBytes()));
        // context.eval("js", "renderer.define('theme-config', function(pathNode) {" + "  return hexo_config_java(pathNode.val);" + "});");
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
            String webCm = "<div style='display:none'>" + basePageInfo.getWebs().getWebCm() + "</div>";
            if (Objects.nonNull(content) && content.toString().contains("Hexo")) {
                content = content.toString().replace("Hexo", "ZrLog").replace("hexo.io", "www.zrlog.com");
            }
            footer.put("content", content + webCm);
        }
    }

    @Override
    protected void regisConfig(Value bindings) {
        bindings.putMember("hexo_config_java", new FluidConfigProxy(this.theme));
        bindings.putMember("fluid_version", templateVO.getVersion());
        Map<String, Object> nestedValue = (Map<String, Object>) YamlLoader.getNestedValue(theme, "index.slogan");
        if (Objects.nonNull(nestedValue)) {
            nestedValue.put("text", basePageInfo.getWebs().getSecond_title());
        }
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
