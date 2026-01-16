'use strict';

/**
 * 轻量版 url.parse 实现
 * 模拟 Node.js 的返回结构
 */
function parse(urlStr) {
    if (!urlStr) return {};

    try {
        // 使用标准的 URL 构造函数（GraalJS 环境自带）
        const parsed = new URL(urlStr, 'http://dummy.com');

        return {
            protocol: urlStr.includes('://') ? parsed.protocol : null,
            auth: parsed.username ? (parsed.username + (parsed.password ? ':' + parsed.password : '')) : null,
            host: urlStr.includes('://') ? parsed.host : null,
            port: parsed.port || null,
            hostname: urlStr.includes('://') ? parsed.hostname : null,
            hash: parsed.hash || null,
            search: parsed.search || null,
            query: parsed.search ? parsed.search.substring(1) : null,
            pathname: parsed.pathname,
            path: parsed.pathname + parsed.search,
            href: parsed.href
        };
    } catch (e) {
        // 如果不是绝对路径，做简单的字符串切割
        const parts = urlStr.split(/[?#]/);
        return {
            pathname: parts[0],
            path: urlStr,
            search: parts[1] ? '?' + parts[1] : null,
            query: parts[1] || null,
            hash: parts[2] ? '#' + parts[2] : null
        };
    }
}

// 导出对象
module.exports = {
    parse: parse
};