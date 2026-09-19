-- liquibase formatted sql

-- changeset wyhao:1
-- comment: tenant-初始化默认菜单
INSERT INTO "sys_menu" ("id", "name", "parent_id", "type", "path", "component", "redirect", "icon", "is_external",
                        "is_cache", "is_hidden", "permission", "sort", "status", "create_user", "create_time")
VALUES (3000, '租户管理', 0, 1, '/tenant', 'Layout', '/tenant/management', 'user-group', FALSE, FALSE, FALSE, NULL, 6,
        1, 1, NOW()),
       (3010, '租户管理', 3000, 2, '/tenant/management', 'tenant/management/index', NULL, 'user-group', FALSE, FALSE,
        FALSE, NULL, 1, 1, 1, NOW()),
       (3011, '列表', 3010, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'tenant:management:list', 1, 1, 1, NOW()),
       (3012, '详情', 3010, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'tenant:management:get', 2, 1, 1, NOW()),
       (3013, '新增', 3010, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'tenant:management:create', 3, 1, 1, NOW()),
       (3014, '修改', 3010, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'tenant:management:update', 4, 1, 1, NOW()),
       (3015, '删除', 3010, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'tenant:management:delete', 5, 1, 1, NOW()),
       (3016, '修改租户管理员密码', 3010, 3, NULL, NULL, NULL, NULL, FALSE, FALSE, FALSE,
        'tenant:management:updateAdminUserPwd', 6, 1, 1, NOW()),
       (3020, '套餐管理', 3000, 2, '/tenant/package', 'tenant/package/index', NULL, 'project', FALSE, FALSE, FALSE,
        NULL, 2, 1, 1, NOW()),
       (3021, '列表', 3020, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'tenant:package:list', 1, 1, 1, NOW()),
       (3022, '详情', 3020, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'tenant:package:get', 2, 1, 1, NOW()),
       (3023, '新增', 3020, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'tenant:package:create', 3, 1, 1, NOW()),
       (3024, '修改', 3020, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'tenant:package:update', 4, 1, 1, NOW()),
       (3025, '删除', 3020, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'tenant:package:delete', 5, 1, 1, NOW());