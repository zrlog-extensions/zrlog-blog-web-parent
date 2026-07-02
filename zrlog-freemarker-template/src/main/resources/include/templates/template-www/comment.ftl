<#if log.canComment>
    <div id="comment" class="mt-10 bg-white dark:bg-black p-6 rounded-lg border border-gray-200 dark:border-gray-800 space-y-4">
        <plugin name="${website.comment_plugin_name}" view="widget" param="articleId=${log.logId}"/>
    </div>
</#if>
