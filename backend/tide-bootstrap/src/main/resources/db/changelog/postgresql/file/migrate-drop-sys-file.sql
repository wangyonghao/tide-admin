-- liquibase formatted sql

-- changeset wyhao:file-migrate-1
-- comment 废弃 sys_file，用户头像改为存 fileId
DROP TABLE IF EXISTS "sys_file";

ALTER TABLE "sys_user" ALTER COLUMN "avatar" DROP DEFAULT;
ALTER TABLE "sys_user" ALTER COLUMN "avatar" TYPE int8 USING NULL;
COMMENT ON COLUMN "sys_user"."avatar" IS '头像文件 ID（关联 file.id）';
