<#list init.logNavs as lognav>
    <li>
        <a href="${lognav.url}" class="flex items-center gap-2 px-3 py-2 rounded-lg transition-all duration-300 hover:bg-black/5 dark:hover:bg-white/10 <#if lognav.current>text-primary bg-black/5 dark:bg-white/5 font-medium<#else>text-gray-700 dark:text-white</#if>">
            <i class="${lognav.icon!''}"></i>
            <span>${lognav.navName}</span>
        </a>
    </li>
</#list>
