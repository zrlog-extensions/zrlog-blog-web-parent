<#if pager??>
    <nav class="pager" aria-label="${_res.pagination}">
        <ul>
            <#if !pager.startPage>
                <li><a href="${pager.pageStartUrl}">${_res.pageStart}</a></li>
            </#if>
            <#list pager.pageList as page>
                <li>
                    <a href="${page.url}" <#if page.current>aria-current="page" class="pager-current"</#if>>${page.desc}</a>
                </li>
            </#list>
            <#if !pager.endPage>
                <li><a href="${pager.pageEndUrl}">${_res.pageEnd}</a></li>
            </#if>
        </ul>
    </nav>
</#if>
