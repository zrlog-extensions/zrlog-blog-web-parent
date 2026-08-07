# 博客公开 API 文档

这里是 `zrlog-blog-web` 公开 `/api` 接口的权威契约入口。

- [`openapi.yaml`](openapi.yaml)：路径、参数、响应和 Schema 的 OpenAPI 3.1 来源。
- `zrlog-blog-web/src/main/java/com/zrlog/blog/web/config/BlogRouters.java`：运行时路由来源。
- `zrlog-blog-web/src/main/java/com/zrlog/blog/web/controller/api`：接口实现来源。

人类可浏览的聚合页面由 `zrlog-www` 提供：`https://www.zrlog.com/docs/api?source=blog-web`。官网保存的契约快照只用于展示和静态发布，接口含义始终以本仓库为准。

当前契约覆盖由 `zrlog-blog-web` 直接提供的公开只读接口。`/api/plugin/*` 和 `/api/p/*` 属于插件协议，不在本契约中；每个插件应维护自己的接口定义。

新增或修改公开 API 时必须：

1. 显式声明 HTTP 方法。
2. 使用稳定 DTO，并检查全部调用方。
3. 同步更新 `openapi.yaml` 的参数、响应、示例和兼容语义。
4. 运行 `BlogApiDocumentationContractTest` 和相关接口测试。
