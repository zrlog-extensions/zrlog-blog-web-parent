<!-- 返回顶部按钮 -->
<button
        id="back-to-top"
        class="fixed bottom-10 right-10 w-12 h-12 bg-blue-600 text-white rounded-lg shadow-lg flex items-center justify-center opacity-0 invisible transition-all duration-300 z-[100] hover:bg-blue-700 group"
>
    <i class="ri-arrow-up-line text-2xl transition-transform group-hover:-translate-y-1"></i>
</button>
<footer class="relative bg-gray-50 dark:bg-gray-950 border-t border-gray-200 dark:border-gray-800 py-16 md:py-20 overflow-hidden"
        id="footer">
    <div class="container mx-auto px-4 md:px-6 relative z-10">
        ${_res['footerLinkExt']!''}
        <div class="mt-16 pt-8 border-t border-gray-200 dark:border-gray-800 flex flex-col md:flex-row justify-between items-center gap-6">
            <div class="text-gray-500 dark:text-gray-400 text-sm font-medium">
                © 2026 ${webSite.title!''}
                <#if webSite.icp != ''>
                    ·
                    <div class="hover:text-blue-600 transition-colors">${webSite.icp}</div>
                </#if>
            </div>
            <#include "footer-links.ftl">
        </div>
    </div>
</footer>
<script>
    // 返回顶部按钮
    const backToTopButton = document.getElementById("back-to-top");
    window.addEventListener("scroll", () => {
        if (window.pageYOffset > 300) {
            backToTopButton.classList.remove("opacity-0", "invisible");
            backToTopButton.classList.add("opacity-100", "visible");
        } else {
            backToTopButton.classList.remove("opacity-100", "visible");
            backToTopButton.classList.add("opacity-0", "invisible");
        }
    });
    backToTopButton.addEventListener("click", () => {
        window.scrollTo({
            top: 0,
            behavior: "smooth",
        });
    });


    // 主题切换
    const themeSwitches = Array.from(document.querySelectorAll(".theme-switch input"));

    function syncThemeSwitches(checked) {
        themeSwitches.forEach((switchEl) => {
            switchEl.checked = checked;
        });
    }

    function applyTheme() {
        const userPreference = localStorage.getItem("theme");
        const prefersDark = window.matchMedia("(prefers-color-scheme: dark)").matches;
        const shouldUseDark = userPreference === "dark" || (!userPreference && prefersDark);

        document.documentElement.classList.toggle("dark", shouldUseDark);
        syncThemeSwitches(shouldUseDark);
    }

    applyTheme();

    themeSwitches.forEach((switchEl) => {
        switchEl.addEventListener("change", function () {
            localStorage.theme = this.checked ? "dark" : "light";
            applyTheme();
        });
    });

    const colorSchemeQuery = window.matchMedia("(prefers-color-scheme: dark)");
    function handleSystemThemeChange() {
        const userPreference = localStorage.getItem("theme");
        if (userPreference === "dark" || (!userPreference && colorSchemeQuery.matches)) {
            document.documentElement.classList.add("dark");
            syncThemeSwitches(true);
        } else {
            document.documentElement.classList.remove("dark");
            syncThemeSwitches(false);
        }
    }
    if (colorSchemeQuery.addEventListener) {
        colorSchemeQuery.addEventListener("change", handleSystemThemeChange);
    } else if (colorSchemeQuery.addListener) {
        colorSchemeQuery.addListener(handleSystemThemeChange);
    }

    const btn = document.getElementById("toggleSidebar");
    const sidebar = document.getElementById("sidebar");
    const overlay = document.getElementById('overlay');
    const closeBtn = document.getElementById('closeSidebar');
    const header = document.getElementById('header');

    function closeSidebar() {
        sidebar.classList.add('-translate-x-full');
        overlay.classList.add('hidden');
        setTimeout(() => {
            sidebar.classList.add('hidden');
        }, 300); // 等待动画结束再隐藏
    }

    if (closeBtn) {
        closeBtn.addEventListener('click', closeSidebar);
    }

    if (overlay) overlay.addEventListener('click', closeSidebar);

    if (btn) {
        btn.addEventListener("click", () => {
            // 先取消隐藏
            sidebar.classList.remove('hidden');
            overlay.classList.remove('hidden');
            // 然后触发滑出
            setTimeout(() => {
                sidebar.classList.remove('-translate-x-full');
            }, 10);
        });
    }

    // 导航栏滚动效果
    window.addEventListener('scroll', () => {
        // const isDark = document.documentElement.classList.contains('dark');
        if (window.scrollY > 20) {
            header.classList.add('shadow-lg', 'py-3');
            header.classList.remove('py-4', 'shadow-sm');
            // 动态调整背景深浅
            header.classList.add('bg-white', 'dark:bg-black');
        } else {
            header.classList.remove('shadow-lg', 'py-3');
            header.classList.add('py-4', 'shadow-sm');
            header.classList.add('bg-white', 'dark:bg-black');
        }
    });

    // ESC 键也可关闭（可选）
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') {
            closeSidebar();
        }
    });
</script>
<#include "_common/statistcis.ftl">
</body>
</html>
