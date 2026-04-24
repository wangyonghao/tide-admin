-- liquibase formatted sql

-- changeset wyhao:1
-- comment openapi-初始化默认菜单
INSERT INTO "sys_menu"
("id", "name", "parent_id", "type", "path", "component", "redirect", "icon", "is_external", "is_cache", "is_hidden",
 "permission", "sort", "status", "create_user", "create_time")
VALUES (7000, '能力开放', 0, 1, '/open', 'Layout', '/open/app', 'expand', FALSE, FALSE, FALSE, NULL, 7, 1, 1, NOW()),
       (7010, '应用管理', 7000, 2, '/open/app', 'open/app/index', NULL, 'common', FALSE, FALSE, FALSE, NULL, 1, 1, 1,
        NOW()),
       (7011, '列表', 7010, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'open:app:list', 1, 1, 1, NOW()),
       (7012, '详情', 7010, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'open:app:get', 2, 1, 1, NOW()),
       (7013, '新增', 7010, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'open:app:create', 3, 1, 1, NOW()),
       (7014, '修改', 7010, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'open:app:update', 4, 1, 1, NOW()),
       (7015, '删除', 7010, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'open:app:delete', 5, 1, 1, NOW()),
       (7016, '导出', 7010, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'open:app:export', 6, 1, 1, NOW()),
       (7017, '查看密钥', 7010, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'open:app:secret', 7, 1, 1, NOW()),
       (7018, '重置密钥', 7010, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'open:app:resetSecret', 8, 1, 1, NOW());
