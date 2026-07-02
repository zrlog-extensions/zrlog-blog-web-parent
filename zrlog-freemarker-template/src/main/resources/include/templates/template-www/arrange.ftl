<#include "header.ftl">
<link rel="stylesheet" href="${url}/css/arrange.css">
<section class="py-12 md:py-16 bg-gray-50 dark:bg-gray-950 min-h-screen">
    <div class="container bg-white dark:bg-black border border-gray-200 dark:border-gray-800 mx-auto rounded-lg md:p-8 p-4">
        <plugin name="${arrangePlugin}" view="${reqUriPath}" param="${reqQueryString}"/>
    </div>
</section>
<#include "footer.ftl">
