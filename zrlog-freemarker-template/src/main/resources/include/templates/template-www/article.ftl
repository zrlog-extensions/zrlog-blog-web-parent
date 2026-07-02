<article class="bg-white dark:bg-black rounded-lg p-6 md:p-8 border border-gray-200 dark:border-gray-800">
    <!-- 标题 -->
    <h2 class="text-2xl md:text-3xl font-bold text-gray-900 dark:text-white mb-6 leading-tight">${log.title}</h2>

    <!-- 分类与发布时间 -->
    <div class="flex items-center flex-wrap gap-6 text-sm text-gray-500 dark:text-gray-400 mb-8 pb-8 border-b border-gray-100 dark:border-gray-800">
        <span class="flex items-center gap-2">
            <i class="ri-folder-line text-lg text-blue-600"></i>
            <a class="hover:text-blue-600 transition-colors font-medium" href="${log.typeUrl}" rel="tag">${log.typeName}</a>
        </span>
        <span class="text-gray-200 dark:text-gray-700">|</span>
        <span class="flex items-center gap-2">
            <i class="ri-time-line text-lg text-gray-400"></i>
            ${log.releaseTime?split("T")[0]}
        </span>
        <span class="text-gray-200 dark:text-gray-700">|</span>
        <span class="flex items-center gap-2">
            <i class="ri-eye-line text-lg text-gray-400"></i>
            ${log.click} 阅读
        </span>
    </div>

    <!-- 正文内容 -->
    <div class="markdown-body prose dark:prose-invert max-w-none text-gray-600 dark:text-gray-300 leading-top-level-height">
        ${log.content!''}
    </div>

    <hr class="my-10 border-gray-100 dark:border-gray-800"/>

    <!-- 标签 -->
    <#if log.tags?has_content>
        <div class="flex flex-wrap items-center gap-3 mb-8">
            <#list log.tags as tag>
                <a class="px-3 py-1.5 bg-gray-100 dark:bg-gray-900 text-gray-600 dark:text-gray-300 text-sm font-medium rounded hover:bg-blue-50 hover:text-blue-600 dark:hover:bg-blue-950 dark:hover:text-blue-300 transition-all flex items-center gap-1.5" href="${tag.url}">
                    <i class="ri-hashtag text-gray-400"></i> ${tag.name}
                </a>
            </#list>
        </div>
    </#if>

    <!-- 转载说明 -->
    <!-- 转载说明 -->
    <div class="bg-gray-50 dark:bg-gray-950 rounded-lg p-5 mb-8 text-sm text-gray-500 dark:text-gray-400 border border-gray-200 dark:border-gray-800 relative overflow-hidden group">
        <div class="absolute top-0 right-0 p-4 opacity-10 group-hover:opacity-20 transition-opacity">
            <i class="ri-copyright-line text-6xl text-gray-400"></i>
        </div>
        <div class="relative z-10 space-y-2">
            <div class="flex flex-col sm:flex-row sm:items-center gap-2">
                <span class="font-medium text-gray-700 dark:text-gray-300 min-w-[4em]">${_res.author}:</span>
                <span class="text-gray-600 dark:text-gray-400">${website.title}</span>
            </div>
            <div class="flex flex-col sm:flex-row sm:items-center gap-2">
                <span class="font-medium text-gray-700 dark:text-gray-300 min-w-[4em]">${log.title}:</span>
                <a class="text-blue-600 hover:underline break-all" title="${log.title}" href="${log.noSchemeUrl}">
                    ${log.noSchemeUrl}
                </a>
            </div>
        </div>
    </div>

    <!-- 上/下一篇 -->
    <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mb-8">
        <#if log.lastLog??>
            <a href="${log.lastLog.url}" class="group block p-4 rounded-lg border border-gray-200 dark:border-gray-800 hover:border-blue-400 dark:hover:border-blue-600 hover:bg-gray-50 dark:hover:bg-gray-950 transition-all">
                <div class="text-xs text-gray-400 mb-1">
                    <i class="ri-arrow-left-line"></i> ${_res.lastArticle}
                </div>
                <div class="text-sm font-medium text-gray-900 dark:text-white group-hover:text-blue-600 dark:group-hover:text-blue-400 truncate">
                    ${log.lastLog.title}
                </div>
            </a>
        <#else>
             <div class="p-4 rounded-lg border border-gray-200 dark:border-gray-800 bg-gray-50 dark:bg-gray-950 text-gray-400 text-sm">
                 ${_res.lastArticle}：没有了
             </div>
        </#if>
        
        <#if log.nextLog??>
            <a href="${log.nextLog.url}" class="group block p-4 rounded-lg border border-gray-200 dark:border-gray-800 hover:border-blue-400 dark:hover:border-blue-600 hover:bg-gray-50 dark:hover:bg-gray-950 transition-all text-right">
                <div class="text-xs text-gray-400 mb-1">
                    ${_res.nextArticle} <i class="ri-arrow-right-line"></i>
                </div>
                <div class="text-sm font-medium text-gray-900 dark:text-white group-hover:text-blue-600 dark:group-hover:text-blue-400 truncate">
                    ${log.nextLog.title}
                </div>
            </a>
        <#else>
            <div class="p-4 rounded-lg border border-gray-200 dark:border-gray-800 bg-gray-50 dark:bg-gray-950 text-gray-400 text-sm text-right">
                ${_res.nextArticle}：没有了
            </div>
        </#if>
    </div>

    <!-- 广告位 -->
    <div class="mt-6">
        ${_res.detailAd!''}
    </div>
</article>
