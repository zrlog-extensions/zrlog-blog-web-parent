<article class="article-detail">
    <header class="article-header">
        <div class="article-meta">
            <a class="category-link" href="${log.typeUrl}" rel="tag">${log.typeName}</a>
            <span aria-hidden="true">·</span>
            <time datetime="${log.releaseTime}">${log.releaseTime?split("T")[0]}</time>
        </div>
        <h1>${log.title}</h1>
    </header>

    <#if log.tocHtml?has_content>
        <details class="article-toc">
            <summary>${_res.tableOfContents}</summary>
            ${log.tocHtml}
        </details>
    </#if>

    <div class="article-content markdown-body">${log.content!''}</div>

    <#if log.tags?has_content>
        <nav class="article-tags" aria-label="${_res.tag}">
            <#list log.tags as tag>
                <a href="${tag.url}">#${tag.name}</a>
            </#list>
        </nav>
    </#if>

    <aside class="reprint-note">
        <p>${_res.reprint!''}</p>
        <a title="${log.title}" href="${log.noSchemeUrl}">${log.noSchemeUrl}</a>
    </aside>

    <#assign hasLastLog = log.lastLog?? && log.lastLog.title?has_content && log.lastLog.url != log.url>
    <#assign hasNextLog = log.nextLog?? && log.nextLog.title?has_content && log.nextLog.url != log.url>
    <#if hasLastLog || hasNextLog>
    <nav class="article-neighbors" aria-label="${_res.articleNavigation}">
        <#if hasLastLog>
            <a href="${log.lastLog.url}">
                <span>${_res.lastArticle}</span>
                <strong>${log.lastLog.title}</strong>
            </a>
        </#if>
        <#if hasNextLog>
            <a href="${log.nextLog.url}">
                <span>${_res.nextArticle}</span>
                <strong>${log.nextLog.title}</strong>
            </a>
        </#if>
    </nav>
    </#if>

    <#if _res.detailAd?has_content><div class="detail-ad">${_res.detailAd}</div></#if>
</article>
