-- liquibase formatted sql

-- changeset wyhao:1
-- comment system-初始化表结构
CREATE TABLE IF NOT EXISTS "sys_menu"
(
    "id"          int8        NOT NULL,
    "parent_id"   int8        NOT NULL DEFAULT 0,
    "type"        int2        NOT NULL DEFAULT 1,
    "path"        varchar(255)         DEFAULT NULL,
    "name"        varchar(50)          DEFAULT NULL,
    "component"   varchar(255)         DEFAULT NULL,
    "redirect"    varchar(255)         DEFAULT NULL,
    "icon"        varchar(50)          DEFAULT NULL,
    "is_external" bool                 DEFAULT false,
    "is_cache"    bool                 DEFAULT false,
    "is_hidden"   bool                 DEFAULT false,
    "permission"  varchar(100)         DEFAULT NULL,
    "sort"        int4        NOT NULL DEFAULT 999,
    "status"      int2        NOT NULL DEFAULT 1,
    "create_user" int8        NOT NULL,
    "create_time" timestamp   NOT NULL,
    "update_user" int8                 DEFAULT NULL,
    "update_time" timestamp            DEFAULT NULL,
    PRIMARY KEY ("id")
);
CREATE INDEX "idx_menu_parent_id" ON "sys_menu" ("parent_id");
CREATE UNIQUE INDEX "uk_menu_name_parent_id" ON "sys_menu" ("name", "parent_id");
COMMENT ON COLUMN "sys_menu"."id" IS 'ID';
COMMENT ON COLUMN "sys_menu"."name" IS '名称';
COMMENT ON COLUMN "sys_menu"."parent_id" IS '上级菜单ID';
COMMENT ON COLUMN "sys_menu"."type" IS '类型（1：目录；2：菜单；3：按钮）';
COMMENT ON COLUMN "sys_menu"."path" IS '路由地址';
COMMENT ON COLUMN "sys_menu"."component" IS '组件路径';
COMMENT ON COLUMN "sys_menu"."redirect" IS '重定向地址';
COMMENT ON COLUMN "sys_menu"."icon" IS '图标';
COMMENT ON COLUMN "sys_menu"."is_external" IS '是否外链';
COMMENT ON COLUMN "sys_menu"."is_cache" IS '是否缓存';
COMMENT ON COLUMN "sys_menu"."is_hidden" IS '是否隐藏';
COMMENT ON COLUMN "sys_menu"."permission" IS '权限标识';
COMMENT ON COLUMN "sys_menu"."sort" IS '排序';
COMMENT ON COLUMN "sys_menu"."status" IS '状态（1：启用；2：禁用）';
COMMENT ON COLUMN "sys_menu"."create_user" IS '创建人';
COMMENT ON COLUMN "sys_menu"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_menu"."update_user" IS '修改人';
COMMENT ON COLUMN "sys_menu"."update_time" IS '修改时间';
COMMENT ON TABLE "sys_menu" IS '菜单表';

CREATE TABLE IF NOT EXISTS "sys_dept"
(
    "id"          int8         NOT NULL,
    "name"        varchar(30)  NOT NULL,
    "parent_id"   int8         NOT NULL DEFAULT 0,
    "ancestors"   varchar(512) NOT NULL DEFAULT '',
    "description" varchar(200)          DEFAULT NULL,
    "sort"        int4         NOT NULL DEFAULT 999,
    "status"      int2         NOT NULL DEFAULT 1,
    "is_builtin"   bool        NOT NULL DEFAULT false,
    "create_user" int8         NOT NULL,
    "create_time" timestamp    NOT NULL,
    "update_user" int8                  DEFAULT NULL,
    "update_time" timestamp             DEFAULT NULL,
    PRIMARY KEY ("id")
);
CREATE INDEX "idx_dept_parent_id" ON "sys_dept" ("parent_id");
CREATE UNIQUE INDEX "uk_dept_name_parent_id" ON "sys_dept" ("name", "parent_id");
COMMENT ON COLUMN "sys_dept"."id" IS 'ID';
COMMENT ON COLUMN "sys_dept"."name" IS '名称';
COMMENT ON COLUMN "sys_dept"."parent_id" IS '上级部门ID';
COMMENT ON COLUMN "sys_dept"."ancestors" IS '祖级列表';
COMMENT ON COLUMN "sys_dept"."description" IS '描述';
COMMENT ON COLUMN "sys_dept"."sort" IS '排序';
COMMENT ON COLUMN "sys_dept"."status" IS '状态（1：启用；2：禁用）';
COMMENT ON COLUMN "sys_dept"."is_builtin" IS '是否为系统内置数据';
COMMENT ON COLUMN "sys_dept"."create_user" IS '创建人';
COMMENT ON COLUMN "sys_dept"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_dept"."update_user" IS '修改人';
COMMENT ON COLUMN "sys_dept"."update_time" IS '修改时间';
COMMENT ON TABLE "sys_dept" IS '部门表';

CREATE TABLE IF NOT EXISTS "sys_role"
(
    "id"                  int8        NOT NULL,
    "name"                varchar(30) NOT NULL,
    "code"                varchar(30) NOT NULL,
    "data_scope"          int2        NOT NULL DEFAULT 4,
    "description"         varchar(200)         DEFAULT NULL,
    "sort"                int4        NOT NULL DEFAULT 999,
    "is_builtin"           bool        NOT NULL DEFAULT false,
    "menu_check_strictly" bool                 DEFAULT true,
    "dept_check_strictly" bool                 DEFAULT true,
    "create_user"         int8        NOT NULL,
    "create_time"         timestamp   NOT NULL,
    "update_user"         int8                 DEFAULT NULL,
    "update_time"         timestamp            DEFAULT NULL,
    PRIMARY KEY ("id")
);
CREATE UNIQUE INDEX "uk_role_name" ON "sys_role" ("name");
CREATE UNIQUE INDEX "uk_role_code" ON "sys_role" ("code");
COMMENT ON COLUMN "sys_role"."id" IS 'ID';
COMMENT ON COLUMN "sys_role"."name" IS '名称';
COMMENT ON COLUMN "sys_role"."id" IS 'ID';
COMMENT ON COLUMN "sys_role"."name" IS '名称';
COMMENT ON COLUMN "sys_role"."code" IS '编码';
COMMENT ON COLUMN "sys_role"."data_scope" IS '数据权限（1：全部数据权限；2：本部门及以下数据权限；3：本部门数据权限；4：仅本人数据权限；5：自定义数据权限）';
COMMENT ON COLUMN "sys_role"."description" IS '描述';
COMMENT ON COLUMN "sys_role"."sort" IS '排序';
COMMENT ON COLUMN "sys_role"."is_builtin" IS '是否为系统内置数据';
COMMENT ON COLUMN "sys_role"."menu_check_strictly" IS '菜单选择是否父子节点关联';
COMMENT ON COLUMN "sys_role"."dept_check_strictly" IS '部门选择是否父子节点关联';
COMMENT ON COLUMN "sys_role"."create_user" IS '创建人';
COMMENT ON COLUMN "sys_role"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_role"."update_user" IS '修改人';
COMMENT ON COLUMN "sys_role"."update_time" IS '修改时间';
COMMENT ON TABLE "sys_role" IS '角色表';

CREATE TABLE IF NOT EXISTS "sys_user"
(
    "id"             int8        NOT NULL,
    "username"       varchar(64) NOT NULL,
    "nickname"       varchar(30) NOT NULL,
    "password"       varchar(255)         DEFAULT NULL,
    "gender"         int2        NOT NULL DEFAULT 0,
    "email"          varchar(255)         DEFAULT NULL,
    "phone"          varchar(255)         DEFAULT NULL,
    "avatar"         text                 DEFAULT NULL,
    "description"    varchar(200)         DEFAULT NULL,
    "status"         int2        NOT NULL DEFAULT 1,
    "is_builtin"      bool        NOT NULL DEFAULT false,
    "pwd_update_time" timestamp           DEFAULT NULL,
    "pwd_expire_date" date                DEFAULT NULL,
    "dept_id"        int8        NOT NULL,
    "create_user"    int8                 DEFAULT NULL,
    "create_time"    timestamp   NOT NULL,
    "update_user"    int8                 DEFAULT NULL,
    "update_time"    timestamp            DEFAULT NULL,
    PRIMARY KEY ("id")
);
CREATE UNIQUE INDEX "uk_user_username" ON "sys_user" ("username");
CREATE UNIQUE INDEX "uk_user_email" ON "sys_user" ("email");
CREATE UNIQUE INDEX "uk_user_phone" ON "sys_user" ("phone");
CREATE INDEX "idx_user_dept_id" ON "sys_user" ("dept_id");
CREATE INDEX "idx_user_create_user" ON "sys_user" ("create_user");
CREATE INDEX "idx_user_update_user" ON "sys_user" ("update_user");
COMMENT ON COLUMN "sys_user"."id" IS 'ID';
COMMENT ON COLUMN "sys_user"."username" IS '用户名';
COMMENT ON COLUMN "sys_user"."nickname" IS '昵称';
COMMENT ON COLUMN "sys_user"."password" IS '密码';
COMMENT ON COLUMN "sys_user"."gender" IS '性别（0：未知；1：男；2：女）';
COMMENT ON COLUMN "sys_user"."email" IS '邮箱';
COMMENT ON COLUMN "sys_user"."phone" IS '手机号码';
COMMENT ON COLUMN "sys_user"."avatar" IS '头像';
COMMENT ON COLUMN "sys_user"."description" IS '描述';
COMMENT ON COLUMN "sys_user"."status" IS '状态（1：启用；2：禁用）';
COMMENT ON COLUMN "sys_user"."is_builtin" IS '是否为系统内置数据';
COMMENT ON COLUMN "sys_user"."pwd_update_time" IS '上次改密时间';
COMMENT ON COLUMN "sys_user"."pwd_expire_date" IS '密码过期日';
COMMENT ON COLUMN "sys_user"."dept_id" IS '部门ID';
COMMENT ON COLUMN "sys_user"."create_user" IS '创建人';
COMMENT ON COLUMN "sys_user"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_user"."update_user" IS '修改人';
COMMENT ON COLUMN "sys_user"."update_time" IS '修改时间';
COMMENT ON TABLE "sys_user" IS '用户表';

CREATE TABLE IF NOT EXISTS "sys_user_password_history"
(
    "id"          int8         NOT NULL,
    "user_id"     int8         NOT NULL,
    "password"    varchar(255) NOT NULL,
    "create_time" timestamp    NOT NULL,
    PRIMARY KEY ("id")
);
CREATE INDEX "idx_uph_user_id" ON "sys_user_password_history" ("user_id");
COMMENT ON COLUMN "sys_user_password_history"."id" IS 'ID';
COMMENT ON COLUMN "sys_user_password_history"."user_id" IS '用户ID';
COMMENT ON COLUMN "sys_user_password_history"."password" IS '密码';
COMMENT ON COLUMN "sys_user_password_history"."create_time" IS '创建时间';
COMMENT ON TABLE "sys_user_password_history" IS '用户历史密码表';

CREATE TABLE IF NOT EXISTS "sys_user_social"
(
    "id"              int8         NOT NULL,
    "source"          varchar(255) NOT NULL,
    "open_id"         varchar(255) NOT NULL,
    "user_id"         int8         NOT NULL,
    "meta_json"       text      DEFAULT NULL,
    "last_login_time" timestamp DEFAULT NULL,
    "create_time"     timestamp    NOT NULL,
    PRIMARY KEY ("id")
);
CREATE UNIQUE INDEX "uk_user_source_open_id" ON "sys_user_social" ("source", "open_id");
COMMENT ON COLUMN "sys_user_social"."id" IS 'ID';
COMMENT ON COLUMN "sys_user_social"."source" IS '来源';
COMMENT ON COLUMN "sys_user_social"."open_id" IS '开放ID';
COMMENT ON COLUMN "sys_user_social"."user_id" IS '用户ID';
COMMENT ON COLUMN "sys_user_social"."meta_json" IS '附加信息';
COMMENT ON COLUMN "sys_user_social"."last_login_time" IS '最后登录时间';
COMMENT ON COLUMN "sys_user_social"."create_time" IS '创建时间';
COMMENT ON TABLE "sys_user_social" IS '用户社会化关联表';

CREATE TABLE IF NOT EXISTS "sys_user_role"
(
    "id"      int8 NOT NULL,
    "user_id" int8 NOT NULL,
    "role_id" int8 NOT NULL,
    PRIMARY KEY ("id")
);
CREATE UNIQUE INDEX "uk_user_id_role_id" ON "sys_user_role" ("user_id", "role_id");
COMMENT ON COLUMN "sys_user_role"."id" IS 'ID';
COMMENT ON COLUMN "sys_user_role"."user_id" IS '用户ID';
COMMENT ON COLUMN "sys_user_role"."role_id" IS '角色ID';
COMMENT ON TABLE "sys_user_role" IS '用户和角色关联表';

CREATE TABLE IF NOT EXISTS "sys_role_menu"
(
    "role_id" int8 NOT NULL,
    "menu_id" int8 NOT NULL,
    PRIMARY KEY ("role_id", "menu_id")
);
COMMENT ON COLUMN "sys_role_menu"."role_id" IS '角色ID';
COMMENT ON COLUMN "sys_role_menu"."menu_id" IS '菜单ID';
COMMENT ON TABLE "sys_role_menu" IS '角色和菜单关联表';

CREATE TABLE IF NOT EXISTS "sys_role_dept"
(
    "role_id" int8 NOT NULL,
    "dept_id" int8 NOT NULL,
    PRIMARY KEY ("role_id", "dept_id")
);
COMMENT ON COLUMN "sys_role_dept"."role_id" IS '角色ID';
COMMENT ON COLUMN "sys_role_dept"."dept_id" IS '部门ID';
COMMENT ON TABLE "sys_role_dept" IS '角色和部门关联表';

-- 删除旧的字典表
DROP TABLE IF EXISTS "sys_dict";

-- 创建新的字典表
CREATE TABLE IF NOT EXISTS "sys_dict"
(
    "id"          BIGSERIAL PRIMARY KEY,
    "dict_type"   varchar(100) NOT NULL,
    "value"       varchar(255) NOT NULL,
    "label"       varchar(255) NOT NULL,
    "ext"         jsonb        DEFAULT NULL,
    "sort"        int4         DEFAULT 0,
    "enabled"     bool         DEFAULT true,
    "description" varchar(500) DEFAULT NULL,
    "create_time" timestamp    DEFAULT CURRENT_TIMESTAMP,
    "create_user" int8,
    "update_time" timestamp    DEFAULT CURRENT_TIMESTAMP,
    "update_user" int8,
    CONSTRAINT "uk_dict_type_value" UNIQUE ("dict_type", "value")
);
-- 索引
CREATE INDEX "idx_dict_type_sort" ON "sys_dict" ("dict_type", "sort");
CREATE INDEX "idx_dict_ext_jsonb" ON "sys_dict" USING gin ("ext");

COMMENT ON COLUMN "sys_dict"."id" IS 'ID';
COMMENT ON COLUMN "sys_dict"."dict_type" IS '字典类型';
COMMENT ON COLUMN "sys_dict"."value" IS '字典值';
COMMENT ON COLUMN "sys_dict"."label" IS '字典标签';
COMMENT ON COLUMN "sys_dict"."ext" IS '扩展信息(JSON)';
COMMENT ON COLUMN "sys_dict"."sort" IS '排序';
COMMENT ON COLUMN "sys_dict"."enabled" IS '是否启用';
COMMENT ON COLUMN "sys_dict"."description" IS '描述';
COMMENT ON COLUMN "sys_dict"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_dict"."update_time" IS '更新时间';
COMMENT ON COLUMN "sys_dict"."create_user" IS '创建人';
COMMENT ON COLUMN "sys_dict"."update_user" IS '更新人';
COMMENT ON TABLE "sys_dict" IS '字典表';

DROP TABLE IF EXISTS sys_operation_log;
CREATE TABLE sys_operation_log
(
    id            BIGSERIAL PRIMARY KEY NOT NULL,
    object_type   VARCHAR(50)   DEFAULT NULL,
    object_id     BIGINT        DEFAULT NULL,
    operation     VARCHAR(50)   DEFAULT NULL,
    operator_id   BIGINT        DEFAULT NULL,
    operator_name VARCHAR(50)   DEFAULT NULL,
    operator_ip   VARCHAR(50)   DEFAULT NULL,
    operate_time  TIMESTAMP     DEFAULT NULL,
    status        VARCHAR(20)   DEFAULT NULL,
    remark        VARCHAR(1000) DEFAULT NULL,
    extra         TEXT          DEFAULT NULL
);
CREATE INDEX idx_sys_operation_log_object ON sys_operation_log (object_type, object_id);
CREATE INDEX idx_sys_operation_log_operator ON sys_operation_log (operator_id);
CREATE INDEX idx_sys_operation_log_operate_time ON sys_operation_log (operate_time);

COMMENT ON TABLE sys_operation_log IS '系统操作日志表';
COMMENT ON COLUMN sys_operation_log.object_type IS '业务对象类型';
COMMENT ON COLUMN sys_operation_log.object_id IS '业务对象ID';
COMMENT ON COLUMN sys_operation_log.operation IS '操作类型';
COMMENT ON COLUMN sys_operation_log.operator_id IS '操作者ID';
COMMENT ON COLUMN sys_operation_log.operator_name IS '操作者名称';
COMMENT ON COLUMN sys_operation_log.operator_ip IS '操作者IP';
COMMENT ON COLUMN sys_operation_log.operate_time IS '操作时间';
COMMENT ON COLUMN sys_operation_log.status IS '状态';
COMMENT ON COLUMN sys_operation_log.remark IS '备注';
COMMENT ON COLUMN sys_operation_log.extra IS '额外JSON格式数据';


CREATE TABLE IF NOT EXISTS "sys_message"
(
    "id"          int8        NOT NULL,
    "title"       varchar(50) NOT NULL,
    "content"     text                 DEFAULT NULL,
    "type"        int2        NOT NULL DEFAULT 1,
    "path"        varchar(255)         DEFAULT NULL,
    "scope"       int2        NOT NULL DEFAULT 1,
    "users"       json                 DEFAULT NULL,
    "create_time" timestamp   NOT NULL,
    PRIMARY KEY ("id")
);
COMMENT ON COLUMN "sys_message"."id" IS 'ID';
COMMENT ON COLUMN "sys_message"."title" IS '标题';
COMMENT ON COLUMN "sys_message"."content" IS '内容';
COMMENT ON COLUMN "sys_message"."type" IS '类型（1：系统消息；2：安全消息）';
COMMENT ON COLUMN "sys_message"."path" IS '跳转路径';
COMMENT ON COLUMN "sys_message"."scope" IS '通知范围（1：所有人；2：指定用户）';
COMMENT ON COLUMN "sys_message"."users" IS '通知用户';
COMMENT ON COLUMN "sys_message"."create_time" IS '创建时间';
COMMENT ON TABLE "sys_message" IS '消息表';

CREATE TABLE IF NOT EXISTS "sys_message_log"
(
    "message_id" int8 NOT NULL,
    "user_id"    int8 NOT NULL,
    "read_time"  timestamp DEFAULT NULL,
    PRIMARY KEY ("message_id", "user_id")
);
COMMENT ON COLUMN "sys_message_log"."message_id" IS '消息ID';
COMMENT ON COLUMN "sys_message_log"."user_id" IS '用户ID';
COMMENT ON COLUMN "sys_message_log"."read_time" IS '读取时间';
COMMENT ON TABLE "sys_message_log" IS '消息日志表';

CREATE TABLE IF NOT EXISTS "sys_notice"
(
    "id"             int8         NOT NULL,
    "title"          varchar(150) NOT NULL,
    "content"        text         NOT NULL,
    "type"           varchar(30)  NOT NULL,
    "notice_scope"   int2         NOT NULL DEFAULT 1,
    "notice_users"   json                  DEFAULT NULL,
    "notice_methods" json                  DEFAULT NULL,
    "is_timing"      bool         NOT NULL DEFAULT false,
    "publish_time"   timestamp             DEFAULT NULL,
    "is_top"         bool         NOT NULL DEFAULT false,
    "status"         int2         NOT NULL DEFAULT 1,
    "create_user"    int8         NOT NULL,
    "create_time"    timestamp    NOT NULL,
    "update_user"    int8                  DEFAULT NULL,
    "update_time"    timestamp             DEFAULT NULL,
    PRIMARY KEY ("id")
);
CREATE INDEX "idx_notice_create_user" ON "sys_notice" ("create_user");
CREATE INDEX "idx_notice_update_user" ON "sys_notice" ("update_user");
COMMENT ON COLUMN "sys_notice"."id" IS 'ID';
COMMENT ON COLUMN "sys_notice"."title" IS '标题';
COMMENT ON COLUMN "sys_notice"."content" IS '内容';
COMMENT ON COLUMN "sys_notice"."type" IS '分类';
COMMENT ON COLUMN "sys_notice"."notice_scope" IS '通知范围（1：所有人；2：指定用户）';
COMMENT ON COLUMN "sys_notice"."notice_users" IS '通知用户';
COMMENT ON COLUMN "sys_notice"."notice_methods" IS '通知方式（1：系统消息；2：登录弹窗）';
COMMENT ON COLUMN "sys_notice"."is_timing" IS '是否定时';
COMMENT ON COLUMN "sys_notice"."publish_time" IS '发布时间';
COMMENT ON COLUMN "sys_notice"."is_top" IS '是否置顶';
COMMENT ON COLUMN "sys_notice"."status" IS '状态（1：草稿；2：待发布；3：已发布）';
COMMENT ON COLUMN "sys_notice"."create_user" IS '创建人';
COMMENT ON COLUMN "sys_notice"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_notice"."update_user" IS '修改人';
COMMENT ON COLUMN "sys_notice"."update_time" IS '修改时间';
COMMENT ON TABLE "sys_notice" IS '公告表';

CREATE TABLE IF NOT EXISTS "sys_notice_log"
(
    "notice_id" int8 NOT NULL,
    "user_id"   int8 NOT NULL,
    "read_time" timestamp DEFAULT NULL,
    PRIMARY KEY ("notice_id", "user_id")
);
COMMENT ON COLUMN "sys_notice_log"."notice_id" IS '消息ID';
COMMENT ON COLUMN "sys_notice_log"."user_id" IS '用户ID';
COMMENT ON COLUMN "sys_notice_log"."read_time" IS '读取时间';
COMMENT ON TABLE "sys_notice_log" IS '公告日志表';

CREATE TABLE IF NOT EXISTS "sys_file"
(
    "id"              SERIAL8      NOT NULL,
    "file_name"       varchar(255) NOT NULL,
    "file_type"       varchar(255)          DEFAULT NULL,
    "file_size"       int8                  DEFAULT NULL,
    "file_extension"  varchar(100)          DEFAULT NULL,
    "oss_file_name"   varchar(255) NOT NULL,
    "oss_platform"    varchar(50)  NOT NULL,
    "oss_path"        varchar(512) NOT NULL,
    "oss_url"         varchar(1024)         DEFAULT NULL,
    "biz_id"          int8                  DEFAULT NULL,
    "biz_type"        varchar(50)           DEFAULT NULL,
    "status"          varchar(20)  NOT NULL DEFAULT 'AVAILABLE',
    "create_user"     int8         NOT NULL,
    "create_time"     timestamp    NOT NULL,
    "update_user"     int8                  DEFAULT NULL,
    "update_time"     timestamp             DEFAULT NULL,
    PRIMARY KEY ("id")
);
CREATE INDEX "idx_file_oss_platform" ON "sys_file" ("oss_platform");
CREATE INDEX "idx_file_biz" ON "sys_file" ("biz_id", "biz_type");
CREATE INDEX "idx_file_status" ON "sys_file" ("status");
CREATE INDEX "idx_file_create_user" ON "sys_file" ("create_user");
COMMENT ON COLUMN "sys_file"."id" IS 'ID';
COMMENT ON COLUMN "sys_file"."file_name" IS '原始文件名';
COMMENT ON COLUMN "sys_file"."file_type" IS '文件类型（MIME类型）';
COMMENT ON COLUMN "sys_file"."file_size" IS '文件大小（字节）';
COMMENT ON COLUMN "sys_file"."file_extension" IS '文件后缀';
COMMENT ON COLUMN "sys_file"."oss_file_name" IS 'OSS文件名';
COMMENT ON COLUMN "sys_file"."oss_platform" IS '存储平台（local、minio、aliyun）';
COMMENT ON COLUMN "sys_file"."oss_path" IS 'OSS文件路径';
COMMENT ON COLUMN "sys_file"."oss_url" IS 'OSS文件URL';
COMMENT ON COLUMN "sys_file"."biz_id" IS '关联业务单号';
COMMENT ON COLUMN "sys_file"."biz_type" IS '关联业务类型（avatar、attachment、contract等）';
COMMENT ON COLUMN "sys_file"."status" IS '文件状态（UPLOADING:上传中、AVAILABLE:可用、INFECTED:病毒感染、DELETED:已删除）';
COMMENT ON COLUMN "sys_file"."create_user" IS '创建人';
COMMENT ON COLUMN "sys_file"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_file"."update_user" IS '修改人';
COMMENT ON COLUMN "sys_file"."update_time" IS '修改时间';
COMMENT ON TABLE "sys_file" IS '文件表';


CREATE TABLE IF NOT EXISTS "sys_sms_config"
(
    "id"              int8         NOT NULL,
    "name"            varchar(100) NOT NULL,
    "supplier"        varchar(50)  NOT NULL,
    "access_key"      varchar(255) NOT NULL,
    "secret_key"      varchar(255) NOT NULL,
    "signature"       varchar(100)          DEFAULT NULL,
    "template_id"     varchar(50)           DEFAULT NULL,
    "weight"          int4                  DEFAULT NULL,
    "retry_interval"  int4                  DEFAULT NULL,
    "max_retries"     int4                  DEFAULT NULL,
    "maximum"         int4                  DEFAULT NULL,
    "supplier_config" text                  DEFAULT NULL,
    "is_default"      bool         NOT NULL DEFAULT false,
    "status"          int2         NOT NULL DEFAULT 1,
    "create_user"     int8         NOT NULL,
    "create_time"     timestamp    NOT NULL,
    "update_user"     int8                  DEFAULT NULL,
    "update_time"     timestamp             DEFAULT NULL,
    PRIMARY KEY ("id")
);
CREATE INDEX "idx_sms_config_create_user" ON "sys_sms_config" ("create_user");
CREATE INDEX "idx_sms_config_update_user" ON "sys_sms_config" ("update_user");
COMMENT ON COLUMN "sys_sms_config"."id" IS 'ID';
COMMENT ON COLUMN "sys_sms_config"."name" IS '名称';
COMMENT ON COLUMN "sys_sms_config"."supplier" IS '厂商';
COMMENT ON COLUMN "sys_sms_config"."access_key" IS 'Access Key';
COMMENT ON COLUMN "sys_sms_config"."secret_key" IS 'Secret Key';
COMMENT ON COLUMN "sys_sms_config"."signature" IS '短信签名';
COMMENT ON COLUMN "sys_sms_config"."template_id" IS '模板ID';
COMMENT ON COLUMN "sys_sms_config"."weight" IS '负载均衡权重';
COMMENT ON COLUMN "sys_sms_config"."retry_interval" IS '重试间隔（单位：秒）';
COMMENT ON COLUMN "sys_sms_config"."max_retries" IS '重试次数';
COMMENT ON COLUMN "sys_sms_config"."maximum" IS '发送上限';
COMMENT ON COLUMN "sys_sms_config"."supplier_config" IS '各个厂商独立配置';
COMMENT ON COLUMN "sys_sms_config"."is_default" IS '是否为默认配置';
COMMENT ON COLUMN "sys_sms_config"."status" IS '状态（1：启用；2：禁用）';
COMMENT ON COLUMN "sys_sms_config"."create_user" IS '创建人';
COMMENT ON COLUMN "sys_sms_config"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_sms_config"."update_user" IS '修改人';
COMMENT ON COLUMN "sys_sms_config"."update_time" IS '修改时间';
COMMENT ON TABLE "sys_sms_config" IS '短信配置表';

CREATE TABLE IF NOT EXISTS "sys_sms_log"
(
    "id"          int8        NOT NULL,
    "config_id"   int8        NOT NULL,
    "phone"       varchar(25) NOT NULL,
    "params"      text                 DEFAULT NULL,
    "status"      int2        NOT NULL DEFAULT 1,
    "res_msg"     text                 DEFAULT NULL,
    "create_user" int8        NOT NULL,
    "create_time" timestamp   NOT NULL,
    PRIMARY KEY ("id")
);
CREATE INDEX "idx_sms_log_config_id" ON "sys_sms_log" ("config_id");
CREATE INDEX "idx_sms_log_create_user" ON "sys_sms_log" ("create_user");
COMMENT ON COLUMN "sys_sms_log"."id" IS 'ID';
COMMENT ON COLUMN "sys_sms_log"."config_id" IS '配置ID';
COMMENT ON COLUMN "sys_sms_log"."phone" IS '手机号';
COMMENT ON COLUMN "sys_sms_log"."params" IS '参数配置';
COMMENT ON COLUMN "sys_sms_log"."status" IS '发送状态（1：成功；2：失败）';
COMMENT ON COLUMN "sys_sms_log"."res_msg" IS '返回数据';
COMMENT ON COLUMN "sys_sms_log"."create_user" IS '创建人';
COMMENT ON COLUMN "sys_sms_log"."create_time" IS '创建时间';
COMMENT ON TABLE "sys_sms_log" IS '短信日志表';