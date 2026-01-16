// 这一段是 Java 实现 require 的“外壳”
global.require = function (id) {
    const source = scriptProvider.load(id);

    const module = { exports: {} }; // 1. 创建 module 对象

    // 2. 传入 require, module, 以及 module.exports
    const wrapper = new Function('require', 'module', 'exports', source);

    // 3. 执行脚本。脚本内部会修改 module.exports
    wrapper(global.require, module, module.exports);

    // 4. 关键：必须返回执行后的 module.exports！
    return module.exports;
};