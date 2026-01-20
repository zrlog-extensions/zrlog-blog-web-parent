package com.zrlog.blog.hexo.template;

import com.zrlog.blog.hexo.template.fluid.FluidHexoObjectBox;
import com.zrlog.blog.polyglot.JsTemplateRender;
import com.zrlog.blog.polyglot.ejs.EjsTemplateRender;
import com.zrlog.blog.polyglot.resource.ZrLogResourceLoader;
import com.zrlog.blog.polyglot.util.GraalDataUtils;
import com.zrlog.blog.polyglot.util.YamlLoader;
import com.zrlog.blog.web.template.ZrLogTemplate;
import com.zrlog.blog.web.template.vo.BasePageInfo;
import com.zrlog.business.type.TemplateType;
import com.zrlog.common.Constants;
import com.zrlog.common.vo.TemplateVO;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HexoTemplate implements ZrLogTemplate {
    private String template;
    private String rootPath;
    private BasePageInfo pageInfo;
    private TemplateVO templateVO;
    private JsTemplateRender jsTemplateRender;

    public String getTemplate() {
        return template;
    }

    public String getRootPath() {
        return rootPath;
    }

    public BasePageInfo getPageInfo() {
        return pageInfo;
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
        this.pageInfo = pageInfo;
        Map<String, Object> configMap = pageInfo.getInit().getTemplateConfigCacheMap().get(pageInfo.getTemplate());
        Map<String, Object> config;
        if (Objects.nonNull(configMap) && configMap.containsKey(Constants.TEMPLATE_CONFIG_STR_KEY)) {
            config = YamlLoader.loadConfig((String) configMap.get(Constants.TEMPLATE_CONFIG_STR_KEY));
        } else {
            config = YamlLoader.loadConfig(ZrLogResourceLoader.read(rootPath + "/" + TemplateType.NODE_JS.getConfigFile()));
        }
        Map<String, Object> theme = new HashMap<>(HexoPageConverter.toThemeMap(pageInfo, page, config));
        FluidHexoObjectBox fluidHexoObjectBox = new FluidHexoObjectBox(theme, rootPath, pageInfo, templateVO);
        this.jsTemplateRender = new EjsTemplateRender(this.template, pageInfo, theme);
        fluidHexoObjectBox.setup(jsTemplateRender);
        this.getJsTemplateRender().getJsBindings().putMember("theme", GraalDataUtils.makeJsFriendly(theme));
        this.getJsTemplateRender().getJsBindings().putMember("body", jsTemplateRender.render((String) YamlLoader.getNestedValue(theme, "page.layout"), theme));
        return jsTemplateRender.render("/layout", theme);
    }

    private void setup() {
        this.template = (rootPath + "/layout");
    }

    @Override
    public void initClassTemplate(String templateBase) {
        this.rootPath = "classpath:" + templateBase;
        setup();
    }

    public JsTemplateRender getJsTemplateRender() {
        return jsTemplateRender;
    }
}