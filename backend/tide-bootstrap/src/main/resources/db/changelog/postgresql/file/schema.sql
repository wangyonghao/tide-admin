-- liquibase formatted sql

-- changeset wyhao:file-1
-- comment 文件服务-初始化表结构
CREATE TABLE IF NOT EXISTS "file"
(
    "id"           BIGSERIAL PRIMARY KEY,
    "file_name"    VARCHAR(255) NOT NULL,
    "content_type" VARCHAR(100),
    "file_size"    BIGINT       NOT NULL,
    "sha256"       VARCHAR(64)  NOT NULL,
    "storage_type" INT2         NOT NULL DEFAULT 1,
    "storage_key"  VARCHAR(500) NOT NULL,
    "status"       INT2         NOT NULL DEFAULT 1,
    "create_user"  BIGINT       NOT NULL,
    "create_time"  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "update_user"  BIGINT,
    "update_time"  TIMESTAMP             DEFAULT CURRENT_TIMESTAMP
);

-- 创建索引
CREATE INDEX IF NOT EXISTS "idx_file_sha256" ON "file" ("sha256");
CREATE INDEX IF NOT EXISTS "idx_file_storage_key" ON "file" ("storage_key");
CREATE INDEX IF NOT EXISTS "idx_file_status" ON "file" ("status");
CREATE INDEX IF NOT EXISTS "idx_file_create_time" ON "file" ("create_time");

-- 添加注释
COMMENT ON TABLE "file" IS '文件表（物理文件元数据）';
COMMENT ON COLUMN "file"."id" IS '文件 ID';
COMMENT ON COLUMN "file"."file_name" IS '文件名（原始文件名）';
COMMENT ON COLUMN "file"."content_type" IS '内容类型（MIME Type）';
COMMENT ON COLUMN "file"."file_size" IS '文件大小（字节）';
COMMENT ON COLUMN "file"."sha256" IS 'SHA-256 哈希值（用于完整性校验和去重）';
COMMENT ON COLUMN "file"."storage_type" IS '存储类型（1-本地存储；2-OSS 对象存储）';
COMMENT ON COLUMN "file"."storage_key" IS '存储键（全局唯一，格式：[{key-prefix}/]{yyyy}/{MM}/{dd}/{uuid}[.ext]）';
COMMENT ON COLUMN "file"."status" IS '文件状态（1-正常；2-已删除；3-已清除）';
COMMENT ON COLUMN "file"."create_user" IS '创建人';
COMMENT ON COLUMN "file"."create_time" IS '创建时间';
COMMENT ON COLUMN "file"."update_user" IS '更新人';
COMMENT ON COLUMN "file"."update_time" IS '更新时间';
