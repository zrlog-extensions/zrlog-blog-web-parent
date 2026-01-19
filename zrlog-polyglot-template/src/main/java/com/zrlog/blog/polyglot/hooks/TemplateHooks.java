package com.zrlog.blog.polyglot.hooks;

import com.zrlog.blog.polyglot.JsTemplateRender;
import com.zrlog.blog.polyglot.resource.TemplateResolver;
import com.zrlog.blog.polyglot.resource.ZrLogResourceLoader;
import com.zrlog.blog.web.template.vo.ArticleDetailPageVO;
import com.zrlog.blog.web.template.vo.BasePageInfo;
import org.graalvm.polyglot.Value;

import java.util.Map;
import java.util.Objects;

// Java 侧定义 Hook 逻辑
public class TemplateHooks {

    private final JsTemplateRender jsTemplateRender;
    private final TemplateResolver resolver;
    private final BasePageInfo basePageInfo;

    public TemplateHooks(JsTemplateRender jsTemplateRender, TemplateResolver resolver, BasePageInfo basePageInfo) {
        this.jsTemplateRender = jsTemplateRender;
        this.resolver = resolver;
        this.basePageInfo = basePageInfo;
    }

    public String handleInclude(String path, Value options) {
        String absolutePath = resolver.resolve(path);
        resolver.pushPath(absolutePath);
        Map<String, Object> data = (Objects.nonNull(options) && !options.isNull()) ? options.as(Map.class) : jsTemplateRender.getLocals();
        // 注意：Hexo 的 partial 路径通常是相对于当前模板目录的
        try {
            //拦截
            if (path.equals("_partials/comments/comment") && basePageInfo instanceof ArticleDetailPageVO) {
                return "<plugin name=\"" + basePageInfo.getWebs().getComment_plugin_name() + "\" view=\"widget\" param=\"articleId=" + ((ArticleDetailPageVO) basePageInfo).getLog().getLogId() + "\"/>\n";
            }
            // 3. 递归渲染
            // 注意：这里需要确保 render 方法不会清空之前的全局 helpers
            String renderPath = absolutePath.substring((jsTemplateRender.getTemplate() + "/").length());
            //System.out.println("renderPath = " + renderPath);
            String testPath = jsTemplateRender.getTemplate() + "/" + path + (path.contains(jsTemplateRender.getTemplateExt()) ? "" : jsTemplateRender.getTemplateExt());
            //System.out.println("testPath = " + testPath);
            //为绝对路径，不关心路径
            if (ZrLogResourceLoader.exists(testPath)) {
                return jsTemplateRender.render(path, data);
            } else {
                return jsTemplateRender.render(renderPath, data);
            }
        } finally {
            // 3. 必须出栈，否则路径上下文会乱
            resolver.popPath();
        }
    }
}