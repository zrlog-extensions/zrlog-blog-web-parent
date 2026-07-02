<#include "header.ftl">
<section class="py-12 md:py-16 bg-gray-50 dark:bg-gray-950 min-h-screen">
    <div class="container mx-auto px-4 md:px-6">
        <div class="flex flex-col lg:flex-row gap-8">
            <main class="lg:w-3/4 w-full space-y-6">

                <#if data?has_content>
                    <#if tipsType?has_content>
                        <div class="bg-white dark:bg-black border border-gray-200 dark:border-gray-800 rounded-lg p-5">
                            <h3 class="text-xl sm:text-2xl dark:text-gray-200 font-semibold text-gray-800 mb-2">${tipsType}目录：${tipsName}</h3>
                            <p class="text-gray-600 dark:text-gray-200 text-base">以下是与 ${tipsType} “${tipsName}” 相关联的文章</p>
                        </div>
                    </#if>

                    <#if data?has_content && data.rows?has_content>
                        <#list data.rows as log>
                            <article class="group bg-white dark:bg-black rounded-lg p-5 border border-gray-200 dark:border-gray-800 transition-colors flex flex-col md:flex-row gap-6 hover:border-blue-400 dark:hover:border-blue-700">
                                <#if log.thumbnail?has_content>
                                    <div class="md:w-1/3 w-full shrink-0 overflow-hidden rounded-lg relative aspect-[4/3] md:aspect-[4/3] bg-gray-100 dark:bg-gray-900">
                                        <img
                                            class="w-full h-full object-cover transform group-hover:scale-110 transition-transform duration-500"
                                            onerror="this.style.display='none'"
                                            alt='${log.title}'
                                            src="${log.thumbnail}"
                                        />
                                        <div class="absolute inset-0 bg-black/5 dark:bg-black/20 group-hover:bg-transparent transition-colors duration-300"></div>
                                    </div>
                                </#if>

                                <div class="flex flex-col justify-between flex-grow space-y-4">
                                    <div class="space-y-3">
                                        <div class="flex items-center gap-3 text-xs font-medium text-gray-500 dark:text-gray-400">
                                            <span class="flex items-center gap-1.5 px-2.5 py-1 rounded bg-blue-50 dark:bg-blue-950 text-blue-600 dark:text-blue-300">
                                                <i class="ri-folder-line"></i>
                                                <a href="${log.typeUrl}" class="hover:underline">${log.typeName}</a>
                                            </span>
                                            <span class="text-gray-300 dark:text-gray-700">|</span>
                                            <span class="flex items-center gap-1">
                                                <i class="ri-time-line"></i> ${log.releaseTime?split("T")[0]}
                                            </span>
                                        </div>

                                        <h2 class="text-2xl font-bold leading-tight">
                                            <a rel="bookmark" href="${log.url}" class="text-gray-900 dark:text-white hover:text-blue-600 dark:hover:text-blue-400 transition-colors line-clamp-2">
                                                ${log.title}
                                            </a>
                                        </h2>

                                        <div class="text-gray-600 dark:text-gray-400 text-sm leading-relaxed line-clamp-3">
                                            ${log.digest!''}
                                        </div>
                                    </div>

                                    <div class="flex items-center justify-between pt-4 border-t border-gray-100 dark:border-gray-800 mt-auto">
                                        <div class="flex items-center gap-4 text-sm text-gray-500 dark:text-gray-400">
                                            <span class="flex items-center gap-1.5">
                                                <i class="ri-eye-line"></i> ${log.click} 阅读
                                            </span>
                                            <#if log.canComment>
                                                <a href="${log.url}#comment" class="flex items-center gap-1.5 hover:text-blue-600 transition-colors group/comment">
                                                    <i class="ri-chat-1-line group-hover/comment:text-blue-600 transition-colors"></i> ${log.commentSize} 评论
                                                </a>
                                            </#if>
                                        </div>
                                        <a href="${log.url}" class="text-blue-600 dark:text-blue-400 text-sm font-medium hover:underline flex items-center gap-1 group/link">
                                            阅读全文 <i class="ri-arrow-right-line transition-transform group-hover/link:translate-x-1"></i>
                                        </a>
                                    </div>
                                </div>
                            </article>
                        </#list>
                    </#if>
                <#else>
                    <#assign pageLevel = 1>
                    <#include "404.ftl">
                </#if>
            </main>
            <#include "plugin.ftl">
        </div>
        <#include "pager.ftl">
    </div>
</section>
<#include "footer.ftl">
