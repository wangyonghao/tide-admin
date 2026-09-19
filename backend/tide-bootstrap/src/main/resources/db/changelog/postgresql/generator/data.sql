-- liquibase formatted sql

-- changeset wyhao:1
-- comment generator-初始化默认菜单
INSERT INTO "sys_menu"
("id", "name", "parent_id", "type", "path", "component", "redirect", "icon", "is_external", "is_cache", "is_hidden",
 "permission", "sort", "status", "create_user", "create_time")
VALUES (9000, '开发工具', 0, 1, '/code', 'Layout', '/code/generator', 'code-release-managment', FALSE, FALSE, FALSE,
        NULL, 9, 1, 1, NOW()),
       (9010, '代码生成', 9000, 2, '/code/generator', 'code/generator/index', NULL, 'code', FALSE, FALSE, FALSE, NULL,
        1, 1, 1, NOW()),
       (9011, '列表', 9010, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'code:generator:list', 1, 1, 1, NOW()),
       (9012, '配置', 9010, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'code:generator:config', 2, 1, 1, NOW()),
       (9013, '预览', 9010, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'code:generator:preview', 3, 1, 1, NOW()),
       (9014, '生成', 9010, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'code:generator:generate', 4, 1, 1, NOW());