# 代码生成（coder）去留

`platform/coder` 这个模块现在不存在：根 `pom` 没有模块，菜单种子也没有「代码生成」入口。`specs/tech-stack.md` 和 `specs/roadmap.md` 仍把 `coder` 写在平台域列表里。那两份是宪法，这里不改。

## 建议

从宪法里去掉 `coder`，不要补一个空模块。

理由：

- 后端没有生成器实现，也没有 `/code/generator/**` 控制器。
- 产品菜单没有指向代码生成页。
- 前端原先的 `views/code/generator` 和 `api/code` 只打向不存在的接口，已经删除，避免再被当成可用功能。

若以后要做代码生成，再单独开阶段，按当时的表结构和模板重做，而不是恢复已删除的页面。

确认要从 `specs/tech-stack.md`、`specs/roadmap.md` 删掉 `coder` 之后再说一声，再改宪法。
