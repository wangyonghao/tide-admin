-- liquibase formatted sql

-- changeset wyhao:user-nickname-to-display-name
-- comment 已有库：sys_user.nickname → display_name；新库 schema 已是 display_name，跳过
DO
$$
    BEGIN
        IF EXISTS (SELECT 1
                   FROM information_schema.columns
                   WHERE table_schema = current_schema()
                     AND table_name = 'sys_user'
                     AND column_name = 'nickname')
            AND NOT EXISTS (SELECT 1
                            FROM information_schema.columns
                            WHERE table_schema = current_schema()
                              AND table_name = 'sys_user'
                              AND column_name = 'display_name') THEN
            ALTER TABLE "sys_user" RENAME COLUMN "nickname" TO "display_name";
        END IF;
    END
$$;
COMMENT ON COLUMN "sys_user"."display_name" IS '显示名称';
