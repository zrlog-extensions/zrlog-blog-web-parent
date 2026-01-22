package com.zrlog.blog.hexo.template;

import com.zrlog.blog.hexo.template.support.butterfly.ButterflyHexoObjectBox;
import com.zrlog.blog.hexo.template.support.fluid.FluidHexoObjectBox;
import com.zrlog.blog.polyglot.JsTemplateRender;
import com.zrlog.blog.polyglot.ejs.EjsTemplateRender;
import com.zrlog.blog.polyglot.pug.PugTemplateRender;
import com.zrlog.blog.polyglot.util.YamlLoader;
import com.zrlog.blog.web.template.ZrLogTemplate;
import com.zrlog.blog.web.template.vo.BasePageInfo;
import com.zrlog.business.type.TemplateType;
import com.zrlog.common.Constants;
import com.zrlog.common.exception.NotImplementException;
import com.zrlog.common.resource.ZrLogResourceLoader;
import com.zrlog.common.vo.TemplateVO;

import java.io.File;
import java.util.Map;
import java.util.Objects;

public class HexoTemplate implements ZrLogTemplate {
    private String template;
    private String rootPath;
    private final TemplateVO templateVO;

    public String getTemplate() {
        return template;
    }

    public HexoTemplate(TemplateVO templateVO) {
        this.templateVO = templateVO;
    }

    @Override
    public void init(File path) throws Exception {
        this.rootPath = path.getAbsolutePath();
        setup();
    }

    @Override
    public String render(String page, BasePageInfo pageInfo) throws Exception {
        Map<String, Object> configMap = pageInfo.getInit().getTemplateConfigCacheMap().get(pageInfo.getTemplate());
        Map<String, Object> config;
        if (Objects.nonNull(configMap) && configMap.containsKey(Constants.TEMPLATE_CONFIG_STR_KEY)) {
            config = YamlLoader.loadConfig((String) configMap.get(Constants.TEMPLATE_CONFIG_STR_KEY));
        } else {
            config = YamlLoader.loadConfig(ZrLogResourceLoader.read(rootPath + "/" + TemplateType.NODE_JS.getConfigFile()));
        }
        Map<String, Object> theme = HexoPageConverter.toThemeMap(pageInfo, page, config);
        HexoObjectBox hexoObjectBox = buildHexoObjectByTemplate(theme, pageInfo);
        try (JsTemplateRender jsTemplateRender = buildJsTemplateRender(theme, pageInfo)) {
            hexoObjectBox.setup(jsTemplateRender);
            String body = jsTemplateRender.render((String) YamlLoader.getNestedValue(theme, "page.layout"), theme);
            if (jsTemplateRender instanceof EjsTemplateRender) {jsTemplateRender.getJsBindings().putMember("body", body);

                return jsTemplateRender.render("layout", theme);
            }
            return body;
        }
    }

    private HexoObjectBox buildHexoObjectByTemplate(Map<String, Object> theme, BasePageInfo pageInfo) {
        if (this.templateVO.getTemplate().endsWith("/hexo-theme-fluid")) {
            return new FluidHexoObjectBox(theme, rootPath, pageInfo, templateVO, template);
        }
        if (this.templateVO.getTemplate().endsWith("/hexo-theme-butterfly")) {
            return new ButterflyHexoObjectBox(theme, rootPath, pageInfo, templateVO);
        }
        return new HexoObjectBox(theme, rootPath, pageInfo, templateVO);
    }

    private JsTemplateRender buildJsTemplateRender(Map<String, Object> theme, BasePageInfo pageInfo) {
        if (templateVO.getViewType().equals(".ejs")) {
            return new EjsTemplateRender(template, pageInfo, theme);
        }
        if (templateVO.getViewType().equals(".pug")) {
            return new PugTemplateRender(template, pageInfo, theme);
        }
        throw new NotImplementException();
    }

    private void setup() {
        this.template = (rootPath + "/layout");
    }

    @Override
    public void initClassTemplate(String templateBase) {
        this.rootPath = "classpath:" + templateBase;
        setup();
    }
}