</main>
<footer class="site-footer">
    <div class="site-footer-inner">
        <#if _res.footerLink?has_content>
            <div class="footer-links">${_res.footerLink}</div>
        </#if>
        <p class="copyright">
            <span>${_res.copyrightCurrentYear}</span>
            <span>${webs.title}</span>
            <#if webs.icp?has_content><span>${webs.icp}</span></#if>
        </p>
    </div>
</footer>
<#if webs.webCm?has_content><div hidden>${webs.webCm}</div></#if>
</body>
</html>
