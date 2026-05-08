-- liquibase formatted sql

-- changeset wyhao:login-log-1
-- comment 创建登录日志表
CREATE TABLE IF NOT EXISTS "sys_login_log"
(
    "id"             int8         NOT NULL,
    "username"       varchar(50)  NOT NULL,
    "ip_address"     varchar(50)  NOT NULL,
    "location"       varchar(200) NOT NULL,
    "device_type"    varchar(20)  NOT NULL,
    "browser"        varchar(100) NOT NULL,
    "os"             varchar(100) NOT NULL,
    "login_status"   varchar(20)  NOT NULL,
    "login_time"     timestamp    NOT NULL,
    "failure_reason" varchar(500)          DEFAULT NULL,
    "user_agent"     varchar(500)          DEFAULT NULL,
    "tenant_id"      int8                  DEFAULT NULL,
    PRIMARY KEY ("id")
);

-- 创建索引
CREATE INDEX "idx_login_log_username" ON "sys_login_log" ("username");
CREATE INDEX "idx_login_log_ip_address" ON "sys_login_log" ("ip_address");
CREATE INDEX "idx_login_log_login_status" ON "sys_login_log" ("login_status");
CREATE INDEX "idx_login_log_login_time" ON "sys_login_log" ("login_time");
CREATE INDEX "idx_login_log_tenant_id" ON "sys_login_log" ("tenant_id");

-- 添加字段注释
COMMENT ON COLUMN "sys_login_log"."id" IS '主键ID';
COMMENT ON COLUMN "sys_login_log"."username" IS '用户名';
COMMENT ON COLUMN "sys_login_log"."ip_address" IS 'IP地址';
COMMENT ON COLUMN "sys_login_log"."location" IS '地理位置';
COMMENT ON COLUMN "sys_login_log"."device_type" IS '设备类型（WEB：浏览器端，MOBILE：移动端，WECHAT_MINI_PROGRAM：微信小程序）';
COMMENT ON COLUMN "sys_login_log"."browser" IS '浏览器';
COMMENT ON COLUMN "sys_login_log"."os" IS '操作系统';
COMMENT ON COLUMN "sys_login_log"."login_status" IS '登录状态（SUCCESS：成功，FAILURE：失败）';
COMMENT ON COLUMN "sys_login_log"."login_time" IS '登录时间';
COMMENT ON COLUMN "sys_login_log"."failure_reason" IS '失败原因';
COMMENT ON COLUMN "sys_login_log"."user_agent" IS 'User-Agent';
COMMENT ON COLUMN "sys_login_log"."tenant_id" IS '租户ID';
COMMENT ON TABLE "sys_login_log" IS '登录日志表';
