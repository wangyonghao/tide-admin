-- liquibase formatted sql

-- changeset wyhao:1
-- comment system-初始化表数据
INSERT INTO "sys_menu"
("id", "name", "parent_id", "type", "path", "component", "redirect", "icon", "is_external", "is_cache", "is_hidden", "permission", "sort", "status", "create_user", "create_time")
VALUES (1000, '系统管理', 0, 1, '/system', 'Layout', '/system/user', 'lucide:settings-2', FALSE, FALSE, FALSE, NULL, 1, 1, 1, NOW()),
       (1010, '用户管理', 1000, 2, '/system/user', 'system/user/index', NULL, 'lucide:user', FALSE, FALSE, FALSE, NULL, 1, 1, 1, NOW()),
       (1011, '列表', 1010, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:user:list', 1, 1, 1, NOW()),
       (1012, '详情', 1010, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:user:get', 2, 1, 1, NOW()),
       (1013, '新增', 1010, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:user:create', 3, 1, 1, NOW()),
       (1014, '修改', 1010, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:user:update', 4, 1, 1, NOW()),
       (1015, '删除', 1010, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:user:delete', 5, 1, 1, NOW()),
       (1016, '导出', 1010, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:user:export', 6, 1, 1, NOW()),
       (1017, '导入', 1010, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:user:import', 7, 1, 1, NOW()),
       (1018, '重置密码', 1010, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:user:resetPwd', 8, 1, 1, NOW()),
       (1019, '分配角色', 1010, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:user:updateRole', 9, 1, 1, NOW()),

       (1030, '角色管理', 1000, 2, '/system/role', 'system/role/index', NULL, 'lucide:shield-user', FALSE, FALSE, FALSE, NULL, 2, 1, 1, NOW()),
       (1031, '列表', 1030, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:role:list', 1, 1, 1, NOW()),
       (1032, '详情', 1030, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:role:get', 2, 1, 1, NOW()),
       (1033, '新增', 1030, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:role:create', 3, 1, 1, NOW()),
       (1034, '修改', 1030, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:role:update', 4, 1, 1, NOW()),
       (1035, '删除', 1030, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:role:delete', 5, 1, 1, NOW()),
       (1036, '修改权限', 1030, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:role:updatePermission', 6, 1, 1, NOW()),
       (1037, '分配', 1030, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:role:assign', 7, 1, 1, NOW()),
       (1038, '取消分配', 1030, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:role:unassign', 8, 1, 1, NOW()),

       (1050, '菜单管理', 1000, 2, '/system/menu', 'system/menu/index', NULL, 'lucide:menu', FALSE, FALSE, FALSE, NULL, 3, 1, 1, NOW()),
       (1051, '列表', 1050, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:menu:list', 1, 1, 1, NOW()),
       (1052, '详情', 1050, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:menu:get', 2, 1, 1, NOW()),
       (1053, '新增', 1050, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:menu:create', 3, 1, 1, NOW()),
       (1054, '修改', 1050, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:menu:update', 4, 1, 1, NOW()),
       (1055, '删除', 1050, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:menu:delete', 5, 1, 1, NOW()),
       (1056, '清除缓存', 1050, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:menu:clearCache', 6, 1, 1, NOW()),

       (1070, '部门管理', 1000, 2, '/system/dept', 'system/dept/index', NULL, 'fluent:organization-48-regular', FALSE, FALSE, FALSE, NULL, 4, 1, 1, NOW()),
       (1071, '列表', 1070, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:dept:list', 1, 1, 1, NOW()),
       (1072, '详情', 1070, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:dept:get', 2, 1, 1, NOW()),
       (1073, '新增', 1070, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:dept:create', 3, 1, 1, NOW()),
       (1074, '修改', 1070, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:dept:update', 4, 1, 1, NOW()),
       (1075, '删除', 1070, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:dept:delete', 5, 1, 1, NOW()),
       (1076, '导出', 1070, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:dept:export', 6, 1, 1, NOW()),

       (1090, '通知公告', 1000, 2, '/system/notice', 'system/notice/index', NULL, 'pepicons-pencil:bulletin-notice', FALSE, FALSE, FALSE, NULL, 5, 1, 1, NOW()),
       (1091, '列表', 1090, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:notice:list', 1, 1, 1, NOW()),
       (1092, '详情', 1090, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:notice:get', 2, 1, 1, NOW()),
       (1093, '修改', 1090, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:notice:update', 5, 1, 1, NOW()),
       (1094, '删除', 1090, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:notice:delete', 6, 1, 1, NOW()),

       (1110, '文件管理', 1000, 2, '/system/file', 'system/file/index', NULL, 'lucide:folder-tree', FALSE, FALSE, FALSE,
        NULL, 6, 1, 1, NOW()),
       (1111, '列表', 1110, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:file:list', 1, 1, 1, NOW()),
       (1112, '详情', 1110, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:file:get', 2, 1, 1, NOW()),
       (1113, '上传', 1110, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:file:upload', 3, 1, 1, NOW()),
       (1114, '修改', 1110, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:file:update', 4, 1, 1, NOW()),
       (1115, '删除', 1110, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:file:delete', 5, 1, 1, NOW()),
       (1116, '下载', 1110, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:file:download', 6, 1, 1, NOW()),
       (1117, '创建文件夹', 1110, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:file:createDir', 7, 1, 1, NOW()),
       (1118, '计算文件夹大小', 1110, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:file:calcDirSize', 8, 1, 1,
        NOW()),

       (1130, '字典管理', 1000, 2, '/system/dict', 'system/dict/index', NULL, 'arcticons:colordict', FALSE, FALSE, FALSE, NULL, 7, 1, 1, NOW()),
       (1131, '列表', 1130, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:dict:list', 1, 1, 1, NOW()),
       (1132, '详情', 1130, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:dict:get', 2, 1, 1, NOW()),
       (1133, '新增', 1130, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:dict:create', 3, 1, 1, NOW()),
       (1134, '修改', 1130, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:dict:update', 4, 1, 1, NOW()),
       (1135, '删除', 1130, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:dict:delete', 5, 1, 1, NOW()),
       (1136, '清除缓存', 1130, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:dict:clearCache', 6, 1, 1, NOW()),
      
       (1140, '字典项管理', 1000, 2, '/system/dict/item', 'system/dict/item/index', NULL, 'bookmark', FALSE, FALSE, TRUE, NULL, 8, 1, 1, NOW()),
       (1141, '列表', 1140, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:dictItem:list', 1, 1, 1, NOW()),
       (1142, '详情', 1140, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:dictItem:get', 2, 1, 1, NOW()),
       (1143, '新增', 1140, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:dictItem:create', 3, 1, 1, NOW()),
       (1144, '修改', 1140, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:dictItem:update', 4, 1, 1, NOW()),
       (1145, '删除', 1140, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:dictItem:delete', 5, 1, 1, NOW()),

       (1150, '系统配置', 1000, 2, '/system/config', 'system/config/index', NULL, 'lucide:settings', FALSE, FALSE, FALSE, NULL, 9, 1, 1, NOW()),
       (1151, '查看', 1150, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:config:get', 1, 1, 1, NOW()),
       (1152, '修改', 1150, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'system:config:edit', 2, 1, 1, NOW()),
       
       (2000, '系统监控', 0, 1, '/monitor', 'Layout', '/monitor/online', 'computer', FALSE, FALSE, FALSE, NULL, 2, 1, 1, NOW()),
       (2010, '在线用户', 2000, 2, '/monitor/online', 'monitor/online/index', NULL, 'user', FALSE, FALSE, FALSE, NULL, 1, 1, 1, NOW()),
       (2011, '列表', 2010, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'monitor:online:list', 1, 1, 1, NOW()),
       (2012, '强退', 2010, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'monitor:online:kickout', 2, 1, 1, NOW()),

       (2030, '系统日志', 2000, 2, '/monitor/log', 'monitor/log/index', NULL, 'ix:log', FALSE, FALSE, FALSE, NULL, 2, 1, 1, NOW()),
       (2031, '列表', 2030, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'monitor:log:list', 1, 1, 1, NOW()),
       (2032, '详情', 2030, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'monitor:log:get', 2, 1, 1, NOW()),
       (2033, '导出', 2030, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'monitor:log:export', 3, 1, 1, NOW()),

       (2050, '短信日志', 2000, 2, '/system/sms/log', 'monitor/sms/log/index', NULL, 'lucide:message-circle', FALSE, FALSE, FALSE, NULL, 3, 1, 1, NOW()),
       (2051, '列表', 2050, 3, NULL, NULL, NULL, NULL, FALSE, FALSE, FALSE, 'system:smsLog:list', 1, 1, 1, NOW()),
       (2052, '删除', 2050, 3, NULL, NULL, NULL, NULL, FALSE, FALSE, FALSE, 'system:smsLog:delete', 2, 1, 1, NOW()),
       (2053, '导出', 2050, 3, NULL, NULL, NULL, NULL, FALSE, FALSE, FALSE, 'system:smsLog:export', 3, 1, 1, NOW());

-- 初始化默认部门
INSERT INTO "sys_dept" ("id", "name", "parent_id", "ancestors", "description", "sort", "status", "is_builtin", "create_user", "create_time")
VALUES (1, 'Xxx科技有限公司', 0, '0', '系统初始部门', 1, 1, TRUE, 1, NOW()),
       (547887852587843590, 'Xxx（天津）科技有限公司', 1, '0,1', NULL, 1, 1, FALSE, 1, NOW()),
       (547887852587843591, '研发部', 547887852587843590, '0,1,547887852587843590', NULL, 1, 1, FALSE, 1, NOW()),
       (547887852587843592, 'UI部', 547887852587843590, '0,1,547887852587843590', NULL, 2, 1, FALSE, 1, NOW()),
       (547887852587843593, '测试部', 547887852587843590, '0,1,547887852587843590', NULL, 3, 1, FALSE, 1, NOW()),
       (547887852587843594, '运维部', 547887852587843590, '0,1,547887852587843590', NULL, 4, 1, FALSE, 1, NOW()),
       (547887852587843595, '研发一组', 547887852587843591, '0,1,547887852587843590,547887852587843591', NULL, 1, 1, FALSE, 1, NOW()),
       (547887852587843596, '研发二组', 547887852587843591, '0,1,547887852587843590,547887852587843591', NULL, 2, 2, FALSE, 1, NOW()),

       (547887852587843597, 'Xxx（四川）科技有限公司', 1, '0,1', NULL, 2, 1, FALSE, 1, NOW()),
       (547887852587843598, '研发部', 547887852587843597, '0,1,547887852587843597', NULL, 1, 1, FALSE, 1, NOW()),
       (547887852587843599, '研发一组', 547887852587843598, '0,1,547887852587843597,547887852587843598', NULL, 1, 1, FALSE, 1, NOW()),

       (547887852587843600, 'Xxx（江西）科技有限公司', 1, '0,1', NULL, 3, 1, FALSE, 1, NOW()),
       (547887852587843601, '研发部', 547887852587843600, '0,1,547887852587843600', NULL, 1, 1, FALSE, 1, NOW()),
       (547887852587843602, '研发一组', 547887852587843601, '0,1,547887852587843600,547887852587843601', NULL, 1, 1, FALSE, 1, NOW()),

       (547887852587843603, 'Xxx（江苏）科技有限公司', 1, '0,1', NULL, 4, 1, FALSE, 1, NOW()),
       (547887852587843604, '研发部', 547887852587843603, '0,1,547887852587843603', NULL, 1, 1, FALSE, 1, NOW()),
       (547887852587843605, '研发一组', 547887852587843604, '0,1,547887852587843603,547887852587843604', NULL, 1, 1, FALSE, 1, NOW()),

       (547887852587843606, 'Xxx（浙江）科技有限公司', 1, '0,1', NULL, 5, 1, FALSE, 1, NOW()),
       (547887852587843607, '研发部', 547887852587843606, '0,1,547887852587843606', NULL, 1, 1, FALSE, 1, NOW()),
       (547887852587843608, '研发一组', 547887852587843607, '0,1,547887852587843606,547887852587843607', NULL, 1, 1, FALSE, 1, NOW()),

       (547887852587843609, 'Xxx（湖南）科技有限公司', 1, '0,1', NULL, 6, 1, FALSE, 1, NOW()),
       (547887852587843610, '研发部', 547887852587843609, '0,1,547887852587843609', NULL, 1, 1, FALSE, 1, NOW()),
       (547887852587843611, '研发一组', 547887852587843610, '0,1,547887852587843609,547887852587843610', NULL, 1, 1, FALSE, 1, NOW());

-- 初始化默认角色
INSERT INTO "sys_role"
("id", "name", "code", "data_scope", "description", "sort", "is_builtin", "create_user", "create_time")
VALUES
(1, '超级管理员', 'super_admin', 1, '系统初始角色', 0, true, 1, NOW()),
(2, '系统管理员', 'sys_admin', 1, NULL, 1, false, 1, NOW()),
(3, '普通用户', 'general', 4, NULL, 2, false, 1, NOW()),
(547888897925840927, '测试人员', 'tester', 5, NULL, 3, false, 1, NOW()),
(547888897925840928, '研发人员', 'developer', 4, NULL, 4, false, 1, NOW());

-- 初始化默认用户：admin/admin123；test/test123
INSERT INTO "sys_user"
("id", "username", "nickname", "password", "gender", "email", "phone", "avatar", "description", "status", "is_builtin", "pwd_update_time", "dept_id", "create_user", "create_time")
VALUES
(1, 'admin', '超级管理员', '$2a$10$kAfyANQ23MKgtwxr9aT.TOWPRW88aX4DXrJmX1W6GfGK463oBdmeG', 1, '42190c6c5639d2ca4edb4150a35e058559ccf8270361a23745a2fd285a273c28', '5bda89a4609a65546422ea56bfe5eab4', NULL, '系统初始用户', 1, true, NOW(), 1, 1, NOW()),
(801822, 'test', '测试员', '{bcrypt}$2a$10$xAsoeMJ.jc/kSxhviLAg7.j2iFrhi6yYAdniNdjLiIUWU/BRZl2Ti', 2, NULL, NULL, NULL,
 NULL, 1, FALSE, NOW(), 547887852587843593, 1, NOW()),
(801823, 'Charles', 'Charles', '{bcrypt}$2a$10$xAsoeMJ.jc/kSxhviLAg7.j2iFrhi6yYAdniNdjLiIUWU/BRZl2Ti', 1, NULL, NULL,
 NULL, '代码写到极致，就是艺术。', 1, FALSE, NOW(), 547887852587843595, 1, NOW()),
(801824, 'Yoofff', 'Yoofff', '{bcrypt}$2a$10$xAsoeMJ.jc/kSxhviLAg7.j2iFrhi6yYAdniNdjLiIUWU/BRZl2Ti', 1, NULL, NULL,
 NULL, '弱小和无知不是生存的障碍，傲慢才是。', 1, FALSE, NOW(), 1, 1, NOW()),
(801825, 'Jasmine', 'Jasmine', '{bcrypt}$2a$10$xAsoeMJ.jc/kSxhviLAg7.j2iFrhi6yYAdniNdjLiIUWU/BRZl2Ti', 1, NULL, NULL,
 NULL, '干就完事了！', 1, FALSE, NOW(), 547887852587843605, 1, NOW()),
(801826, 'AutumnSail', '秋登', '{bcrypt}$2a$10$xAsoeMJ.jc/kSxhviLAg7.j2iFrhi6yYAdniNdjLiIUWU/BRZl2Ti', 1, NULL, NULL,
 NULL, '只有追求完美，才能创造奇迹。', 1, FALSE, NOW(), 547887852587843602, 1, NOW()),
(801827, 'Kils', 'Kils', '{bcrypt}$2a$10$xAsoeMJ.jc/kSxhviLAg7.j2iFrhi6yYAdniNdjLiIUWU/BRZl2Ti', 1, NULL, NULL, NULL,
 '可以摆烂，但不能真的菜。', 1, FALSE, NOW(), 547887852587843599, 1, NOW()),
(801828, 'mochou', '莫愁', '{bcrypt}$2a$10$xAsoeMJ.jc/kSxhviLAg7.j2iFrhi6yYAdniNdjLiIUWU/BRZl2Ti', 1, NULL, NULL, NULL,
 '万事莫愁，皆得所愿。', 1, FALSE, NOW(), 547887852587843602, 1, NOW()),
(801829, 'Jing', 'MS-Jing', '{bcrypt}$2a$10$xAsoeMJ.jc/kSxhviLAg7.j2iFrhi6yYAdniNdjLiIUWU/BRZl2Ti', 1, NULL, NULL, NULL,
 '路虽远，行则将至。', 2, FALSE, NOW(), 547887852587843599, 1, NOW()),
(801830, 'domw', '梓陌', '{bcrypt}$2a$10$xAsoeMJ.jc/kSxhviLAg7.j2iFrhi6yYAdniNdjLiIUWU/BRZl2Ti', 1, NULL, NULL, NULL,
 '胜利是奖赏，挫折是常态。', 1, FALSE, NOW(), 547887852587843608, 1, NOW()),
(801831, 'xtanyu', '小熊', '{bcrypt}$2a$10$xAsoeMJ.jc/kSxhviLAg7.j2iFrhi6yYAdniNdjLiIUWU/BRZl2Ti', 1, NULL, NULL, NULL,
 '不想上班。', 1, FALSE, NOW(), 547887852587843611, 1, NOW()),
(801832, 'ppxb', '番茄', '{bcrypt}$2a$10$xAsoeMJ.jc/kSxhviLAg7.j2iFrhi6yYAdniNdjLiIUWU/BRZl2Ti', 1, NULL, NULL, NULL,
 'one day smile one day cry.', 1, FALSE, NOW(), 1, 547887852587843599, NOW()),
(801833, 'luoqiz', '老罗', '{bcrypt}$2a$10$xAsoeMJ.jc/kSxhviLAg7.j2iFrhi6yYAdniNdjLiIUWU/BRZl2Ti', 1, NULL, NULL, NULL,
 '行者无疆，丈量四方。', 1, FALSE, NOW(), 1, 1, NOW()),
(801834, 'lishuyanla', '颜如玉', '{bcrypt}$2a$10$xAsoeMJ.jc/kSxhviLAg7.j2iFrhi6yYAdniNdjLiIUWU/BRZl2Ti', 1, NULL, NULL,
 NULL, '书中自有颜如玉，世间多是李莫愁。', 1, FALSE, NOW(), 1, 1, NOW());


-- 初始化字典数据
INSERT INTO "sys_dict" ("dict_type", "value", "label", "ext", "sort", "enabled", "description")
VALUES
('notice_type', '1', '产品新闻', '{"color": "primary"}', 1, true, NULL),
('notice_type', '2', '企业动态', '{"color": "success"}', 2, true, NULL),
('client_type', 'PC', '桌面端', '{"color": "primary"}', 1, true, NULL),
('client_type', 'ANDROID', '安卓', '{"color": "success"}', 2, true, NULL),
('client_type', 'XCX', '小程序', '{"color": "warning"}', 3, true, NULL),
('sms_supplier', 'alibaba', '阿里云', '{"color": "warning"}', 1, true, NULL),
('sms_supplier', 'tencent', '腾讯云', '{"color": "primary"}', 2, true, NULL),
('sms_supplier', 'cloopen', '容联云', '{"color": "success"}', 3, true, NULL);

-- 初始化默认用户和角色关联数据
INSERT INTO "sys_user_role" ("id", "user_id", "role_id")
VALUES
(1, 1, 1),
(2, 801822, 547888897925840927),
(3, 801823, 547888897925840928),
(4, 801824, 547888897925840928),
(5, 801825, 547888897925840928),
(6, 801826, 547888897925840928),
(7, 801827, 547888897925840928),
(8, 801828, 547888897925840928),
(9, 801829, 547888897925840928),
(10, 801830, 547888897925840928),
(11, 801831, 547888897925840928),
(12, 801832, 547888897925840928),
(13, 801833, 547888897925840928),
(14, 801834, 547888897925840928);

-- 初始化默认角色和菜单关联数据
INSERT INTO "sys_role_menu" ("role_id", "menu_id")
VALUES
(547888897925840927, 1000),
(547888897925840927, 1010),
(547888897925840927, 1011),
(547888897925840927, 1012),
(547888897925840927, 1013),
(547888897925840927, 1014),
(547888897925840927, 1150),
(547888897925840927, 1151),
(547888897925840927, 1152),
(547888897925840928, 2000),
(547888897925840928, 2010),
(547888897925840928, 2011),
(547888897925840928, 2020),
(547888897925840928, 2021),
(547888897925840928, 2022),
(547888897925840928, 2023);

-- 初始化默认角色和部门关联数据
INSERT INTO "sys_role_dept" ("role_id", "dept_id") 
VALUES 
(547888897925840927, 547887852587843593);