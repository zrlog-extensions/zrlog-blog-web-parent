<#include "header.ftl">
<section class="content-column">
    <header class="page-heading">
        <span class="page-heading-icon"><@themeIcon name="categories"/></span>
        <p class="eyebrow">${_res.category}</p>
        <h1>${_res.categoriesTitle}</h1>
        <p>${_res.categoriesDescription}</p>
    </header>
    <#if init.types?has_content>
        <div class="category-grid">
            <#list init.types as type>
                <a class="category-card" href="${type.url}">
                    <span class="collection-card-heading">
                        <strong>${type.typeName}</strong>
                        <span>${type.typeamount} ${_res.articles}</span>
                    </span>
                    <#if type.remark?has_content><span class="collection-description">${type.remark}</span></#if>
                </a>
            </#list>
        </div>
    <#else>
        <div class="empty-state compact-empty">
            <p class="empty-state-mark" aria-hidden="true">□</p>
            <h2>${_res.noCategoriesTitle}</h2>
            <p>${_res.noCategoriesDescription}</p>
        </div>
    </#if>
</section>
<#include "plugin.ftl">
<#include "footer.ftl">
