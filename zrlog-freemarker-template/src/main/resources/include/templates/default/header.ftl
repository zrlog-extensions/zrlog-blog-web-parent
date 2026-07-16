<!DOCTYPE html>
<html lang="${lang}" data-theme="light">
<head>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <meta name="color-scheme" content="light dark"/>
    <title>${title!''}</title>
    <link rel="shortcut icon" type="image/x-icon" href="${baseUrl}favicon.ico"/>
    <meta name="description" content="${description!''}"/>
    <meta name="keywords" content="${keywords!''}"/>
    ${socialPreviewHtml!''}
    <link rel="stylesheet" href="${baseUrl}assets/css/markdown.css"/>
    <link rel="stylesheet" href="${baseUrl}assets/css/pretty-print.css"/>
    <link rel="stylesheet" href="${baseUrl}assets/css/katex.min.css"/>
    <#include "_common/auto-hljs.ftl"/>
    <#include "_common/icons.ftl"/>
    <link rel="stylesheet" type="text/css" href="${url}/css/style_v3.css"/>
    <script src="${url}/js/auto-theme.js"></script>
    ${_res.globalStyle!''}
</head>
<body>
<a class="skip-link" href="#main-content">${_res.skipToContent}</a>
<header class="site-header <#if _res.navStickyTop?? && _res.navStickyTop>site-header-sticky</#if>"
        style="--brand-color: ${_res.navBg!'#2563eb'}">
    <div class="site-header-inner">
        <a class="site-brand" href="${rurl}" aria-label="${webs.title}">
            <span><#if _res.navBarBrand?has_content>${_res.navBarBrand}<#else>${webs.title}</#if></span>
        </a>
        <button class="menu-toggle" type="button" aria-label="${_res.menu}" aria-expanded="false" aria-controls="primary-navigation">
            <span class="menu-toggle-lines" aria-hidden="true"></span>
            <span>${_res.menu}</span>
        </button>
        <nav class="site-navigation" id="primary-navigation" aria-label="${_res.primaryNavigation}" data-open="false">
            <ul>
                <#list init.logNavs as lognav>
                    <li>
                        <a href="${lognav.url}" <#if lognav.current>aria-current="page"</#if>>${lognav.navName}</a>
                    </li>
                </#list>
            </ul>
        </nav>
        <details class="theme-menu">
            <summary class="theme-menu-trigger" aria-label="${_res.theme}" data-label="${_res.theme}">
                <span class="theme-mode-icon theme-mode-auto" aria-hidden="true">
                    <svg viewBox="0 0 24 24"><rect x="3.5" y="4.5" width="17" height="12" rx="2"/><path d="M8 20h8M12 16.5V20"/></svg>
                </span>
                <span class="theme-mode-icon theme-mode-light" aria-hidden="true">
                    <svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="4"/><path d="M12 2v2M12 20v2M4.93 4.93l1.42 1.42M17.65 17.65l1.42 1.42M2 12h2M20 12h2M4.93 19.07l1.42-1.42M17.65 6.35l1.42-1.42"/></svg>
                </span>
                <span class="theme-mode-icon theme-mode-dark" aria-hidden="true">
                    <svg viewBox="0 0 24 24"><path d="M20.2 15.3A8.5 8.5 0 0 1 8.7 3.8 8.5 8.5 0 1 0 20.2 15.3Z"/></svg>
                </span>
            </summary>
            <div class="theme-menu-panel" role="group" aria-label="${_res.theme}">
                <button type="button" data-theme-value="light">
                    <span class="theme-option-icon" aria-hidden="true"><svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="4"/><path d="M12 2v2M12 20v2M4.93 4.93l1.42 1.42M17.65 17.65l1.42 1.42M2 12h2M20 12h2M4.93 19.07l1.42-1.42M17.65 6.35l1.42-1.42"/></svg></span>
                    <span class="theme-option-label">${_res.themeLight}</span><span class="theme-option-check" aria-hidden="true">✓</span>
                </button>
                <button type="button" data-theme-value="auto">
                    <span class="theme-option-icon" aria-hidden="true"><svg viewBox="0 0 24 24"><rect x="3.5" y="4.5" width="17" height="12" rx="2"/><path d="M8 20h8M12 16.5V20"/></svg></span>
                    <span class="theme-option-label">${_res.themeAuto}</span><span class="theme-option-check" aria-hidden="true">✓</span>
                </button>
                <button type="button" data-theme-value="dark">
                    <span class="theme-option-icon" aria-hidden="true"><svg viewBox="0 0 24 24"><path d="M20.2 15.3A8.5 8.5 0 0 1 8.7 3.8 8.5 8.5 0 1 0 20.2 15.3Z"/></svg></span>
                    <span class="theme-option-label">${_res.themeDark}</span><span class="theme-option-check" aria-hidden="true">✓</span>
                </button>
            </div>
        </details>
    </div>
</header>
<main class="site-layout" id="main-content">
