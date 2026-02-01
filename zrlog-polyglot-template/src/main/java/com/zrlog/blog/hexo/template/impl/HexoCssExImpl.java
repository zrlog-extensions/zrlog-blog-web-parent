package com.zrlog.blog.hexo.template.impl;

import com.zrlog.blog.web.template.vo.BasePageInfo;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyExecutable;

public class HexoCssExImpl implements ProxyExecutable {

    private final BasePageInfo basePageInfo;

    public HexoCssExImpl(BasePageInfo basePageInfo) {
        this.basePageInfo = basePageInfo;
    }

    @Override
    public Object execute(Value... args) {
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
    }
}
