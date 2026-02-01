<section class="py-12">
    <div role="main" class="px-4">
        <article class="bg-white dark:bg-gray-900 shadow rounded-lg p-8 text-center">
            <header class="mb-6">
                <h1 class="text-2xl font-bold text-red-600">${_res.notFound}</h1>
            </header>

            <div class="space-y-4">
                <p class="text-gray-600">抱歉，没有符合您搜索条件的结果。请换其它关键词再试。</p>

                <form method="post" action="${searchUrl}" class="flex flex-col sm:flex-row items-center gap-3 justify-center">
                    <input
                            type="text"
                            name="key"
                            value='${key!""}'
                            placeholder="${_res.searchTip}"
                            class="w-full sm:w-64 border border-gray-300 rounded px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-primary"
                    />
                    <input
                            type="submit"
                            name="submit"
                            value="${_res.search}"
                            class="bg-primary text-white text-sm px-5 py-2 rounded hover:bg-primary cursor-pointer"
                    />
                </form>
            </div>
        </article>
    </div>
</section>
