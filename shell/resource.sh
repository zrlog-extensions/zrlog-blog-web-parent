basePwd=$(pwd)
webResourcePath=$(pwd)/zrlog-blog-web/src/main/resources
templateResourcePath=$(pwd)/zrlog-freemarker-template/src/main/resources
polyglotTemplateResourcePath=$(pwd)/zrlog-polyglot-template/src/main/resources
cd ${webResourcePath}
find "." -type f  | sed 's|^\./||' > ${basePwd}/temp.txt
cd ${templateResourcePath}
find "." -type f  | sed 's|^\./||' >> ${basePwd}/temp.txt
cd ${polyglotTemplateResourcePath}
find "." -type f  | sed 's|^\./||' >> ${basePwd}/temp.txt
export LC_ALL=C
sort ${basePwd}/temp.txt > ${webResourcePath}/resource.txt