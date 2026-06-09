# zrlog-blog-web

`zrlog-blog-web` 是 ZrLog 的博客前台渲染工程，负责文章列表、文章详情、评论、归档、标签、分类、搜索、RSS、模板渲染和主题资源。

面向 AI Agent 的开发约束和主题维护规范放在 [AGENTS.md](AGENTS.md)。

## 模块

- `zrlog-blog-web/`: 博客前台主模块，包含 Controller、Service、Router、Listener、页面 VO 等。
- `zrlog-freemarker-template/`: Freemarker 模板适配与渲染集成。
- `zrlog-polyglot-template/`: Polyglot / Hexo 兼容模板支持。
- `static/include/templates/`: 本地 ZrLog 主题开发目录。
- `docs/`: 开发文档。

## 文档

- [Freemarker template data contract](docs/freemarker-template-data.md)
- [Theme workspace guide](static/include/templates/README.md)
- [Signal Notes theme guide](static/include/templates/template-signal-notes/README.md)

## 构建与测试

```bash
mvn -q -DskipTests compile
mvn test
mvn verify
```
