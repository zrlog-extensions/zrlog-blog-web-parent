package com.zrlog.blog.hexo.template.util;

import com.zrlog.blog.hexo.template.HexoTemplate;
import com.zrlog.blog.hexo.template.ejs.TemplateResolver;
import com.zrlog.blog.hexo.template.impl.HexoHelperImpl;
import com.zrlog.blog.hexo.template.impl.HexoI18nHelperImpl;
import com.zrlog.blog.hexo.template.impl.HexoPaginator;
import com.zrlog.blog.hexo.template.impl.HexoTagCloud;
import com.zrlog.blog.web.template.vo.ArticleDetailPageVO;
import com.zrlog.blog.web.template.vo.ArticleListPageVO;
import com.zrlog.blog.web.template.vo.BasePageInfo;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyExecutable;

import java.util.*;

public class HexoBaseHooks implements HexoHooks {

    private final BasePageInfo basePageInfo;
    private final TemplateResolver templateResolver;
    private final HexoTemplate hexoTemplate;

    public HexoBaseHooks(BasePageInfo basePageInfo, TemplateResolver templateResolver, HexoTemplate hexoTemplate) {
        this.basePageInfo = basePageInfo;
        this.templateResolver = templateResolver;
        this.hexoTemplate = hexoTemplate;
    }

    @Override
    public void inject(Value bindings) {
        HexoHelperImpl hexoHelper = new HexoHelperImpl(hexoTemplate, templateResolver, basePageInfo);
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
                List<Object> objects = new ArrayList<>(Arrays.asList(args).subList(1, args.length));
                return new HexoI18nHelperImpl(hexoTemplate, basePageInfo.getLocal()).i18n(key, objects);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

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
            return basePageInfo instanceof ArticleDetailPageVO;
        });

        bindings.putMember("is_page", (ProxyExecutable) args -> {
            return basePageInfo instanceof ArticleListPageVO;
        });

        bindings.putMember("is_home", (ProxyExecutable) args -> {
            return Objects.equals(YamlLoader.getNestedValue(basePageInfo.getTheme(), "page.layout"), "/index");
        });
    }
}
