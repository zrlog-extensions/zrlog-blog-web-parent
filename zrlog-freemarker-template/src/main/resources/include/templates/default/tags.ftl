<#include "header.ftl">
<section class="content-column">
    <header class="page-heading">
        <span class="page-heading-icon"><@themeIcon name="tags"/></span>
        <p class="eyebrow">${_res.tag}</p>
        <h1>${_res.tagsTitle}</h1>
        <p>${_res.tagsDescription}</p>
    </header>
    <#if init.tags?has_content>
        <div class="tag-cloud-page">
            <#list init.tags as tag>
                <a href="${tag.url}">
                    <span>#${tag.text}</span>
                    <span>${tag.count}</span>
                </a>
            </#list>
        </div>
    <#else>
        <div class="empty-state compact-empty">
            <p class="empty-state-mark" aria-hidden="true">#</p>
            <h2>${_res.noTagsTitle}</h2>
            <p>${_res.noTagsDescription}</p>
        </div>
    </#if>
</section>
<#include "plugin.ftl">
<#include "footer.ftl">
