'use strict';

// 移除 HTML 标签
function stripHTML(content) {
    if (!content) return '';
    return content.toString().replace(/<[^>]+>/g, '').trim();
}

// 解码 URL
function decodeURL(str) {
    if (!str) return '';
    try {
        return decodeURIComponent(str);
    } catch (e) {
        // 如果编码格式有问题，返回原字符串
        return str;
    }
}

function Cache(str) {

}

function htmlTag(tag, attrs, text, escape = true) {
    if (!tag) return '';

    let result = `<${tag}`;

    // 1. 处理属性 (Attributes)
    for (const i in attrs) {
        if (attrs[i] === null || attrs[i] === undefined) continue;

        // 对属性值进行 HTML 转义，防止 XSS 或格式破碎
        result += ` ${i}="${escapeHTML(String(attrs[i]))}"`;
    }

    // 2. 处理内容 (Text/Inner HTML)
    if (text === null || text === undefined || text === '') {
        // 检查是否为自闭合标签 (void elements)
        if (['meta', 'img', 'link', 'br', 'hr', 'input'].includes(tag)) {
            return result + '>';
        }
        return result + `></${tag}>`;
    }

    // 根据参数决定是否对内容进行转义
    const content = escape ? escapeHTML(String(text)) : text;

    return result + `>${content}</${tag}>`;
}

const MAP = {
    '&': '&amp;',
    '<': '&lt;',
    '>': '&gt;',
    '"': '&quot;',
    "'": '&#39;'
};

function escapeHTML(str) {
    if (typeof str !== 'string') str = String(str);

    // 使用正则匹配这五个特殊字符
    return str.replace(/[&<>"']/g, (s) => MAP[s]);
}

function truncate(string, {length}) {
    return string.substring(0, Math.min(length, string.length));
}

// 导出为对象
module.exports = {
    stripHTML: stripHTML,
    escapeHTML: escapeHTML,
    decodeURL: decodeURL,
    Cache: Cache,
    htmlTag: htmlTag,
    truncate: truncate,
};