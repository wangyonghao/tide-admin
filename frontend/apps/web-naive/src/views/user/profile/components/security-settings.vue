<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import {
  NCard,
  NSpace,
  NButton,
  NModal,
  NForm,
  NFormItem,
  NInput,
  NList,
  NListItem,
  NThing,
  NTag,
  NEmpty,
  NPopconfirm,
} from 'naive-ui';
import { $t } from '#/locales';
import { useUserStore } from '#/store/user';
import { message } from '#/adapter/naive';
import {
  userProfileApi,
  type BindSocialAccountRes,
} from '#/api/system/user-profile';
import { loginLogApi, type LoginLogResult } from '#/api/auth/login-log';
import { encryptByRsa } from '#/utils/crypto';

const userStore = useUserStore();

// 修改密码
const showPasswordModal = ref(false);
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
});

// 修改手机号
const showPhoneModal = ref(false);
const phoneForm = ref({
  phone: '',
  captcha: '',
  oldPassword: '',
});

// 修改邮箱
const showEmailModal = ref(false);
const emailForm = ref({
  email: '',
  captcha: '',
  oldPassword: '',
});

// 登录设备列表
const loginDevices = ref<LoginLogResult[]>([]);
const loadingDevices = ref(false);

// 三方账号列表
const socialAccounts = ref<BindSocialAccountRes[]>([]);
const loadingSocial = ref(false);

// 获取登录设备列表
const fetchLoginDevices = async () => {
  try {
    loadingDevices.value = true;
    const res = await loginLogApi.list({
      username: userStore.user?.username,
      loginStatus: 'SUCCESS',
      page: 1,
      size: 10,
    });
    loginDevices.value = res.list || [];
  } catch (error) {
    console.error('获取登录设备失败:', error);
  } finally {
    loadingDevices.value = false;
  }
};

// 获取三方账号列表
const fetchSocialAccounts = async () => {
  try {
    loadingSocial.value = true;
    socialAccounts.value = await userProfileApi.listSocial();
  } catch (error) {
    console.error('获取三方账号失败:', error);
  } finally {
    loadingSocial.value = false;
  }
};

// 修改密码
const handleChangePassword = async () => {
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    message.error('两次输入的密码不一致');
    return;
  }

  try {
    await userProfileApi.updatePassword({
      oldPassword: encryptByRsa(passwordForm.value.oldPassword) || '',
      newPassword: encryptByRsa(passwordForm.value.newPassword) || '',
    });
    message.success($t('page.profile.security.passwordChanged'));
    showPasswordModal.value = false;
    passwordForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' };
    
    // 退出登录
    setTimeout(() => {
      userStore.logout();
    }, 1500);
  } catch (error) {
    console.error('修改密码失败:', error);
  }
};

// 修改手机号
const handleChangePhone = async () => {
  try {
    await userProfileApi.updatePhone({
      ...phoneForm.value,
      oldPassword: encryptByRsa(phoneForm.value.oldPassword) || '',
    });
    message.success($t('page.profile.security.phoneChanged'));
    showPhoneModal.value = false;
    phoneForm.value = { phone: '', captcha: '', oldPassword: '' };
    await userStore.fetchAuthInfo();
  } catch (error) {
    console.error('修改手机号失败:', error);
  }
};

// 修改邮箱
const handleChangeEmail = async () => {
  try {
    await userProfileApi.updateEmail({
      ...emailForm.value,
      oldPassword: encryptByRsa(emailForm.value.oldPassword) || '',
    });
    message.success($t('page.profile.security.emailChanged'));
    showEmailModal.value = false;
    emailForm.value = { email: '', captcha: '', oldPassword: '' };
    await userStore.fetchAuthInfo();
  } catch (error) {
    console.error('修改邮箱失败:', error);
  }
};

// 解绑三方账号
const handleUnbindSocial = async (source: string) => {
  try {
    await userProfileApi.unbindSocial(source);
    message.success('解绑成功');
    await fetchSocialAccounts();
  } catch (error) {
    console.error('解绑失败:', error);
  }
};

// 退出所有设备
const handleLogoutAllDevices = () => {
  message.info('功能开发中');
};

onMounted(() => {
  fetchLoginDevices();
  fetchSocialAccounts();
});
</script>

<template>
  <div class="security-settings">
    <h3 class="text-lg font-semibold mb-6">{{ $t('page.profile.tabs.security') }}</h3>

    <NSpace vertical :size="24">
      <!-- 修改密码 -->
      <NCard :bordered="false" class="shadow-sm">
        <div class="flex items-center justify-between">
          <div>
            <h4 class="font-medium mb-1">{{ $t('page.profile.security.changePassword') }}</h4>
            <p class="text-sm text-gray-500">{{ $t('page.profile.security.passwordRule') }}</p>
          </div>
          <NButton @click="showPasswordModal = true">
            {{ $t('page.profile.security.changePassword') }}
          </NButton>
        </div>
      </NCard>

      <!-- 绑定手机 -->
      <NCard :bordered="false" class="shadow-sm">
        <div class="flex items-center justify-between">
          <div>
            <h4 class="font-medium mb-1">{{ $t('page.profile.basic.phone') }}</h4>
            <p class="text-sm text-gray-500">
              {{ userStore.user?.phone || $t('page.profile.summary.phoneUnbound') }}
            </p>
          </div>
          <NButton @click="showPhoneModal = true">
            {{ userStore.user?.phone ? $t('page.profile.security.changePhone') : $t('page.profile.security.bindPhone') }}
          </NButton>
        </div>
      </NCard>

      <!-- 绑定邮箱 -->
      <NCard :bordered="false" class="shadow-sm">
        <div class="flex items-center justify-between">
          <div>
            <h4 class="font-medium mb-1">{{ $t('page.profile.basic.email') }}</h4>
            <p class="text-sm text-gray-500">
              {{ userStore.user?.email || $t('page.profile.summary.emailUnbound') }}
            </p>
          </div>
          <NButton @click="showEmailModal = true">
            {{ userStore.user?.email ? $t('page.profile.security.changeEmail') : $t('page.profile.security.bindEmail') }}
          </NButton>
        </div>
      </NCard>

      <!-- 登录设备管理 -->
      <NCard :bordered="false" class="shadow-sm">
        <div class="flex items-center justify-between mb-4">
          <h4 class="font-medium">{{ $t('page.profile.security.loginDevices') }}</h4>
          <NButton
            type="error"
            secondary
            size="small"
            @click="handleLogoutAllDevices"
          >
            {{ $t('page.profile.security.logoutAllDevices') }}
          </NButton>
        </div>

        <NList v-if="loginDevices.length > 0" :loading="loadingDevices">
          <NListItem v-for="device in loginDevices" :key="device.id">
            <NThing>
              <template #header>
                <div class="flex items-center gap-2">
                  <span>{{ device.browser }} / {{ device.os }}</span>
                  <NTag v-if="device.id === loginDevices[0]?.id" type="success" size="small">
                    {{ $t('page.profile.security.currentDevice') }}
                  </NTag>
                </div>
              </template>
              <template #description>
                <NSpace vertical :size="4">
                  <span class="text-xs">{{ $t('page.profile.security.ipAddress') }}: {{ device.ipAddress }}</span>
                  <span class="text-xs">{{ $t('page.profile.security.location') }}: {{ device.location }}</span>
                  <span class="text-xs">{{ $t('page.profile.security.loginTime') }}: {{ device.loginTime }}</span>
                </NSpace>
              </template>
            </NThing>
          </NListItem>
        </NList>
        <NEmpty v-else description="暂无登录记录" />
      </NCard>

      <!-- 三方账号绑定 -->
      <NCard :bordered="false" class="shadow-sm">
        <h4 class="font-medium mb-4">{{ $t('page.profile.security.socialAccount') }}</h4>
        
        <NList v-if="socialAccounts.length > 0" :loading="loadingSocial">
          <NListItem v-for="account in socialAccounts" :key="account.source">
            <NThing>
              <template #header>{{ account.description }}</template>
              <template #header-extra>
                <NPopconfirm
                  :positive-text="$t('common.confirm')"
                  :negative-text="$t('common.cancel')"
                  @positive-click="handleUnbindSocial(account.source)"
                >
                  <template #trigger>
                    <NButton type="error" text size="small">
                      {{ $t('page.profile.security.unbind') }}
                    </NButton>
                  </template>
                  {{ $t('page.profile.security.unbindConfirm') }}
                </NPopconfirm>
              </template>
            </NThing>
          </NListItem>
        </NList>
        <NEmpty v-else description="暂无绑定的三方账号" />
      </NCard>
    </NSpace>

    <!-- 修改密码弹窗 -->
    <NModal
      v-model:show="showPasswordModal"
      preset="card"
      :title="$t('page.profile.security.changePassword')"
      style="width: 500px"
    >
      <NForm :model="passwordForm" label-placement="left" label-width="100">
        <NFormItem :label="$t('page.profile.security.oldPassword')">
          <NInput
            v-model:value="passwordForm.oldPassword"
            type="password"
            show-password-on="click"
            :placeholder="$t('page.profile.security.oldPassword')"
          />
        </NFormItem>
        <NFormItem :label="$t('page.profile.security.newPassword')">
          <NInput
            v-model:value="passwordForm.newPassword"
            type="password"
            show-password-on="click"
            :placeholder="$t('page.profile.security.newPassword')"
          />
        </NFormItem>
        <NFormItem :label="$t('page.profile.security.confirmPassword')">
          <NInput
            v-model:value="passwordForm.confirmPassword"
            type="password"
            show-password-on="click"
            :placeholder="$t('page.profile.security.confirmPassword')"
          />
        </NFormItem>
      </NForm>
      <template #footer>
        <div class="flex justify-end gap-2">
          <NButton @click="showPasswordModal = false">{{ $t('common.cancel') }}</NButton>
          <NButton type="primary" @click="handleChangePassword">{{ $t('common.confirm') }}</NButton>
        </div>
      </template>
    </NModal>

    <!-- 修改手机号弹窗 -->
    <NModal
      v-model:show="showPhoneModal"
      preset="card"
      :title="$t('page.profile.security.changePhone')"
      style="width: 500px"
    >
      <NForm :model="phoneForm" label-placement="left" label-width="100">
        <NFormItem :label="$t('page.profile.basic.phone')">
          <NInput
            v-model:value="phoneForm.phone"
            :placeholder="$t('page.profile.basic.phone')"
          />
        </NFormItem>
        <NFormItem :label="$t('page.profile.security.captcha')">
          <div class="flex gap-2 w-full">
            <NInput
              v-model:value="phoneForm.captcha"
              :placeholder="$t('page.profile.security.captcha')"
            />
            <NButton>{{ $t('page.profile.security.sendCaptcha') }}</NButton>
          </div>
        </NFormItem>
        <NFormItem :label="$t('page.profile.security.oldPassword')">
          <NInput
            v-model:value="phoneForm.oldPassword"
            type="password"
            show-password-on="click"
            :placeholder="$t('page.profile.security.oldPassword')"
          />
        </NFormItem>
      </NForm>
      <template #footer>
        <div class="flex justify-end gap-2">
          <NButton @click="showPhoneModal = false">{{ $t('common.cancel') }}</NButton>
          <NButton type="primary" @click="handleChangePhone">{{ $t('common.confirm') }}</NButton>
        </div>
      </template>
    </NModal>

    <!-- 修改邮箱弹窗 -->
    <NModal
      v-model:show="showEmailModal"
      preset="card"
      :title="$t('page.profile.security.changeEmail')"
      style="width: 500px"
    >
      <NForm :model="emailForm" label-placement="left" label-width="100">
        <NFormItem :label="$t('page.profile.basic.email')">
          <NInput
            v-model:value="emailForm.email"
            :placeholder="$t('page.profile.basic.email')"
          />
        </NFormItem>
        <NFormItem :label="$t('page.profile.security.captcha')">
          <div class="flex gap-2 w-full">
            <NInput
              v-model:value="emailForm.captcha"
              :placeholder="$t('page.profile.security.captcha')"
            />
            <NButton>{{ $t('page.profile.security.sendCaptcha') }}</NButton>
          </div>
        </NFormItem>
        <NFormItem :label="$t('page.profile.security.oldPassword')">
          <NInput
            v-model:value="emailForm.oldPassword"
            type="password"
            show-password-on="click"
            :placeholder="$t('page.profile.security.oldPassword')"
          />
        </NFormItem>
      </NForm>
      <template #footer>
        <div class="flex justify-end gap-2">
          <NButton @click="showEmailModal = false">{{ $t('common.cancel') }}</NButton>
          <NButton type="primary" @click="handleChangeEmail">{{ $t('common.confirm') }}</NButton>
        </div>
      </template>
    </NModal>
  </div>
</template>

<style lang="scss" scoped>
.security-settings {
  max-width: 900px;
}
</style>
