<#include "header.ftl">
<#assign siteTitle = webSite.title!'ZrLog'>
<#if !siteTitle?has_content>
    <#assign siteTitle = _res['navBarBrand']!'ZrLog'>
</#if>
<#assign siteSubtitle = webSite.second_title!''>
<#if !siteSubtitle?has_content>
    <#assign siteSubtitle = webSite.description!''>
</#if>
<#if !siteSubtitle?has_content>
    <#assign siteSubtitle = '记录 ZrLog 的产品更新、使用文档和长期维护经验。'>
</#if>
<#assign hasPosts = data?? && data.rows?? && data.rows?has_content>

<section id="hero" class="bg-white dark:bg-black border-b border-gray-200 dark:border-gray-800">
    <div class="container mx-auto px-4 md:px-6 py-12 md:py-16">
        <div class="grid grid-cols-1 lg:grid-cols-[0.82fr_1.18fr] gap-8 lg:gap-12 items-start">
            <div>
                <div class="inline-flex items-center gap-2 px-3 py-1.5 border border-gray-200 dark:border-gray-800 bg-gray-50 dark:bg-gray-950 text-gray-700 dark:text-gray-300 rounded-lg text-sm font-semibold mb-5">
                    <i class="ri-newspaper-line"></i>
                    产品更新 · 使用文档 · 运维实践
                </div>
                <h1 class="text-3xl md:text-4xl font-bold text-gray-950 dark:text-white leading-tight tracking-tight mb-5">
                    ${siteTitle}
                </h1>
                <p class="text-base text-gray-600 dark:text-gray-400 leading-relaxed mb-7 max-w-2xl">
                    ${siteSubtitle}
                </p>
                <div class="flex flex-col sm:flex-row gap-3 mb-8">
                    <a href="#latest"
                       class="inline-flex items-center justify-center px-6 py-3 bg-blue-600 hover:bg-blue-700 text-white font-bold rounded-lg transition-colors active:scale-[0.98]">
                        <i class="ri-article-line mr-2 text-xl"></i>
                        阅读最新文章
                    </a>
                    <a href="https://www.zrlog.com" target="_blank" rel="noopener"
                       class="inline-flex items-center justify-center px-6 py-3 bg-white dark:bg-black border border-gray-300 dark:border-gray-700 hover:border-blue-500 dark:hover:border-blue-500 text-gray-900 dark:text-white font-bold rounded-lg transition-colors active:scale-[0.98]">
                        <i class="ri-home-5-line mr-2 text-xl"></i>
                        访问官网
                    </a>
                </div>
                <div class="flex flex-wrap gap-x-5 gap-y-2 text-sm text-gray-600 dark:text-gray-400">
                    <span class="inline-flex items-center gap-1.5">
                        <i class="ri-file-list-3-line text-blue-600"></i>
                        ${(init.statistics.totalArticleSize)!0} 篇文章
                    </span>
                    <span class="inline-flex items-center gap-1.5">
                        <i class="ri-folder-3-line text-blue-600"></i>
                        ${(init.statistics.totalTypeSize)!0} 个分类
                    </span>
                    <span class="inline-flex items-center gap-1.5">
                        <i class="ri-price-tag-3-line text-blue-600"></i>
                        ${(init.statistics.totalTagSize)!0} 个标签
                    </span>
                </div>
            </div>
            <div class="border border-gray-200 dark:border-gray-800 rounded-lg bg-gray-50 dark:bg-gray-950 p-5 md:p-6">
                <div class="flex items-start justify-between gap-4 pb-5 border-b border-gray-200 dark:border-gray-800">
                    <div>
                        <div class="text-sm text-gray-500 dark:text-gray-400 mb-1">最新发布</div>
                        <div class="text-2xl font-bold text-gray-950 dark:text-white">
                            <#if hasPosts>${data.rows[0].releaseTime?split("T")[0]}<#else>待发布</#if>
                        </div>
                    </div>
                    <div class="text-right text-sm text-gray-500 dark:text-gray-400">
                        <div class="flex items-center justify-end gap-1.5">
                            <span>博客</span>
                            <span>/</span>
                            <span>文档</span>
                            <span>/</span>
                            <span>版本</span>
                        </div>
                        <div class="mt-1">持续更新</div>
                    </div>
                </div>

                <#if hasPosts>
                    <#assign latestLog = data.rows[0]>
                    <a href="${latestLog.url}" class="block py-5 group">
                        <#if latestLog.thumbnail?has_content>
                            <div class="mb-5 overflow-hidden rounded-lg border border-gray-200 dark:border-gray-800 bg-white dark:bg-black aspect-[16/9]">
                                <img src="${latestLog.thumbnail}" alt="${latestLog.title}" class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105" onerror="this.style.display='none'">
                            </div>
                        </#if>
                        <div class="flex flex-wrap items-center gap-3 text-xs font-medium text-gray-500 dark:text-gray-400 mb-3">
                            <span class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-lg bg-blue-50 dark:bg-blue-950 text-blue-600 dark:text-blue-300">
                                <i class="ri-folder-line"></i>
                                ${latestLog.typeName}
                            </span>
                            <span class="inline-flex items-center gap-1.5">
                                <i class="ri-time-line"></i>
                                ${latestLog.releaseTime?split("T")[0]}
                            </span>
                        </div>
                        <h2 class="text-2xl font-bold text-gray-950 dark:text-white leading-tight mb-3 group-hover:text-blue-600 dark:group-hover:text-blue-400 transition-colors">
                            ${latestLog.title}
                        </h2>
                        <div class="text-gray-600 dark:text-gray-400 leading-relaxed line-clamp-3">
                            ${latestLog.digest!''}
                        </div>
                    </a>
                    <div class="rounded-lg border border-gray-200 dark:border-gray-800 bg-white dark:bg-black p-4 flex items-center justify-between gap-4">
                        <div class="flex items-center gap-2 text-sm font-bold text-gray-950 dark:text-white">
                            <i class="ri-eye-line text-blue-600"></i>
                            ${latestLog.click} 阅读
                        </div>
                        <a href="${latestLog.url}" class="text-sm font-semibold text-blue-600 dark:text-blue-400 inline-flex items-center">
                            阅读全文 <i class="ri-arrow-right-line ml-1"></i>
                        </a>
                    </div>
                <#else>
                    <div class="py-10 text-center">
                        <div class="w-12 h-12 rounded-lg bg-blue-50 dark:bg-blue-950 text-blue-600 dark:text-blue-300 flex items-center justify-center mx-auto mb-4">
                            <i class="ri-article-line text-2xl"></i>
                        </div>
                        <h2 class="text-xl font-bold text-gray-950 dark:text-white mb-2">还没有发布文章</h2>
                        <p class="text-gray-600 dark:text-gray-400">发布第一篇文章后，首页会自动展示最新内容。</p>
                    </div>
                </#if>
            </div>
        </div>
    </div>
</section>

<section class="bg-gray-50 dark:bg-gray-950 border-b border-gray-200 dark:border-gray-800">
    <div class="container mx-auto px-4 md:px-6 py-8">
        <div class="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-6">
            <div>
                <p class="text-sm font-semibold text-gray-500 dark:text-gray-400 mb-2">围绕内容生产、部署维护和插件扩展沉淀资料</p>
                <div class="text-xl font-bold text-gray-950 dark:text-white">从最近的问题和版本变化开始浏览</div>
            </div>
            <div class="flex flex-wrap gap-x-5 gap-y-2 text-sm text-gray-600 dark:text-gray-400">
                <span>产品更新</span>
                <span>使用文档</span>
                <span>主题插件</span>
                <span>部署实践</span>
                <span>问题记录</span>
            </div>
        </div>
    </div>
</section>

<section id="topics" class="py-16 md:py-20 bg-white dark:bg-black">
    <div class="container mx-auto px-4 md:px-6">
        <div class="max-w-3xl mb-10">
            <h2 class="text-3xl md:text-4xl font-extrabold text-gray-950 dark:text-white mb-4">按主题进入</h2>
            <p class="text-base text-gray-600 dark:text-gray-400 leading-relaxed">
                分类会跟随站点内容自动更新，适合从文档、版本记录或具体问题切入。
            </p>
        </div>
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
            <#if init.types?? && init.types?has_content>
                <#list init.types as type>
                    <#if type?index < 4>
                        <a href="${type.url}" class="group border border-gray-200 dark:border-gray-800 rounded-lg p-5 bg-white dark:bg-gray-950 hover:border-blue-500 transition-colors">
                            <div class="w-10 h-10 rounded-lg bg-blue-50 dark:bg-blue-950 text-blue-600 dark:text-blue-300 flex items-center justify-center mb-4">
                                <i class="ri-folder-3-line text-xl"></i>
                            </div>
                            <h3 class="font-bold text-gray-950 dark:text-white mb-2">${type.typeName}</h3>
                            <p class="text-sm text-gray-600 dark:text-gray-400 leading-relaxed line-clamp-2">${type.remark!'查看该分类下的文章。'}</p>
                            <div class="mt-4 text-sm font-semibold text-blue-600 dark:text-blue-400 inline-flex items-center">
                                ${type.amount!type.typeamount!0} 篇 <i class="ri-arrow-right-line ml-1 transition-transform group-hover:translate-x-1"></i>
                            </div>
                        </a>
                    </#if>
                </#list>
            <#else>
                <div class="border border-gray-200 dark:border-gray-800 rounded-lg p-5 bg-white dark:bg-gray-950">
                    <div class="w-10 h-10 rounded-lg bg-blue-50 dark:bg-blue-950 text-blue-600 dark:text-blue-300 flex items-center justify-center mb-4">
                        <i class="ri-folder-3-line text-xl"></i>
                    </div>
                    <h3 class="font-bold text-gray-950 dark:text-white mb-2">内容分类</h3>
                    <p class="text-sm text-gray-600 dark:text-gray-400 leading-relaxed">创建分类后，这里会展示主要内容入口。</p>
                </div>
            </#if>
        </div>
    </div>
</section>

<section id="latest" class="py-16 md:py-20 bg-gray-50 dark:bg-gray-950 border-y border-gray-200 dark:border-gray-800">
    <div class="container mx-auto px-4 md:px-6">
        <div class="flex flex-col md:flex-row md:items-end md:justify-between gap-6 mb-10">
            <div class="max-w-3xl">
                <h2 class="text-3xl md:text-4xl font-extrabold text-gray-950 dark:text-white mb-4">最近文章</h2>
                <p class="text-gray-600 dark:text-gray-400 leading-relaxed">
                    查看最近发布的更新、文档和维护记录。
                </p>
            </div>
            <a href="#topics" class="inline-flex items-center text-blue-600 dark:text-blue-400 font-semibold">
                浏览分类 <i class="ri-arrow-right-line ml-1"></i>
            </a>
        </div>

        <#if hasPosts>
            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                <#list data.rows as log>
                    <article class="group border border-gray-200 dark:border-gray-800 rounded-lg bg-white dark:bg-black overflow-hidden hover:border-blue-500 transition-colors flex flex-col">
                        <#if log.thumbnail?has_content>
                            <a href="${log.url}" class="block aspect-[16/9] bg-gray-100 dark:bg-gray-900 overflow-hidden">
                                <img src="${log.thumbnail}" alt="${log.title}" class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105" onerror="this.style.display='none'">
                            </a>
                        </#if>
                        <div class="p-5 flex flex-col flex-1">
                            <div class="flex flex-wrap items-center gap-3 text-xs font-medium text-gray-500 dark:text-gray-400 mb-3">
                                <a href="${log.typeUrl}" class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-lg bg-blue-50 dark:bg-blue-950 text-blue-600 dark:text-blue-300 hover:underline">
                                    <i class="ri-folder-line"></i>
                                    ${log.typeName}
                                </a>
                                <span class="inline-flex items-center gap-1">
                                    <i class="ri-time-line"></i>
                                    ${log.releaseTime?split("T")[0]}
                                </span>
                            </div>
                            <h3 class="text-xl font-bold text-gray-950 dark:text-white leading-tight mb-3">
                                <a href="${log.url}" class="hover:text-blue-600 dark:hover:text-blue-400 transition-colors line-clamp-2">${log.title}</a>
                            </h3>
                            <div class="text-sm text-gray-600 dark:text-gray-400 leading-relaxed line-clamp-3 mb-5">
                                ${log.digest!''}
                            </div>
                            <div class="mt-auto pt-4 border-t border-gray-200 dark:border-gray-800 flex items-center justify-between gap-4 text-sm">
                                <div class="flex items-center gap-3 text-gray-500 dark:text-gray-400">
                                    <span class="inline-flex items-center gap-1">
                                        <i class="ri-eye-line"></i>
                                        ${log.click}
                                    </span>
                                    <#if log.canComment>
                                        <a href="${log.url}#comment" class="inline-flex items-center gap-1 hover:text-blue-600 transition-colors">
                                            <i class="ri-chat-1-line"></i>
                                            ${log.commentSize}
                                        </a>
                                    </#if>
                                </div>
                                <a href="${log.url}" class="font-semibold text-blue-600 dark:text-blue-400 inline-flex items-center">
                                    阅读 <i class="ri-arrow-right-line ml-1 transition-transform group-hover:translate-x-1"></i>
                                </a>
                            </div>
                        </div>
                    </article>
                </#list>
            </div>
            <#include "pager.ftl">
        <#else>
            <div class="border border-gray-200 dark:border-gray-800 rounded-lg bg-white dark:bg-black p-10 text-center">
                <div class="w-12 h-12 rounded-lg bg-blue-50 dark:bg-blue-950 text-blue-600 dark:text-blue-300 flex items-center justify-center mx-auto mb-4">
                    <i class="ri-article-line text-2xl"></i>
                </div>
                <h3 class="text-xl font-bold text-gray-950 dark:text-white mb-2">暂无文章</h3>
                <p class="text-gray-600 dark:text-gray-400">发布文章后会自动生成最近文章列表。</p>
            </div>
        </#if>
    </div>
</section>

<section class="py-16 md:py-20 bg-white dark:bg-black">
    <div class="container mx-auto px-4 md:px-6">
        <div class="grid grid-cols-1 lg:grid-cols-[0.82fr_1.18fr] gap-8 items-start">
            <div>
                <h2 class="text-3xl md:text-4xl font-extrabold text-gray-950 dark:text-white mb-4">标签与归档</h2>
                <p class="text-gray-600 dark:text-gray-400 leading-relaxed">
                    高频标签用于快速定位主题，归档适合按时间回看更新记录。
                </p>
            </div>
            <div class="space-y-5">
                <#if init.tags?? && init.tags?has_content>
                    <div class="border border-gray-200 dark:border-gray-800 rounded-lg bg-gray-50 dark:bg-gray-950 p-5">
                        <div class="flex items-center gap-2 text-sm font-bold text-gray-950 dark:text-white mb-4">
                            <i class="ri-price-tag-3-line text-blue-600"></i>
                            常用标签
                        </div>
                        <div class="flex flex-wrap gap-2">
                            <#list init.tags as tag>
                                <#if tag?index < 16>
                                    <a href="${tag.url}" class="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-white dark:bg-black border border-gray-200 dark:border-gray-800 text-sm text-gray-700 dark:text-gray-300 hover:border-blue-500 hover:text-blue-600 dark:hover:text-blue-400 transition-colors">
                                        ${tag.text}
                                        <span class="text-xs text-gray-400">${tag.count}</span>
                                    </a>
                                </#if>
                            </#list>
                        </div>
                    </div>
                </#if>
                <#if init.archiveList?? && init.archiveList?has_content>
                    <div class="border border-gray-200 dark:border-gray-800 rounded-lg bg-gray-50 dark:bg-gray-950 p-5">
                        <div class="flex items-center gap-2 text-sm font-bold text-gray-950 dark:text-white mb-4">
                            <i class="ri-archive-line text-blue-600"></i>
                            最近归档
                        </div>
                        <div class="grid grid-cols-1 sm:grid-cols-2 gap-3">
                            <#list init.archiveList as archive>
                                <#if archive?index < 6>
                                    <a href="${archive.url}" class="flex items-center justify-between gap-3 rounded-lg bg-white dark:bg-black border border-gray-200 dark:border-gray-800 px-4 py-3 hover:border-blue-500 transition-colors">
                                        <span class="font-medium text-gray-800 dark:text-gray-200">${archive.text}</span>
                                        <span class="text-sm text-gray-500 dark:text-gray-400">${archive.count}</span>
                                    </a>
                                </#if>
                            </#list>
                        </div>
                    </div>
                </#if>
            </div>
        </div>
    </div>
</section>

<#include "footer.ftl">
