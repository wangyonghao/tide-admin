<script setup lang="ts">
import { ref, computed } from 'vue';
import {
  NForm,
  NFormItem,
  NInput,
  NRadioGroup,
  NRadio,
  NModal,
} from 'naive-ui';
import { VbenAvatar } from '@vben-core/shadcn-ui';
import { Button } from '#/ui/button';
import { FileUpload } from '#/ui/upload';
import { $t } from '#/locales';
import { useUserStore } from '#/store/user';
import { message } from '#/adapter/naive';
import { userProfileApi } from '#/api/system/user-profile';
import { resolveFilePreviewUrl } from '#/api/system/file';

const userStore = useUserStore();

// 编辑状态
const isEditing = ref(false);

// 表单数据
const formData = ref({
  displayName: '',
  gender: 0 as 0 | 1 | 2,
});

// 头像上传
const showAvatarModal = ref(false);
const avatarUrl = ref('');
const uploadingAvatar = ref(false);

// 初始化表单数据
const initFormData = () => {
  if (userStore.user) {
    formData.value = {
      displayName: userStore.user.displayName,
      gender: userStore.user.gender,
    };
  }
};

// 开始编辑
const handleEdit = () => {
  initFormData();
  isEditing.value = true;
};

// 取消编辑
const handleCancel = () => {
  isEditing.value = false;
  initFormData();
};

// 保存修改
const handleSave = async () => {
  try {
    await userProfileApi.updateBaseInfo(formData.value);
    message.success($t('page.profile.basic.editSuccess'));
    isEditing.value = false;
    // 刷新用户信息
    await userStore.fetchAuthInfo();
  } catch (error) {
    console.error('更新用户信息失败:', error);
  }
};

// 上传头像
const handleAvatarUpload = async (files: File[]) => {
  const avatarFile = files[0];
  if (!avatarFile) return;
  try {
    uploadingAvatar.value = true;
    const body = new FormData();
    body.append('avatarFile', avatarFile);

    await userProfileApi.uploadAvatar(body);
    message.success($t('page.profile.basic.uploadSuccess'));
    showAvatarModal.value = false;

    // 刷新用户信息
    await userStore.fetchAuthInfo();
  } catch (error) {
    console.error('上传头像失败:', error);
  } finally {
    uploadingAvatar.value = false;
  }
};

// 性别选项
const genderOptions = [
  { label: $t('page.profile.basic.unknown'), value: 0 },
  { label: $t('page.profile.basic.male'), value: 1 },
  { label: $t('page.profile.basic.female'), value: 2 },
];

// 用户信息（只读）
const userInfo = computed(() => userStore.user);
</script>

<template>
  <div class="basic-info">
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-lg font-semibold">{{ $t('page.profile.tabs.basic') }}</h3>
      <Button v-if="!isEditing" type="button" @click="handleEdit">
        {{ $t('page.profile.basic.edit') }}
      </Button>
    </div>

    <!-- 头像上传区域 -->
    <div class="avatar-section mb-8 p-6 bg-gray-50 dark:bg-gray-800 rounded-lg">
      <div class="flex items-center gap-6">
        <!-- 头像 -->
        <VbenAvatar
          :src="resolveFilePreviewUrl(userInfo?.avatar)"
          :alt="userInfo?.displayName || 'User'"
          :size="100"
          class="mb-4 mx-auto"
          @click="showAvatarModal = true"
        />
        <div class="flex-1">
          <h4 class="font-medium mb-2">
            {{ $t('page.profile.basic.avatar') }}
          </h4>
          <p class="text-sm text-gray-500 mb-3">
            {{ $t('page.profile.basic.uploadTip') }}
          </p>
          <Button
            type="button"
            size="sm"
            variant="outline"
            @click="showAvatarModal = true"
          >
            {{ $t('page.profile.basic.changeAvatar') }}
          </Button>
        </div>
      </div>
    </div>

    <!-- 基本信息表单 -->
    <NForm
      :model="formData"
      label-placement="left"
      label-width="120"
      :disabled="!isEditing"
    >
      <NFormItem :label="$t('page.profile.basic.username')">
        <NInput :value="userInfo?.username" disabled />
      </NFormItem>

      <NFormItem :label="$t('page.profile.basic.displayName')">
        <NInput
          v-model:value="formData.displayName"
          :placeholder="$t('page.profile.basic.displayName')"
        />
      </NFormItem>

      <NFormItem :label="$t('page.profile.basic.gender')">
        <NRadioGroup v-model:value="formData.gender">
          <NRadio
            v-for="option in genderOptions"
            :key="option.value"
            :value="option.value"
          >
            {{ option.label }}
          </NRadio>
        </NRadioGroup>
      </NFormItem>

      <NFormItem :label="$t('page.profile.basic.phone')">
        <NInput :value="userInfo?.phone" disabled />
      </NFormItem>

      <NFormItem :label="$t('page.profile.basic.email')">
        <NInput :value="userInfo?.email" disabled />
      </NFormItem>

      <NFormItem :label="$t('page.profile.basic.dept')">
        <NInput :value="userInfo?.deptName" disabled />
      </NFormItem>

      <NFormItem :label="$t('page.profile.basic.registrationDate')">
        <NInput :value="userInfo?.registrationDate" disabled />
      </NFormItem>

      <!-- 操作按钮 -->
      <NFormItem v-if="isEditing" :show-label="false">
        <div class="flex items-center gap-2">
          <Button type="button" @click="handleSave">
            {{ $t('page.profile.basic.save') }}
          </Button>
          <Button type="button" variant="outline" @click="handleCancel">
            {{ $t('page.profile.basic.cancel') }}
          </Button>
        </div>
      </NFormItem>
    </NForm>

    <!-- 头像上传弹窗 -->
    <NModal
      v-model:show="showAvatarModal"
      preset="card"
      :title="$t('page.profile.basic.uploadAvatar')"
      style="width: 500px"
    >
      <div class="text-center">
        <FileUpload
          :max="1"
          accept="image/png,image/jpeg,image/jpg"
          :disabled="uploadingAvatar"
          @select="handleAvatarUpload"
        >
          <Button type="button" :loading="uploadingAvatar">
            {{ $t('page.profile.basic.uploadAvatar') }}
          </Button>
        </FileUpload>
        <p class="text-sm text-gray-500 mt-4">
          {{ $t('page.profile.basic.uploadTip') }}
        </p>
      </div>
    </NModal>
  </div>
</template>

<style lang="scss" scoped>
.basic-info {
  max-width: 800px;
}

.avatar-section {
  transition: all 0.3s ease;

  &:hover {
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }
}
</style>
