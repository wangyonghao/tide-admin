-- liquibase formatted sql

-- changeset wyhao:1
-- comment openapi-初始化能力开放插件数据表
-- 初始化表结构
CREATE TABLE IF NOT EXISTS "sys_app" (
    "id"          int8         NOT NULL,
    "name"        varchar(100) NOT NULL,
    "access_key"  varchar(255) NOT NULL,
    "secret_key"  varchar(255) NOT NULL,
    "expire_time" timestamp    DEFAULT NULL,
    "description" varchar(200) DEFAULT NULL,
    "status"      int2         NOT NULL DEFAULT 1,
    "create_user" int8         NOT NULL,
    "create_time" timestamp    NOT NULL,
    "update_user" int8         DEFAULT NULL,
    "update_time" timestamp    DEFAULT NULL,
    PRIMARY KEY ("id")
);
CREATE UNIQUE INDEX "uk_app_access_key" ON "sys_app" ("access_key");
CREATE INDEX "idx_app_create_user" ON "sys_app" ("create_user");
CREATE INDEX "idx_app_update_user" ON "sys_app" ("update_user");
COMMENT ON COLUMN "sys_app"."id"              IS 'ID';
COMMENT ON COLUMN "sys_app"."name"            IS '名称';
COMMENT ON COLUMN "sys_app"."access_key"      IS 'Access Key（访问密钥）';
COMMENT ON COLUMN "sys_app"."secret_key"      IS 'Secret Key（私有密钥）';
COMMENT ON COLUMN "sys_app"."expire_time"     IS '失效时间';
COMMENT ON COLUMN "sys_app"."description"     IS '描述';
COMMENT ON COLUMN "sys_app"."status"          IS '状态（1：启用；2：禁用）';
COMMENT ON COLUMN "sys_app"."create_user"     IS '创建人';
COMMENT ON COLUMN "sys_app"."create_time"     IS '创建时间';
COMMENT ON COLUMN "sys_app"."update_user"     IS '修改人';
COMMENT ON COLUMN "sys_app"."update_time"     IS '修改时间';
COMMENT ON TABLE  "sys_app"                   IS '应用表';
