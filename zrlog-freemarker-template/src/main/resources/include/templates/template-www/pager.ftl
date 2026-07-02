<#if pager??>
    <nav aria-label="Pagination" class="my-16 flex justify-center">
        <ul class="flex items-center gap-3 text-sm font-medium select-none">

            <#-- 首页按钮 -->
            <#if !pager.startPage>
                <li>
                    <a href="${pager.pageStartUrl}" title="${_res.pageStart}"
                       class="h-10 px-5 flex items-center justify-center bg-white dark:bg-gray-900 border border-gray-200 dark:border-gray-800 rounded-lg text-gray-500 hover:text-blue-600 hover:border-blue-400 dark:hover:border-blue-600 transition-colors">
                        ${_res.pageStart}
                    </a>
                </li>
            </#if>

            <#-- 页码列表 -->
            <#list pager.pageList as page>
                <li>
                    <a href="${page.url}"
                       class="h-10 min-w-[2.5rem] px-3 flex items-center justify-center rounded-lg border transition-colors
                       <#if page.current>
                           bg-blue-600 text-white border-blue-600 cursor-default
                       <#else>
                           bg-white dark:bg-gray-900 text-gray-600 dark:text-gray-400 border-gray-200 dark:border-gray-800 hover:text-blue-600 hover:border-blue-400 dark:hover:border-blue-600
                       </#if>">
                        ${page.desc}
                    </a>
                </li>
            </#list>

            <#-- 末页按钮 -->
            <#if !pager.endPage>
                <li>
                    <a href="${pager.pageEndUrl}" title="${_res.pageEnd}"
                       class="h-10 px-5 flex items-center justify-center bg-white dark:bg-gray-900 border border-gray-200 dark:border-gray-800 rounded-lg text-gray-500 hover:text-blue-600 hover:border-blue-400 dark:hover:border-blue-600 transition-colors">
                        ${_res.pageEnd}
                    </a>
                </li>
            </#if>

        </ul>
    </nav>
</#if>
