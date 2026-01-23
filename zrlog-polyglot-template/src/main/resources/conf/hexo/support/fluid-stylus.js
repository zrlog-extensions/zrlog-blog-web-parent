// 注意：nodes 是 stylus.nodes，在 define 的上下文中可以获取
renderer.define('hexo-config', function (pathNode) {
    // 1. 从 Java 拿原始数据
    var raw = hexo_config_java(pathNode.val);

    // 2. 核心：根据原始类型，封装成 Stylus 识别的 AST 节点
    if (typeof raw === 'boolean') {
        // 这样 Stylus 的 if (hexo-config('...')) 才能正常工作
        return new nodes.Boolean(raw);
    }

    if (typeof raw === 'number') {
        // 这样 hexo-config('...') * 2 这种算术运算才不会崩
        return new nodes.Unit(raw);
    }

    // 3. 默认作为字符串处理
    // String(raw || '') 确保即使 Java 返回 null 也不会导致 node 实例化失败
    var strNode = new nodes.String(String(raw || ''));

    // 如果值看起来像个颜色（比如 #fff），有时需要 unquote 让它参与颜色运算
    // 但通常作为 String 返回是最稳妥的
    return strNode;
});