<#include "header.ftl">
<link rel="stylesheet" href="${url}/css/arrange.css">
<section class="py-16 bg-gray-50 dark:bg-black">
    <div class="container bg-white dark:bg-gray-900 mx-auto rounded md:p-8 p-4">
        <plugin name="${arrangePlugin}" view="${reqUriPath}" param="${reqQueryString}"/>
    </div>
</section>
<#include "footer.ftl">
