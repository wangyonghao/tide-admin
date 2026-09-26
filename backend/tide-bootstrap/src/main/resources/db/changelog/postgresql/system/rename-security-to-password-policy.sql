-- liquibase formatted sql

-- changeset wyhao:rename-config-key-security-to-password-policy
-- comment 将密码策略配置键 security 重命名为 password-policy，并将会话超时迁入 login
UPDATE "sys_config" AS login
SET config_value = COALESCE(login.config_value, '{}'::jsonb) || jsonb_build_object(
        'sessionTimeout',
        COALESCE((security.config_value ->> 'sessionTimeout')::int, 30)
    )
FROM "sys_config" AS security
WHERE login.config_key = 'login'
  AND security.config_key = 'security'
  AND NOT (COALESCE(login.config_value, '{}'::jsonb) ? 'sessionTimeout');

UPDATE "sys_config"
SET config_key   = 'password-policy',
    description  = '密码策略配置',
    config_value = COALESCE(config_value, '{}'::jsonb) - 'sessionTimeout'
WHERE config_key = 'security';
