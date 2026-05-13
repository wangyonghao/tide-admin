<script setup lang="ts">
import type {
  EmailConfig,
  LoginConfig,
  RegisterConfig,
  SecurityConfig,
  SiteConfig,
  SmsConfig,
  StorageConfig,
} from '#/api/system/config';

import { onMounted, ref } from 'vue';

import { IconifyIcon } from '@vben/icons';
import { Page } from '@vben/common-ui';

import {
  NButton,
  NCard,
  NCheckbox,
  NForm,
  NFormItem,
  NInput,
  NInputNumber,
  NSelect,
  NSpace,
  NSplit,
  useMessage,
} from 'naive-ui';

import { configApi } from '#/api/system';

const message = useMessage();

// ==================== 配置类型定义 ====================
interface ConfigItem {
  key: string;
  label: string;
  icon: string;
  permission?: string;
}

const configList: ConfigItem[] = [
  { key: 'site', label: '站点配置', icon: 'lucide:globe' },
  { key: 'login', label: '登录配置', icon: 'lucide:log-in' },
  { key: 'register', label: '注册配置', icon: 'lucide:user-plus' },
  {
    key: 'email',
    label: '邮件配置',
    icon: 'lucide:mail',
    permission: 'system:config:edit',
  },
  {
    key: 'sms',
    label: '短信配置',
    icon: 'lucide:message-square',
    permission: 'system:config:edit',
  },
  {
    key: 'storage',
    label: '存储配置',
    icon: 'lucide:hard-drive',
    permission: 'system:config:edit',
  },
  { key: 'security', label: '安全配置', icon: 'lucide:shield' },
];

// ==================== 当前选中的配置 ====================
const selectedConfigKey = ref<string>('site');
const loading = ref(false);
const saving = ref(false);

// ==================== 各配置表单数据 ====================
const siteForm = ref<SiteConfig>({
  siteName: '',
  siteLogo: '',
  siteCopyright: '',
  siteIcp: '',
});

const loginForm = ref<LoginConfig>({
  captchaEnabled: true,
  captchaType: 'graphic',
  maxRetry: 5,
  lockTime: 30,
});

const registerForm = ref<RegisterConfig>({
  enabled: true,
  verifyEmail: false,
  verifyPhone: false,
  defaultRoleId: '',
});

const emailForm = ref<EmailConfig>({
  host: '',
  port: 465,
  username: '',
  password: '',
  fromName: '',
});

const smsForm = ref<SmsConfig>({
  provider: 'aliyun',
  accessKey: '',
  secretKey: '',
  signName: '',
});

const storageForm = ref<StorageConfig>({
  type: 'local',
  endpoint: '',
  accessKey: '',
  secretKey: '',
  bucket: '',
});

const securityForm = ref<SecurityConfig>({
  passwordMinLength: 8,
  passwordRequireUppercase: true,
  passwordRequireLowercase: true,
  passwordRequireNumber: true,
  passwordRequireSpecial: false,
  sessionTimeout: 30,
});

// ==================== 选项数据 ====================
const captchaTypeOptions = [
  { label: '图形验证码', value: 'graphic' },
  { label: '行为验证码', value: 'behavior' },
];

const smsProviderOptions = [
  { label: '阿里云', value: 'aliyun' },
  { label: '腾讯云', value: 'tencent' },
];

const storageTypeOptions = [
  { label: '本地存储', value: 'local' },
  { label: '阿里云OSS', value: 'oss' },
  { label: 'Amazon S3', value: 's3' },
];

// ==================== 加载配置数据 ====================
async function loadConfig(configKey: string) {
  loading.value = true;
  try {
    switch (configKey) {
      case 'site': {
        const data = await configApi.getSiteConfig();
        siteForm.value = data;
        break;
      }
      case 'login': {
        const data = await configApi.getLoginConfig();
        loginForm.value = data;
        break;
      }
      case 'register': {
        const data = await configApi.getRegisterConfig();
        registerForm.value = data;
        break;
      }
      case 'email': {
        const data = await configApi.getEmailConfig();
        emailForm.value = data;
        break;
      }
      case 'sms': {
        const data = await configApi.getSmsConfig();
        smsForm.value = data;
        break;
      }
      case 'storage': {
        const data = await configApi.getStorageConfig();
        storageForm.value = data;
        break;
      }
      case 'security': {
        const data = await configApi.getSecurityConfig();
        securityForm.value = data;
        break;
      }
    }
  } finally {
    loading.value = false;
  }
}

// ==================== 保存配置 ====================
async function handleSave() {
  saving.value = true;
  try {
    switch (selectedConfigKey.value) {
      case 'site': {
        await configApi.updateSiteConfig(siteForm.value);
        break;
      }
      case 'login': {
        await configApi.updateLoginConfig(loginForm.value);
        break;
      }
      case 'register': {
        await configApi.updateRegisterConfig(registerForm.value);
        break;
      }
      case 'email': {
        await configApi.updateEmailConfig(emailForm.value);
        break;
      }
      case 'sms': {
        await configApi.updateSmsConfig(smsForm.value);
        break;
      }
      case 'storage': {
        await configApi.updateStorageConfig(storageForm.value);
        break;
      }
      case 'security': {
        await configApi.updateSecurityConfig(securityForm.value);
        break;
      }
    }
    message.success('保存成功');
  } finally {
    saving.value = false;
  }
}

// ==================== 重置表单 ====================
function handleReset() {
  loadConfig(selectedConfigKey.value);
}

// ==================== 切换配置类型 ====================
function handleSelectConfig(configKey: string) {
  selectedConfigKey.value = configKey;
  loadConfig(configKey);
}

// ==================== 初始化 ====================
onMounted(() => {
  loadConfig(selectedConfigKey.value);
});
</script>

<template>
  <Page class="h-full m-4 bg-background">
    <NSplit
      direction="horizontal"
      default-size="200px"
      min="200px"
      max="320px"
      :resizable="true"
      class="h-full"
    >
      <template #1>
        <!-- 左侧配置列表 -->
        <div class="space-y-2">
          <div
            v-for="item in configList"
            :key="item.key"
            class="flex items-center gap-2 px-3 py-2 rounded cursor-pointer transition-colors"
            :class="
              selectedConfigKey === item.key
                ? 'bg-primary text-primary-foreground'
                : 'hover:bg-muted'
            "
            @click="handleSelectConfig(item.key)"
          >
            <IconifyIcon :icon="item.icon" class="text-lg" />
            <span>{{ item.label }}</span>
          </div>
        </div>
      </template>
      <template #2>
        <!-- 右侧配置表单 -->
        <div class="h-full bg-background p-4 overflow-auto">
          <NCard :title="configList.find((c) => c.key === selectedConfigKey)?.label" :loading="loading">
            <!-- 站点配置 -->
            <NForm
              v-if="selectedConfigKey === 'site'"
              :model="siteForm"
              label-placement="left"
              label-width="120"
            >
              <NFormItem label="站点名称" path="siteName">
                <NInput v-model:value="siteForm.siteName" placeholder="请输入站点名称" />
              </NFormItem>
              <NFormItem label="站点Logo" path="siteLogo">
                <NInput v-model:value="siteForm.siteLogo" placeholder="请输入Logo URL" />
              </NFormItem>
              <NFormItem label="版权信息" path="siteCopyright">
                <NInput v-model:value="siteForm.siteCopyright" placeholder="请输入版权信息" />
              </NFormItem>
              <NFormItem label="ICP备案号" path="siteIcp">
                <NInput v-model:value="siteForm.siteIcp" placeholder="请输入ICP备案号" />
              </NFormItem>
            </NForm>

            <!-- 登录配置 -->
            <NForm
              v-else-if="selectedConfigKey === 'login'"
              :model="loginForm"
              label-placement="left"
              label-width="120"
            >
              <NFormItem label="启用验证码" path="captchaEnabled">
                <NCheckbox v-model:checked="loginForm.captchaEnabled">启用</NCheckbox>
              </NFormItem>
              <NFormItem label="验证码类型" path="captchaType">
                <NSelect
                  v-model:value="loginForm.captchaType"
                  :options="captchaTypeOptions"
                  placeholder="请选择验证码类型"
                />
              </NFormItem>
              <NFormItem label="最大重试次数" path="maxRetry">
                <NInputNumber
                  v-model:value="loginForm.maxRetry"
                  :min="1"
                  :max="10"
                  placeholder="请输入最大重试次数"
                  class="w-full"
                />
              </NFormItem>
              <NFormItem label="锁定时间(分钟)" path="lockTime">
                <NInputNumber
                  v-model:value="loginForm.lockTime"
                  :min="1"
                  :max="1440"
                  placeholder="请输入锁定时间"
                  class="w-full"
                />
              </NFormItem>
            </NForm>

            <!-- 注册配置 -->
            <NForm
              v-else-if="selectedConfigKey === 'register'"
              :model="registerForm"
              label-placement="left"
              label-width="120"
            >
              <NFormItem label="开启注册" path="enabled">
                <NCheckbox v-model:checked="registerForm.enabled">启用</NCheckbox>
              </NFormItem>
              <NFormItem label="邮箱验证" path="verifyEmail">
                <NCheckbox v-model:checked="registerForm.verifyEmail">需要邮箱验证</NCheckbox>
              </NFormItem>
              <NFormItem label="手机验证" path="verifyPhone">
                <NCheckbox v-model:checked="registerForm.verifyPhone">需要手机验证</NCheckbox>
              </NFormItem>
              <NFormItem label="默认角色ID" path="defaultRoleId">
                <NInput v-model:value="registerForm.defaultRoleId" placeholder="请输入默认角色ID" />
              </NFormItem>
            </NForm>

            <!-- 邮件配置 -->
            <NForm
              v-else-if="selectedConfigKey === 'email'"
              :model="emailForm"
              label-placement="left"
              label-width="120"
            >
              <NFormItem label="SMTP服务器" path="host">
                <NInput v-model:value="emailForm.host" placeholder="请输入SMTP服务器地址" />
              </NFormItem>
              <NFormItem label="SMTP端口" path="port">
                <NInputNumber
                  v-model:value="emailForm.port"
                  :min="1"
                  :max="65535"
                  placeholder="请输入SMTP端口"
                  class="w-full"
                />
              </NFormItem>
              <NFormItem label="发件人邮箱" path="username">
                <NInput v-model:value="emailForm.username" placeholder="请输入发件人邮箱" />
              </NFormItem>
              <NFormItem label="邮箱密码" path="password">
                <NInput
                  v-model:value="emailForm.password"
                  type="password"
                  show-password-on="click"
                  placeholder="请输入邮箱密码或授权码"
                />
              </NFormItem>
              <NFormItem label="发件人名称" path="fromName">
                <NInput v-model:value="emailForm.fromName" placeholder="请输入发件人名称" />
              </NFormItem>
            </NForm>

            <!-- 短信配置 -->
            <NForm
              v-else-if="selectedConfigKey === 'sms'"
              :model="smsForm"
              label-placement="left"
              label-width="120"
            >
              <NFormItem label="服务商" path="provider">
                <NSelect
                  v-model:value="smsForm.provider"
                  :options="smsProviderOptions"
                  placeholder="请选择短信服务商"
                />
              </NFormItem>
              <NFormItem label="AccessKey" path="accessKey">
                <NInput
                  v-model:value="smsForm.accessKey"
                  type="password"
                  show-password-on="click"
                  placeholder="请输入AccessKey"
                />
              </NFormItem>
              <NFormItem label="SecretKey" path="secretKey">
                <NInput
                  v-model:value="smsForm.secretKey"
                  type="password"
                  show-password-on="click"
                  placeholder="请输入SecretKey"
                />
              </NFormItem>
              <NFormItem label="短信签名" path="signName">
                <NInput v-model:value="smsForm.signName" placeholder="请输入短信签名" />
              </NFormItem>
            </NForm>

            <!-- 存储配置 -->
            <NForm
              v-else-if="selectedConfigKey === 'storage'"
              :model="storageForm"
              label-placement="left"
              label-width="120"
            >
              <NFormItem label="存储类型" path="type">
                <NSelect
                  v-model:value="storageForm.type"
                  :options="storageTypeOptions"
                  placeholder="请选择存储类型"
                />
              </NFormItem>
              <NFormItem label="存储端点" path="endpoint">
                <NInput v-model:value="storageForm.endpoint" placeholder="请输入存储端点" />
              </NFormItem>
              <NFormItem label="AccessKey" path="accessKey">
                <NInput
                  v-model:value="storageForm.accessKey"
                  type="password"
                  show-password-on="click"
                  placeholder="请输入AccessKey"
                />
              </NFormItem>
              <NFormItem label="SecretKey" path="secretKey">
                <NInput
                  v-model:value="storageForm.secretKey"
                  type="password"
                  show-password-on="click"
                  placeholder="请输入SecretKey"
                />
              </NFormItem>
              <NFormItem label="存储桶名称" path="bucket">
                <NInput v-model:value="storageForm.bucket" placeholder="请输入存储桶名称" />
              </NFormItem>
            </NForm>

            <!-- 安全配置 -->
            <NForm
              v-else-if="selectedConfigKey === 'security'"
              :model="securityForm"
              label-placement="left"
              label-width="140"
            >
              <NFormItem label="密码最小长度" path="passwordMinLength">
                <NInputNumber
                  v-model:value="securityForm.passwordMinLength"
                  :min="6"
                  :max="32"
                  placeholder="请输入密码最小长度"
                  class="w-full"
                />
              </NFormItem>
              <NFormItem label="需要大写字母" path="passwordRequireUppercase">
                <NCheckbox v-model:checked="securityForm.passwordRequireUppercase">启用</NCheckbox>
              </NFormItem>
              <NFormItem label="需要小写字母" path="passwordRequireLowercase">
                <NCheckbox v-model:checked="securityForm.passwordRequireLowercase">启用</NCheckbox>
              </NFormItem>
              <NFormItem label="需要数字" path="passwordRequireNumber">
                <NCheckbox v-model:checked="securityForm.passwordRequireNumber">启用</NCheckbox>
              </NFormItem>
              <NFormItem label="需要特殊字符" path="passwordRequireSpecial">
                <NCheckbox v-model:checked="securityForm.passwordRequireSpecial">启用</NCheckbox>
              </NFormItem>
              <NFormItem label="会话超时时间(分钟)" path="sessionTimeout">
                <NInputNumber
                  v-model:value="securityForm.sessionTimeout"
                  :min="5"
                  :max="1440"
                  placeholder="请输入会话超时时间"
                  class="w-full"
                />
              </NFormItem>
            </NForm>

            <template #action>
              <NSpace justify="end">
                <NButton @click="handleReset">重置</NButton>
                <NButton type="primary" :loading="saving" @click="handleSave">
                  <template #icon><IconifyIcon icon="lucide:save" /></template>
                  保存
                </NButton>
              </NSpace>
            </template>
          </NCard>
        </div>
      </template>
    </NSplit>
  </Page>
</template>
