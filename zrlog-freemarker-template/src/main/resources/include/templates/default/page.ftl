<#include "header.ftl">
<section class="content-column" aria-label="${tipsType!_res.articleList}">
    <#if !tipsType?has_content><h1 class="screen-reader-text">${webs.title}</h1></#if>
    <#if tipsType?has_content>
        <header class="page-heading">
            <p class="eyebrow">${tipsType}</p>
            <h1><#if tipsName?has_content>${tipsName}<#else>${tipsType}</#if></h1>
            <#if tipsName?has_content>
                <p>${_res.relatedArticlesPrefix}${tipsType} “${tipsName}” ${_res.relatedArticlesSuffix}</p>
            </#if>
        </header>
    </#if>

    <#if data?? && data.rows?has_content>
        <div class="post-list">
            <#list data.rows as log>
                <article class="post-card <#if log.thumbnail?has_content>post-card-with-image</#if>">
                    <#if log.thumbnail?has_content>
                        <a class="post-card-media" href="${log.url}" tabindex="-1" aria-hidden="true">
                            <img class="preview-img" alt="" src="${log.thumbnail}" loading="lazy" decoding="async"/>
                        </a>
                    </#if>
                    <div class="post-card-body">
                        <div class="post-card-meta">
                            <#if zrlogHomeIndex?? && zrlogHomeIndex && log.sticky?? && log.sticky gt 0>
                                <span class="post-card-pinned">
                                    <@themeIcon name="pin" className="post-card-pinned-icon"/>
                                    <span>${_res.pinned}</span>
                                </span>
                            </#if>
                            <a class="category-link" href="${log.typeUrl}">${log.typeName}</a>
                            <span aria-hidden="true">·</span>
                            <time datetime="${log.releaseTime}">${log.releaseTime?split("T")[0]}</time>
                        </div>
                        <h2><a rel="bookmark" href="${log.url}">${log.title}</a></h2>
                        <div class="post-digest markdown-body">${log.digest!''}</div>
                        <div class="post-card-footer">
                            <a class="read-more" href="${log.url}">${_res.readMore}<span aria-hidden="true"> →</span></a>
                            <#if log.canComment>
                                <a class="comment-link" href="${log.url}#comment">${_res.commentView} · ${log.commentSize}</a>
                            </#if>
                        </div>
                    </div>
                </article>
            </#list>
        </div>
    <#else>
        <div class="empty-state">
            <p class="empty-state-mark" aria-hidden="true">○</p>
            <h1>${_res.emptyListTitle}</h1>
            <p>${_res.emptyListDescription}</p>
            <a class="button-link" href="${baseUrl}">${_res.backHome}</a>
        </div>
    </#if>

    <#include "pager.ftl">
</section>
<#include "plugin.ftl">
<#include "footer.ftl">
