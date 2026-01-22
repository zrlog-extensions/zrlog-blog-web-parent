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

function htmlTag(str) {

}

function truncate(string, {length}) {
    return string.substring(0, Math.min(length, string.length));
}

// 导出为对象
module.exports = {
    stripHTML: stripHTML,
    decodeURL: decodeURL,
    Cache: Cache,
    htmlTag: htmlTag,
    truncate: truncate,
};