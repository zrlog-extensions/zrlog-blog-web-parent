<#include "header.ftl">
<section class="content-column">
    <header class="page-heading">
        <span class="page-heading-icon"><@themeIcon name="links"/></span>
        <p class="eyebrow">${_res.link}</p>
        <h1>${_res.linksTitle}</h1>
        <p>${_res.linksDescription}</p>
    </header>
    <#if init.links?has_content>
        <div class="link-grid">
            <#list init.links as link>
                <a class="link-card" href="${link.url}" target="_blank" rel="noopener noreferrer">
                    <span class="link-card-icon" aria-hidden="true">
                        <#if link.icon?has_content>
                            <img src="${link.icon}" alt="" loading="lazy" decoding="async"/>
                        <#else>
                            <@themeIcon name="external" className="external-link-icon"/>
                        </#if>
                    </span>
                    <span class="link-card-copy">
                        <strong>${link.linkName}</strong>
                        <#if link.alt?has_content><span>${link.alt}</span></#if>
                    </span>
                </a>
            </#list>
        </div>
    <#else>
        <div class="empty-state compact-empty">
            <p class="empty-state-mark" aria-hidden="true">↗</p>
            <h2>${_res.noLinksTitle}</h2>
            <p>${_res.noLinksDescription}</p>
        </div>
    </#if>
</section>
<#include "plugin.ftl">
<#include "footer.ftl">
