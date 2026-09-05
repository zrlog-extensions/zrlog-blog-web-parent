# Server Markdown Rendering

`MarkdownJsRenderer` uses the standalone bundle from `zrlog-editor` 2.1.32.
The bundle is committed under `zrlog-polyglot-template/src/main/resources/conf/base/scripts/`
and packaged in the JAR, so rendering does not require network access or a browser DOM.

- Published artifact: `https://raw.githubusercontent.com/zrlog/zrlog-editor/refs/heads/main/artifacts/zrlog-markdown-v2.1.32.min.js`
- Resource: `conf/base/scripts/zrlog-markdown-v2.1.32.min.js`
- SHA-256: `75b63055e86017f32c028ba0ecff8cfe440334c32b43d94bad402e21908cc255`
- JavaScript entry: `ZrLogMarkdown.markdownToHtml(markdown)`
- Bundled dependencies: Marked 16.0.0, Highlight.js 11.11.1, KaTeX 0.16.22.
- License files are included under `META-INF/licenses/`.

Java 17+ loads the bundle in an isolated GraalJS context with host access and IO disabled.
The Java 11 implementation continues to return `null` and log a warning, so callers can
retain existing HTML. The existing `MARKED_VERSION` constant is retained for compatibility;
`EDITOR_VERSION` identifies the published renderer bundle.

Rendering includes GFM and line breaks, CJK strong boundaries, code highlighting,
inline/display math, and `math`/`latex`/`katex` code fences. `flow` and `seq` code fences
retain their escaped source as code blocks. Link preview fetching and browser hydration
are not performed. Raw HTML retains the editor's existing behavior and is not sanitized.
Display pages still need the appropriate Markdown, Highlight.js, and KaTeX CSS/fonts.

When updating the renderer, first publish the corresponding editor version, copy its
versioned min.js without rebuilding or modifying it, update `EDITOR_VERSION` in both
Java implementations and this artifact checksum, and run `mvn verify`.
