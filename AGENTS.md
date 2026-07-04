# AGENTS.md

这份文档是 AI Agent 在 `zrlog-blog-web` 工程内工作的入口规则。进入本仓库后，先读本文件，再按任务打开更细的源码或文档。

## 工程定位

`zrlog-blog-web` 是 ZrLog 的博客前台渲染层，主要负责：

- 博客前台页面：文章列表、文章详情、评论、归档、标签、分类、搜索、RSS。
- Freemarker `.ftl` 主题渲染。
- Polyglot / Hexo 风格模板兼容。
- 本地主题资源维护，主要目录是 `static/include/templates`。
- Java 单元测试与 JaCoCo 覆盖率检查，目标覆盖率约 80%。

## 目录职责

| 路径 | 职责 |
| --- | --- |
| `zrlog-blog-web/` | 博客前台主模块，包含 Controller、Service、Router、Listener、页面 VO 和公共博客行为。 |
| `zrlog-freemarker-template/` | Freemarker 模板适配与渲染集成。修改 `.ftl` 渲染行为或模板数据暴露时，从这里开始。 |
| `zrlog-polyglot-template/` | Polyglot / Hexo 兼容模板支持和内置 Hexo 风格主题资源。 |
| `static/include/templates/` | 本地 ZrLog 主题工作区。新增 Freemarker 主题优先放这里。 |
| `docs/` | 面向人和 AI 的开发文档。 |
| `conf/`、`shell/` | 本地运行配置与辅助脚本。修改前确认不是用户本地配置。 |

## 必读文档

- [Freemarker 模板数据结构](docs/freemarker-template-data.md)
- [主题工作区说明](static/include/templates/README.md)
- [Signal Notes 主题维护规范](static/include/templates/template-signal-notes/README.md)
- `zrlog-ops/docs/repository-structure-guide.md`
- `zrlog-ops/acceptance/zrlog-blog-web.yaml`

模板字段不要猜。文档没有写到的字段，需要检查对应 Java 页面对象、DTO 或已有模板用法。

## 构建与验证

常用命令：

```bash
mvn -q -DskipTests compile
mvn test
mvn verify
```

修改 Java 逻辑或测试时，至少运行 `mvn test`。修改可能影响覆盖率、打包或发布时，运行 `mvn verify`。

根 `pom.xml` 配置了 JaCoCo 覆盖率检查。不要为了通过验证降低覆盖率阈值，应补充聚焦测试。

## Freemarker 模板规则

Freemarker 模板的根对象是页面对象本身，不是包了一层的 `model`。模板中应直接使用：

```ftl
${title}
${log.title}
${data.rows}
${init.tags}
${webs.title}
${_res.search!'搜索'}
```

不要写成：

```ftl
${model.title}
${model.log.title}
```

新增或修改主题时遵守：

- 主题目录放在 `static/include/templates/<template-name>/`。
- 尽量拆分为 `header.ftl`、`footer.ftl`、`page.ftl`、`detail.ftl`、`article.ftl`、`comment.ftl`、`pager.ftl`、`plugin.ftl`。
- 主题元信息写入 `template.properties`。
- 用户可见文案写入 `language/i18n_*.properties`，不要散落在模板里。
- 优先复用已有 ZrLog 字段，不要随意发明字段约定。
- 可选字段必须加兜底，例如 `${log.thumbnail!''}`。
- 不要硬编码生产域名或外部资源，除非主题文档明确说明该依赖。

## 当前主题约束

`template-signal-notes` 是当前重点维护的 Freemarker 主题，风格参考 Next.js 官方博客。后续 AI Agent 除非收到明确重设计要求，否则必须保留这些约束：

- 可以参考 Next.js Blog 的布局节奏，但不能复制或保留 Next.js / Vercel 官方品牌图标。
- 顶部品牌使用文字，来源优先为 `_res.navBarBrand`，其次为 `webs.title`。
- 顶部搜索、暗黑模式切换、部署/行动按钮应保持同一套视觉语言。
- 不显示搜索快捷键提示，例如 `⌘K`。
- 不增加 `Learn` 导航项。
- 首页文章卡片使用瀑布流布局。
- 首页卡片需要显示文章预览图。
- 首页卡片摘要支持 Markdown 渲染，并使用 `markdown-body`。
- 作者头像使用 `log.header`。
- 首页和详情页都要显示分类，并使用统一的分类 icon。
- 详情页不要再次显示外部列表预览摘要，避免和正文重复。
- 文章正文使用 `markdown-body`。
- Markdown 的暗黑模式必须跟随顶部主题切换状态，不能自己用独立媒体查询。
- 不要把 `.article-body` 限制为 `65ch`。

细节以 [Signal Notes 主题维护规范](static/include/templates/template-signal-notes/README.md) 为准。

## AI 修改流程

1. 先判断任务属于哪一层：Java 博客行为、Freemarker 渲染、Polyglot / Hexo 兼容，还是纯主题资源。
2. 编辑前读取最小必要文档或源码，不要直接猜实现。
3. 保留工作区里已有的用户改动，不要 reset、restore 或覆盖无关文件。
4. 修改 Java 行为时，同步补充或调整测试。
5. 修改模板时，先核对 `docs/freemarker-template-data.md` 或 Java DTO 中的数据字段。
6. 修改主题视觉时，优先遵守当前主题文档，不要把已经确认的交互和样式退回去。
7. 完成后运行最小但有效的验证命令。
8. 最终回复说明改了什么文件，以及验证是否通过。

## 常见任务入口

| 任务 | 起点 |
| --- | --- |
| 修改公开文章行为 | `zrlog-blog-web/src/main/java` |
| 新增或修复 Freemarker 数据字段 | `zrlog-freemarker-template/` 和 `docs/freemarker-template-data.md` |
| 新建 `.ftl` 主题 | `static/include/templates/` |
| 维护 `template-signal-notes` | `static/include/templates/template-signal-notes/README.md` |
| 修复 Hexo 主题兼容 | `zrlog-polyglot-template/` |
| 补充测试和覆盖率 | 各模块的 `src/test` 目录 |
