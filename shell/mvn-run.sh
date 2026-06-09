export CDN=0
./mvnw clean compile -U
./mvnw -pl zrlog-blog-web exec:java -Dexec.mainClass="com.zrlog.blog.Application"
