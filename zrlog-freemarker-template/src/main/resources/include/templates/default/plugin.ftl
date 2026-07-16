<aside class="sidebar" aria-label="${_res.sidebar}">
    <#if _res.widgetAd?has_content>
        <div class="widget widget-ad">${_res.widgetAd}</div>
    </#if>

    <form class="widget search-form" action="${searchUrl}" method="post" role="search" autocomplete="off">
        <label class="screen-reader-text" for="sidebar-search">${_res.searchLabel}</label>
        <input id="sidebar-search" type="search" name="key" placeholder="${_res.searchTip}" value="${key!''}" autocomplete="off"/>
        <button type="submit">${_res.search}</button>
    </form>

    <#if init.plugins?has_content>
        <#list init.plugins as plugin>
            <#if plugin.isSystem>
                <#switch plugin.pluginName>
                    <#case "types">
                        <section class="widget">
                            <div class="widget-heading">
                                <h2><@themeIcon name="categories" className="widget-title-icon"/>${_res.category}</h2>
                                <a href="${baseUrl}categories${suffix!''}">${_res.viewAll}</a>
                            </div>
                            <ul class="widget-list">
                                <#list init.types as type>
                                    <li><a href="${type.url}"><span>${type.typeName}</span><span>${type.typeamount}</span></a></li>
                                </#list>
                            </ul>
                        </section>
                        <#break>
                    <#case "links">
                        <section class="widget">
                            <div class="widget-heading">
                                <h2><@themeIcon name="links" className="widget-title-icon"/>${_res.link}</h2>
                                <a href="${baseUrl}links${suffix!''}">${_res.viewAll}</a>
                            </div>
                            <ul class="widget-list">
                                <#list init.links as link>
                                    <li><a href="${link.url}" target="_blank" rel="noopener noreferrer"><span>${link.linkName}</span><@themeIcon name="external" className="external-link-icon"/></a></li>
                                </#list>
                            </ul>
                        </section>
                        <#break>
                    <#case "archives">
                        <section class="widget">
                            <div class="widget-heading">
                                <h2><@themeIcon name="archives" className="widget-title-icon"/>${_res.archive}</h2>
                                <a href="${baseUrl}archives${suffix!''}">${_res.viewAll}</a>
                            </div>
                            <ul class="widget-list">
                                <#list init.archiveList as archive>
                                    <li><a href="${archive.url}"><span>${archive.text?replace("_", "-")}</span><span>${archive.count}</span></a></li>
                                </#list>
                            </ul>
                        </section>
                        <#break>
                    <#case "tags">
                        <section class="widget">
                            <div class="widget-heading">
                                <h2><@themeIcon name="tags" className="widget-title-icon"/>${_res.tag}</h2>
                                <a href="${baseUrl}tags${suffix!''}">${_res.viewAll}</a>
                            </div>
                            <div class="tag-list">
                                <#list init.tags as tag>
                                    <a href="${tag.url}"><span>${tag.text}</span><span>${tag.count}</span></a>
                                </#list>
                            </div>
                        </section>
                        <#break>
                </#switch>
            </#if>
        </#list>
    </#if>
</aside>
