package com.zrlog.blog.hexo.template.support.butterfly;

import com.zrlog.blog.hexo.template.HexoObjectBox;
import com.zrlog.blog.hexo.template.util.HexoDataUtils;
import com.zrlog.blog.polyglot.resource.ScriptProvider;
import com.zrlog.blog.web.template.vo.ArticleListPageVO;
import com.zrlog.blog.web.template.vo.BasePageInfo;
import com.zrlog.common.cache.dto.LogNavDTO;
import com.zrlog.common.vo.TemplateVO;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyExecutable;

import java.util.*;

public class ButterflyHexoObjectBox extends HexoObjectBox {

    public ButterflyHexoObjectBox(Map<String, Object> theme, String rootPath, BasePageInfo basePageInfo, TemplateVO templateVO) {
        super(theme, rootPath, basePageInfo, templateVO);
    }

    @Override
    protected void fillConfig() {
        theme.put("asset", Map.of("main_css", basePageInfo.getTemplateUrl() + getStylRoot() + "/index.css", "fontawesome", basePageInfo.getTemplateUrl() + getStylRoot() + "/fontawesome.css"));
        //Map<String, Object> o = (Map<String, Object>) theme.get("site");
        //o.put("data", Map.of("article",theme.get("posts")));
        Map<String, Object> config = (Map<String, Object>) theme.get("config");
        config.put("author", basePageInfo.getWebs().getTitle());
        config.put("prismjs", Map.of("enable", false));
        config.put("highlight", Map.of("enable", false));
        /*if (basePageInfo instanceof ArticleListPageVO) {
            Map<String, Object> page = (Map<String, Object>) theme.get("page");
            List<Map<String, Object>> posts = (List<Map<String, Object>>) page.get("posts");
            List<Map<String, Object>> wraps = new ArrayList<>();

        }*/
        Map<String, Object> menu = new LinkedHashMap<>();
        for (LogNavDTO logNav : basePageInfo.getInit().getLogNavs()) {
            menu.put(logNav.getNavName(), logNav.getUrl() + " || " + logNav.getIcon());
        }
        theme.put("menu", menu);
        Map<String, Object> site = (Map<String, Object>) theme.get("site");
        site.put("data", Map.of("widget", Map.of("title", basePageInfo.getWebs().getTitle())));

        fixImageUrl("avatar", "img");
        fixImageUrl("error_img", "flink");
        fixImageUrl("error_img", "post_img");
        fixImageUrl("error_404", "background");
    }

    @Override
    public void initScript(ScriptProvider scriptProvider) {
        //scriptProvider.addBaseScript("package.json", ZrLogResourceLoader.read(rootPath + "/package.json"));
    }

    @Override
    protected void regisConfig(Value bindings) {

        bindings.putMember("full_url_for", (ProxyExecutable) args -> {
            return args[0];
        });
        bindings.putMember("fragment_cache", (ProxyExecutable) args -> {
            return "";
        });
        bindings.putMember("getVersion", (ProxyExecutable) args -> {
            return Map.of("hexo", "-", "theme", templateVO.getVersion());
        });
    }

    @Override
    public List<String> getCompileStyl() {
        return Arrays.asList("/index.styl");
    }
}
