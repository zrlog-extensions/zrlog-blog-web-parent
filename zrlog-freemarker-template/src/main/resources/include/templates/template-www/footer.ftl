<!-- 返回顶部按钮 -->
<button
        id="back-to-top"
        class="fixed bottom-10 right-10 w-12 h-12 bg-blue-600 text-white rounded-full shadow-2xl flex items-center justify-center opacity-0 invisible transition-all duration-500 z-[100] hover:scale-110 active:scale-95 group"
>
    <i class="ri-arrow-up-line text-2xl transition-transform group-hover:-translate-y-1"></i>
</button>
<footer class="relative bg-gray-50/50 dark:bg-gray-950/50 border-t border-gray-200 dark:border-white/5 py-16 md:py-24 overflow-hidden"
        id="footer">
    <!-- 背景装饰 -->
    <div class="absolute inset-0 overflow-hidden pointer-events-none">
        <div class="absolute bottom-[-10%] left-[-10%] w-[40%] h-[40%] bg-blue-400/5 dark:bg-blue-600/5 rounded-full blur-[100px]"></div>
    </div>

    <div class="container mx-auto px-4 md:px-6 relative z-10">
        ${_res['footerLinkExt']!''}
        <div class="mt-16 pt-8 border-t border-gray-200 dark:border-white/5 flex flex-col md:flex-row justify-between items-center gap-6">
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
    const themeSwitch = document.querySelector(".theme-switch input");

    // 页面加载时，根据 localStorage 设置初始主题
    if (localStorage.theme === "dark") {
        document.documentElement.classList.add("dark");
        if (themeSwitch) themeSwitch.checked = true;
    }

    const userPreference = localStorage.getItem("theme");

    function applyTheme() {
        if (userPreference === "dark" || (!userPreference && window.matchMedia("(prefers-color-scheme: dark)").matches)) {
            document.documentElement.classList.add("dark");
            if (themeSwitch) themeSwitch.checked = true;
        } else {
            document.documentElement.classList.remove("dark");
            if (themeSwitch) themeSwitch.checked = false;
        }
    }

    // 初始化：加载页面时设置主题
    applyTheme();

    if (themeSwitch) {
        themeSwitch.addEventListener("change", function () {
            if (this.checked) {
                document.documentElement.classList.add('dark'); // ✅ 开启暗黑模式
                localStorage.theme = "dark";
            } else {
                document.documentElement.classList.remove('dark'); // ✅ 关闭暗黑模式
                localStorage.theme = "light";
            }
        });
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
            header.classList.add('bg-white/95', 'dark:bg-gray-900/95');
            header.classList.remove('bg-white/70', 'dark:bg-gray-900/80');
        } else {
            header.classList.remove('shadow-lg', 'py-3');
            header.classList.add('py-4', 'shadow-sm');
            header.classList.add('bg-white/70', 'dark:bg-gray-900/80');
            header.classList.remove('bg-white/95', 'dark:bg-gray-900/95');
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
