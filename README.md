# zrlog-blog-web

公开博客 API 的权威契约见 [docs/api/README.md](docs/api/README.md)。人类可浏览页面由 `zrlog-www` 聚合提供，接口定义仍归本仓库所有。

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

## 默认主题评审环境

使用内存数据库启动一个每次都会重置的干净博客前台：

```bash
bash shell/memory-run.sh
```

站点和安装配置位于 `conf/memory-install.json`，页面评审数据位于 `conf/memory-content.json`。
该入口使用 `.zrlog-memory/` 作为隔离运行目录，不读取或覆盖仓库现有的 `conf/db.properties`。

生成安装默认主题的发布预览图时，必须使用 `bash shell/memory-run.sh --installed-default`。该模式读取
`conf/default-install-preview.json`，只保留 install-web 创建的默认文章、分类、标签和导航，不注入
`conf/memory-content.json`；扩展评审数据只能用于页面开发与回归截图，不能作为 `template.properties`
所引用的主题预览图来源。
