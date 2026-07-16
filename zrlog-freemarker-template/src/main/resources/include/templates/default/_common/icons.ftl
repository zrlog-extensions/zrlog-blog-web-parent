<#macro themeIcon name className="">
    <svg class="${className}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"
         stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
        <#switch name>
            <#case "archives">
                <rect x="3.5" y="5" width="17" height="15.5" rx="2.5"/>
                <path d="M7.5 3v4M16.5 3v4M3.5 9.5h17M8 14h3M13.5 14h2.5"/>
                <#break>
            <#case "categories">
                <path d="M3.5 7.5A2.5 2.5 0 0 1 6 5h4l2 2h6A2.5 2.5 0 0 1 20.5 9.5v7A2.5 2.5 0 0 1 18 19H6a2.5 2.5 0 0 1-2.5-2.5Z"/>
                <#break>
            <#case "links">
                <path d="m9.5 14.5 5-5M7.2 16.8l-1.1 1.1a3.5 3.5 0 0 1-5-5l3.4-3.4a3.5 3.5 0 0 1 5 0M16.8 7.2l1.1-1.1a3.5 3.5 0 1 1 5 5l-3.4 3.4a3.5 3.5 0 0 1-5 0" transform="translate(-.5 -.5) scale(1.04)"/>
                <#break>
            <#case "external">
                <path d="M14 4h6v6M20 4l-9 9"/>
                <path d="M18 13v5a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h5"/>
                <#break>
            <#case "tags">
                <path d="M4 4h6.8a2 2 0 0 1 1.4.6l7.2 7.2a2 2 0 0 1 0 2.8l-4.8 4.8a2 2 0 0 1-2.8 0l-7.2-7.2A2 2 0 0 1 4 10.8Z"/>
                <circle cx="8" cy="8" r="1.25"/>
                <#break>
        </#switch>
    </svg>
</#macro>
