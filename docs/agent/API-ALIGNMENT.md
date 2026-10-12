# 前后端主路径对齐

菜单组件路径是 `/src/views/${component}/index.vue`（见 `frontend/apps/admin/src/router/guard.ts`）。下面只记 iam / tenant / system 上，页面会点到、但先前对不上的地方，以及仍未做的缺口。

## 已对齐

| 表面 | 处理 |
|------|------|
| `/tenant/management`、`/tenant/package` | 补了列表页。后端分页、详情、修改、删除不再返回空。套餐菜单变更会回写该套餐下租户管理员角色。默认租户不能删。仍被租户引用的套餐不能删。 |
| 用户修改、用户详情 | 权限码改为种子里的 `system:user:update`、`system:user:get`。 |
| `GET /system/user/dict` | 公告选人用这个接口，后端已提供。 |
| `GET /system/user/import/template` | 与 `/system/user/download-template` 同一处理。另有 `POST /import/parse`、`POST /import`、`PUT /{id}/role`。导入页面还没接。 |
| `GET /system/department/dict/tree` | 部门下拉走这条，返回部门树，不再套一层 `Result`。原来的 `GET /dict/tree` 仍保留。 |
| `GET /system/role/list` | 角色下拉不再请求不存在的 `/system/role/option`。 |
| 邮件 / 存储 / 按 key 读配置、短信配置读取 | 权限码改为种子里的 `system:config:get`。 |
| 公告导出按钮 | 后端没有 `GET /system/notice/export`，按钮已去掉。 |
| 短信日志删除 | 后端没有删除接口，监控日志页上的删除已去掉。导出仍走 `GET /system/sms/log/export`。 |
| `views/monitor/sms` | 菜单实际是 `/monitor/log`，这份重复页已删。 |
| 代码生成页和 `api/code` | 后端没有对应模块，已删。见 `docs/agent/CODER.md`。 |

创建租户时管理员密码仍用前端 `encryptByRsa`（开发公钥）。修改租户不改管理员口令。

## 仍未对齐

这些没有在本轮补接口或补按钮，避免扩成新功能。

- `PUT /tenant/management/{id}/admin/pwd` 不存在。菜单权限 `tenant:management:updateAdminUserPwd` 还在种子里，页面上没有入口。
- 租户、套餐的导出接口不存在。`GET /tenant/package/tree`、`GET /tenant/package/dict`、`GET /tenant/package/menu/tree` 也不存在；套餐表单改用 `GET /system/menu/all`。
- `system:config:export` 没有写进菜单种子。
- `POST /system/role/{id}/user`（分配用户）不存在。角色页目前只取消分配。
- `GET /user/profile` 不存在。个人中心用的是 `/auth/user` 和已有的修改接口。
- 登录日志导出：页面走 `GET /auth/login-log/export`。`logApi.exportLoginLog` 仍指向不存在的 `GET /system/log/export/login`，页面没有调用它。
- 用户导入接口已有，导入向导页面没有接上。
- 租户 `tree()`、套餐 `tree()` 仍返回空列表。
- 更新租户时，MyBatis `NOT_NULL` 策略不会把过期时间、描述清空成 null。
