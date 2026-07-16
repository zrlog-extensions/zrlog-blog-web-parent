<#include "header.ftl">
<section class="content-column">
    <header class="page-heading">
        <span class="page-heading-icon"><@themeIcon name="archives"/></span>
        <p class="eyebrow">${_res.archive}</p>
        <h1>${_res.archivesTitle}</h1>
        <p>${_res.archivesDescription}</p>
    </header>
    <#if init.archiveList?has_content>
        <div class="archive-groups">
            <#assign currentYear = "">
            <#list init.archiveList as archive>
                <#assign archiveYear = archive.text?substring(0, 4)>
                <#if archiveYear != currentYear>
                    <#if currentYear?has_content></div></section></#if>
                    <section class="archive-year">
                        <h2>${archiveYear}</h2>
                        <div class="archive-months">
                    <#assign currentYear = archiveYear>
                </#if>
                <a href="${archive.url}">
                    <span>${archive.text?replace("_", "-")}</span>
                    <span>${archive.count} ${_res.articles}</span>
                </a>
            </#list>
            <#if currentYear?has_content></div></section></#if>
        </div>
    <#else>
        <div class="empty-state compact-empty">
            <p class="empty-state-mark" aria-hidden="true">◇</p>
            <h2>${_res.noArchivesTitle}</h2>
            <p>${_res.noArchivesDescription}</p>
        </div>
    </#if>
</section>
<#include "plugin.ftl">
<#include "footer.ftl">
