<#include "header.ftl">
<section class="py-12 md:py-16 bg-gray-50 dark:bg-gray-950 min-h-screen">
    <div class="container mx-auto px-4 md:px-6">
        <div class="flex flex-col lg:flex-row gap-8">
            <main class="lg:w-3/4 w-full">
                <#if log??>
                    <nav class="flex items-center gap-2 text-sm text-gray-500 dark:text-gray-400 mb-6 pl-1">
                        <a href="/" class="hover:text-primary transition-colors flex items-center gap-1">
                            <i class="ri-home-5-line"></i> ${_res.home!'首页'}
                        </a>
                        <i class="ri-arrow-right-s-line text-gray-300"></i>
                        <a href="${log.typeUrl}" class="hover:text-primary transition-colors">${log.typeName}</a>
                        <i class="ri-arrow-right-s-line text-gray-300"></i>
                        <span class="text-gray-900 dark:text-white font-medium truncate max-w-[200px] sm:max-w-md">${log.title}</span>
                    </nav>
                    <#include "article.ftl">
                    <#include "comment.ftl">
                <#else>
                    <#assign pageLevel = 1>
                    <#include "404.ftl">
                </#if>
            </main>
            <#include "plugin.ftl">
        </div>
    </div>
</section>
<#include "footer.ftl">
