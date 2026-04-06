package com.zrlog.blog.polyglot.util;

import com.zrlog.blog.hexo.template.InjectionStorage;
import com.zrlog.blog.hexo.template.JsMapAdapter;
import com.zrlog.blog.hexo.template.util.HexoDateObjUtils;
import com.zrlog.blog.polyglot.njk.NativeJavaLoader;
import com.zrlog.blog.polyglot.resource.ScriptProvider;
import com.zrlog.common.Constants;
import org.graalvm.polyglot.Context;

import java.lang.reflect.Method;
import java.util.HashMap;

public class PolyglotNativeImageUtils {

    public static void reg() {
        try {
            Method add = InjectionStorage.class.getMethod("add", String.class, String.class);
            add.invoke(new InjectionStorage(null, null), Constants.TEMPLATE_BASE_PATH + "test", Constants.TEMPLATE_BASE_PATH + "test");
        } catch (Throwable e) {
            e.printStackTrace();
        }
        try {
            Method isRelative = NativeJavaLoader.class.getMethod("isRelative", String.class);
            Method getSource = NativeJavaLoader.class.getMethod("getSource", String.class);
            Method resolve = NativeJavaLoader.class.getMethod("resolve", String.class, String.class);
            NativeJavaLoader obj = new NativeJavaLoader("");
            isRelative.invoke(obj, "/test");
            getSource.invoke(obj, "/test");
            resolve.invoke(obj, "/test", "a");
        } catch (Throwable e) {
            e.printStackTrace();
        }

        try {
            Method has = JsMapAdapter.class.getMethod("has", String.class);
            Method get = JsMapAdapter.class.getMethod("get", String.class);
            Method size = JsMapAdapter.class.getMethod("size");
            JsMapAdapter obj = new JsMapAdapter(new HashMap<>());
            has.invoke(obj, "/test");
            get.invoke(obj, "/test");
            size.invoke(obj);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        try {
            Method load = ScriptProvider.class.getMethod("load", String.class);
            load.invoke(new ScriptProvider(), "path");
        } catch (Throwable e) {
            e.printStackTrace();
        }
        try {
            HexoDateObjUtils.getInstance();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        try (Context context = PolyglotContextUtils.buildJsContext()) {
            // 必须包含触发 ICU 反射的逻辑：日期格式化、本地化字符串等
            context.eval("js",
                    "console.log(new Date().toISOString()); " +
                            "console.log(new Date().toLocaleString('zh-CN')); " +
                            "console.log(Intl.DateTimeFormat().resolvedOptions().timeZone);"
            );
            System.out.println("模拟调用完成，Agent 应该已经记录了元数据。");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        reg();
    }
}
