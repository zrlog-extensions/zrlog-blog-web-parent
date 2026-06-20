<!DOCTYPE html>
<html lang="${lang!''}">
<head>
    <#include "_common/meta.ftl"/>
    <link href="${url}/fonts/remixicon.css" rel="stylesheet"/>
    <link href="${url}/css/editormd.css" rel="stylesheet"/>
    <link href="${url}/css/style.css" rel="stylesheet"/>
    <#include "_common/auto-hljs.ftl"/>
    <style>
        :root {
            --color-primary: ${_res['colorPrimary']!'#1677ff'};
        }
    </style>
    ${globalStyle!''}
    <script src="${url}/js/tailwindcss-3.4.6.js"></script>
    <script>tailwind.config = {
            darkMode: 'class', // ✅ 启用暗黑模式（class 模式）
            theme: {
                extend: {
                    colors: {primary: '${_res['colorPrimary']!'#1677ff'}', secondary: '#f97316'},
                    borderRadius: {
                        'none': '0px',
                        'sm': '4px',
                        DEFAULT: '8px',
                        'md': '12px',
                        'lg': '16px',
                        'xl': '20px',
                        '2xl': '24px',
                        '3xl': '32px',
                        'full': '9999px',
                        'button': '8px'
                    }
                }
            }
        }
    </script>
    <script src="${url}/js/helpers.js"></script>
</head>
<body class="dark:bg-black dark:text-gray-200">
<!-- 导航栏 -->
<nav class="sticky top-0 z-50 bg-white dark:bg-black border-b border-gray-200 dark:border-gray-800 py-4 transition-all duration-300 shadow-sm"
     id="header">
    <div class="container mx-auto px-4 md:px-6 flex items-center justify-between">
        <div class="flex items-center">
            <a href="${baseUrl}"
               class="flex items-center gap-2 text-2xl font-black tracking-tighter text-gray-900 dark:text-white mr-12 group">
                <div class="w-10 h-10 flex items-center justify-center transition-transform group-hover:scale-110">
                    <img src="${baseUrl}favicon.ico" alt="" class="w-full h-full">
                </div>
                <span>${_res['navBarBrand']!''}</span>
            </a>
            <ul class="hidden lg:flex items-center space-x-1">
                <#include "header-nav.ftl"/>
            </ul>
        </div>
        <div class="flex items-center gap-6">
            <div class="hidden md:flex items-center space-x-4">
                <#if _res['githubLink']?has_content>
                    ${_res['githubLink']}
                    <span class="w-px h-4 bg-gray-200 dark:bg-white/10"></span>
                </#if>
                <div class="flex items-center gap-2 bg-gray-50 dark:bg-gray-950 px-2 py-1 rounded-lg border border-gray-200 dark:border-gray-800">
                    <i class="ri-sun-line text-xs text-gray-500 dark:text-gray-400"></i>
                    <label class="theme-switch relative inline-block w-8 h-4 cursor-pointer">
                        <input type="checkbox" class="sr-only peer">
                        <div class="absolute inset-0 bg-gray-300 dark:bg-gray-700 rounded-full transition-colors peer-checked:bg-blue-600"></div>
                        <div class="absolute left-0.5 top-0.5 bg-white w-3 h-3 rounded-full transition-transform peer-checked:translate-x-4"></div>
                    </label>
                    <i class="ri-moon-line text-xs text-gray-500 dark:text-gray-400"></i>
                </div>
            </div>
            <button class="md:hidden flex items-center justify-center w-10 h-10 text-gray-900 dark:text-white hover:bg-black/5 dark:hover:bg-white/5 rounded-xl border border-gray-200 dark:border-white/10"
                    id="toggleSidebar">
                <i class="ri-menu-4-line ri-lg"></i>
            </button>
        </div>
    </div>
</nav>

<div id="overlay" class="fixed inset-0 bg-black/60 backdrop-blur-sm z-[60] hidden"></div>
<aside id="sidebar"
        class="fixed top-0 left-0 w-72 h-full bg-white dark:bg-black border-r border-gray-200 dark:border-gray-800 p-8 shadow-2xl -translate-x-full transition-transform duration-500 ease-out z-[70] hidden">
    <div class="flex flex-col h-full">
        <div class="mb-12 flex items-center justify-between">
            <a href="${baseUrl}"
               class="flex items-center gap-2 text-2xl font-black tracking-tighter text-gray-900 dark:text-white mr-12 group">
                <div class="w-10 h-10 flex items-center justify-center transition-transform group-hover:scale-110">
                    <img src="${baseUrl}favicon.ico" alt="" class="w-full h-full">
                </div>
                <span>${_res['navBarBrand']!''}</span>
            </a>
            <button id="closeSidebar"
                    class="text-gray-500 hover:text-gray-900 dark:text-gray-400 dark:hover:text-white">
                <i class="ri-close-line ri-xl"></i>
            </button>
        </div>
        <ul class="flex flex-col gap-4">
            <#include "header-nav.ftl"/>
        </ul>
        <div class="mt-auto pt-8 border-t border-gray-200 dark:border-gray-800">
            <#if _res['githubLink']?has_content>
                <div class="flex items-center gap-3 text-gray-600 dark:text-gray-400">
                    ${_res['githubLink']}
                </div>
            </#if>
        </div>
    </div>
</aside>
