package com.zrlog.blog.hexo.template;

import com.zrlog.blog.hexo.template.fluid.FluidHexoObjectBox;
import com.zrlog.blog.polyglot.JsTemplateRender;
import com.zrlog.blog.polyglot.ejs.EjsTemplateRender;
import com.zrlog.blog.polyglot.resource.ZrLogResourceLoader;
import com.zrlog.blog.polyglot.util.YamlLoader;
import com.zrlog.blog.web.template.ZrLogTemplate;
import com.zrlog.blog.web.template.vo.BasePageInfo;
import com.zrlog.business.type.TemplateType;
import com.zrlog.common.Constants;
import org.graalvm.polyglot.Context;

import java.io.File;
import java.util.Map;
import java.util.Objects;

public class HexoTemplate implements ZrLogTemplate {
    private String template;
    private String rootPath;
    private Map<String, Object> locals;
    private BasePageInfo pageInfo;
    private JsTemplateRender jsTemplateRender;


    public Map<String, Object> getLocals() {
        return locals;
    }

    public String getTemplate() {
        return template;
    }

    public String getRootPath() {
        return rootPath;
    }

    public BasePageInfo getPageInfo() {
        return pageInfo;
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
        pageInfo.setTheme(config);
        this.locals = HexoPageConverter.toHexoMap(pageInfo, page);
        new FluidHexoObjectBox(config, rootPath, this).setup();
        this.locals.put("body", jsTemplateRender.render((String) YamlLoader.getNestedValue(locals, "page.layout"), locals));
        return jsTemplateRender.render("/layout", locals);
    }

    private void setup() {
        this.template = (rootPath + "/layout");
        this.jsTemplateRender = new EjsTemplateRender(this.template);
    }

    @Override
    public void initClassTemplate(String templateBase) {
        this.rootPath = "classpath:" + templateBase;
        setup();
    }

    public JsTemplateRender getJsTemplateRender() {
        return jsTemplateRender;
    }

    public Context getContext() {
        return jsTemplateRender.getContext();
    }

    public String getTemplateExt() {
        return jsTemplateRender.getTemplateExt();
    }
}