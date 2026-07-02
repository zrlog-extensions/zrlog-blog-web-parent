<aside class="lg:w-1/4 w-full space-y-6">
    <#-- 广告位 -->
    <#if _res.widgetAd?has_content>
        <div class="bg-white dark:bg-black border border-gray-200 dark:border-gray-800 rounded-lg p-5">
            ${_res.widgetAd}
        </div>
    </#if>

    <#-- 搜索框 -->
    <form action="${searchUrl}" method="post" class="bg-white dark:bg-black border border-gray-200 dark:border-gray-800 rounded-lg p-5 space-y-4">
        <h3 class="text-lg font-bold text-gray-950 dark:text-white">${_res.search}</h3>
        <div class="flex items-center space-x-2">
            <input
                    type="text"
                    name="key"
                    value="${key!""}"
                    placeholder="${_res.searchTip}"
                    class="flex-1 border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-950 rounded-lg px-4 py-2.5 text-sm focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 focus:outline-none w-full transition-all dark:text-white"
            />
            <button
                    type="submit"
                    class="bg-blue-600 text-white text-sm font-medium px-5 py-2.5 rounded-lg hover:bg-blue-700 transition-colors cursor-pointer"
            ><i class="ri-search-line text-lg"></i> ${_res.search}</button>
        </div>
    </form>

    <#-- 插件内容 -->
    <#if init.plugins?has_content>
        <#list init.plugins as plugin>
            <#if plugin.isSystem == false>
            <#-- 跳过非系统插件 -->
            <#else>
                <#switch plugin.pluginName>

                    <#case "types">
                        <div class="bg-white dark:bg-black border border-gray-200 dark:border-gray-800 rounded-lg p-5">
                            <h3 class="text-lg font-bold text-gray-950 dark:text-white mb-4 pb-2 border-b border-gray-100 dark:border-gray-800">${_res.category}</h3>
                            <ul class="space-y-2 text-sm text-gray-600 dark:text-gray-400">
                                <#list init.types as type>
                                    <li>
                                        <a class="flex items-center justify-between group hover:text-blue-600 transition-colors p-2 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-950" href="${type.url}">
                                            <span class="flex items-center gap-3"><i class="ri-folder-line text-lg text-gray-400 group-hover:text-blue-500"></i> ${type.typeName}</span>
                                            <span class="bg-gray-100 dark:bg-gray-900 text-gray-400 text-xs px-2 py-0.5 rounded group-hover:bg-blue-50 group-hover:text-blue-600 transition-colors">${type.typeamount}</span>
                                        </a>
                                    </li>
                                </#list>
                            </ul>
                        </div>
                        <#break>

                    <#case "links">
                        <div class="bg-white dark:bg-black border border-gray-200 dark:border-gray-800 rounded-lg p-5">
                            <h3 class="text-lg font-bold text-gray-950 dark:text-white mb-4 pb-2 border-b border-gray-100 dark:border-gray-800">${_res.link}</h3>
                            <ul class="space-y-1 text-sm text-gray-600 dark:text-gray-400">
                                <#list init.links as link>
                                    <li>
                                        <a class="flex items-center gap-3 group hover:text-blue-600 transition-colors p-2 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-950" href="${link.url}" title="${link.alt}" target="_blank">
                                            <i class="ri-link text-lg text-gray-400 group-hover:text-blue-500"></i> ${link.linkName}
                                        </a>
                                    </li>
                                </#list>
                            </ul>
                        </div>
                        <#break>

                    <#case "archives">
                        <div class="bg-white dark:bg-black border border-gray-200 dark:border-gray-800 rounded-lg p-5">
                            <h3 class="text-lg font-bold text-gray-950 dark:text-white mb-4 pb-2 border-b border-gray-100 dark:border-gray-800">${_res.archive}</h3>
                            <ul id="archive-list" class="space-y-1 text-sm text-gray-600 dark:text-gray-400">
                                <#list init.archiveList as archive>
                                    <li class="archive-item">
                                        <a class="flex items-center justify-between group hover:text-blue-600 transition-colors p-2 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-950" href="${archive.url}" rel="nofollow">
                                            <span class="flex items-center gap-3"><i class="ri-archive-line text-lg text-gray-400 group-hover:text-blue-500"></i> ${archive.text}</span>
                                            <span class="bg-gray-100 dark:bg-gray-900 text-gray-400 text-xs px-2 py-0.5 rounded group-hover:bg-blue-50 group-hover:text-blue-600 transition-colors">${archive.count}</span>
                                        </a>
                                    </li>
                                </#list>
                            </ul>
                            <div id="archive-more-btn-container" class="hidden mt-2">
                                <button id="archive-more-btn" class="w-full text-xs text-center text-gray-400 hover:text-blue-600 transition-colors py-2 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-950 cursor-pointer">
                                    ${_res['more']!'更多...'}
                                </button>
                            </div>
                            <script>
                                (function() {
                                    const items = document.querySelectorAll('#archive-list .archive-item');
                                    const limit = 10;
                                    
                                    if (items.length > limit) {
                                        // Hide items beyond the limit
                                        for (let i = limit; i < items.length; i++) {
                                            items[i].classList.add('hidden');
                                        }
                                        
                                        // Show the button
                                        const btnContainer = document.getElementById('archive-more-btn-container');
                                        const btn = document.getElementById('archive-more-btn');
                                        btnContainer.classList.remove('hidden');
                                        
                                        btn.addEventListener('click', function() {
                                            const isExpanded = btn.getAttribute('data-expanded') === 'true';
                                            
                                            if (isExpanded) {
                                                // Collapse
                                                for (let i = limit; i < items.length; i++) {
                                                    items[i].classList.add('hidden');
                                                }
                                                btn.innerText = '${_res['more']!'更多...'}';
                                                btn.setAttribute('data-expanded', 'false');
                                            } else {
                                                // Expand
                                                for (let i = limit; i < items.length; i++) {
                                                    items[i].classList.remove('hidden');
                                                }
                                                btn.innerText = '${_res['packUp']!'收起'}';
                                                btn.setAttribute('data-expanded', 'true');
                                            }
                                        });
                                    }
                                })();
                            </script>
                        </div>
                        <#break>

                    <#case "tags">
                        <div class="bg-white dark:bg-black border border-gray-200 dark:border-gray-800 rounded-lg p-5">
                            <h3 class="text-lg font-bold text-gray-950 dark:text-white mb-4 pb-2 border-b border-gray-100 dark:border-gray-800">${_res.tag}</h3>
                            <div class="flex flex-wrap gap-2">
                                <#list init.tags as tag>
                                    <a class="px-2 py-1 bg-gray-100 dark:bg-gray-900 text-gray-600 dark:text-gray-300 text-xs font-medium rounded hover:bg-blue-50 hover:text-blue-600 transition-colors" href="${tag.url}">
                                         ${tag.text}
                                    </a>
                                </#list>
                            </div>
                        </div>
                        <#break>

                </#switch>
            </#if>
        </#list>
    </#if>
</aside>
