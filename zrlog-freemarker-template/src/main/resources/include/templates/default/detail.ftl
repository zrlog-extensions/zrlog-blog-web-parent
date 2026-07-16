<#include "header.ftl">
<section class="content-column <#if !log??>content-column-wide</#if>">
    <#if log??>
        <#include "article.ftl">
        <#include "comment.ftl">
    <#else>
        <#include "404.ftl">
    </#if>
</section>
<#if log??><#include "plugin.ftl"></#if>
<#include "footer.ftl">
