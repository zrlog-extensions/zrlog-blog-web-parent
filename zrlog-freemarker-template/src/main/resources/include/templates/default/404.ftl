<div class="empty-state not-found">
    <p class="empty-state-mark" aria-hidden="true">404</p>
    <h1>${_res.notFound}</h1>
    <p>${_res.notFoundDescription}</p>
    <form class="search-form search-form-main" method="post" action="${searchUrl}" role="search" autocomplete="off">
        <label class="screen-reader-text" for="not-found-search">${_res.searchLabel}</label>
        <input id="not-found-search" type="search" value="${key!''}" name="key" placeholder="${_res.searchTip}" autocomplete="off"/>
        <button type="submit">${_res.search}</button>
    </form>
    <a class="text-link" href="${baseUrl}">${_res.backHome}</a>
</div>
