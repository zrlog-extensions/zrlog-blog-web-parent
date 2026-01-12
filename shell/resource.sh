webResourcePath=$(pwd)/zrlog-blog-web/src/main/resources
templateResourcePath=$(pwd)/zrlog-freemarker-template/src/main/resources
hexoTemplateResourcePath=$(pwd)/zrlog-hexo-template/src/main/resources
cd ${webResourcePath}
find "." -type f  | sed 's|^\./||' > resource.txt
cd ${templateResourcePath}
find "." -type f  | sed 's|^\./||' >> ${webResourcePath}/resource.txt
cd ${hexoTemplateResourcePath}
find "." -type f  | sed 's|^\./||' >> ${webResourcePath}/resource.txt