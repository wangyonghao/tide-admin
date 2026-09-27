<script lang="ts" setup>
import { computed, ref } from 'vue';

import { z } from '@vben/common-ui';

import { VbenButton, VbenInputPassword } from '@vben-core/shadcn-ui';

import { message } from '#/adapter/naive';
import { authApi } from '#/api/auth';
import { encryptByRsa } from '#/utils/crypto';

defineOptions({ name: 'ForceChangePassword' });

const props = defineProps<{
  tempToken: string;
  userId: string;
}>();

const emit = defineEmits<{
  cancel: [];
  success: [token: string];
}>();

// Form data
const oldPassword = ref('');
const newPassword = ref('');
const confirmPassword = ref('');

// Password validation schema
const passwordSchema = z.object({
  oldPassword: z.string().min(1, '请输入旧密码'),
  newPassword: z
    .string()
    .min(6, '密码长度至少6位')
    .regex(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/, '密码必须包含大小写字母和数字'),
  confirmPassword: z.string().min(1, '请确认新密码'),
});

// Validation state
const validationErrors = ref<Record<string, string>>({});

// Validate field
const validateField = (field: string, value: string) => {
  try {
    switch (field) {
      case 'confirmPassword': {
        passwordSchema.shape.confirmPassword.parse(value);
        if (value !== newPassword.value) {
          validationErrors.value.confirmPassword = '两次输入的密码不一致';
          return;
        }

        break;
      }
      case 'newPassword': {
        passwordSchema.shape.newPassword.parse(value);
        // Also check if passwords match
        validationErrors.value.confirmPassword =
          confirmPassword.value && value !== confirmPassword.value
            ? '两次输入的密码不一致'
            : '';

        break;
      }
      case 'oldPassword': {
        passwordSchema.shape.oldPassword.parse(value);

        break;
      }
      // No default
    }
    validationErrors.value[field] = '';
  } catch (error) {
    if (error instanceof z.ZodError) {
      validationErrors.value[field] = error.errors[0]?.message || '';
    }
  }
};

// Validate entire form
const validateForm = () => {
  const formData = {
    oldPassword: oldPassword.value,
    newPassword: newPassword.value,
    confirmPassword: confirmPassword.value,
  };

  try {
    passwordSchema.parse(formData);

    // Check if passwords match
    if (newPassword.value !== confirmPassword.value) {
      validationErrors.value.confirmPassword = '两次输入的密码不一致';
      return false;
    }

    validationErrors.value = {};
    return true;
  } catch (error) {
    if (error instanceof z.ZodError) {
      const errors: Record<string, string> = {};
      error.errors.forEach((err) => {
        if (err.path[0]) {
          errors[err.path[0] as string] = err.message;
        }
      });
      validationErrors.value = errors;
    }
    return false;
  }
};

// Submit handler
const submitting = ref(false);
const handleSubmit = async (event?: Event) => {
  event?.preventDefault();

  if (!validateForm()) {
    return;
  }

  try {
    submitting.value = true;

    const { token } = await authApi.forceChangePassword({
      userId: props.userId,
      tempToken: props.tempToken,
      oldPassword: encryptByRsa(oldPassword.value) || '',
      newPassword: encryptByRsa(newPassword.value) || '',
      confirmPassword: encryptByRsa(confirmPassword.value) || '',
    });

    message.success('密码修改成功');
    emit('success', token);
  } catch (error: any) {
    // Check if it's a password mismatch error
    if (
      error?.message?.includes('密码') ||
      error?.message?.includes('password')
    ) {
      message.error('用户名或密码不匹配');
      validationErrors.value.oldPassword = '用户名或密码不匹配';
    } else {
      message.error(error?.message || '密码修改失败');
    }
  } finally {
    submitting.value = false;
  }
};

// Cancel handler
const handleCancel = () => {
  emit('cancel');
};

// Computed properties for validation
const oldPasswordError = computed(() => validationErrors.value.oldPassword);
const newPasswordError = computed(() => validationErrors.value.newPassword);
const confirmPasswordError = computed(
  () => validationErrors.value.confirmPassword,
);
</script>

<template>
  <div @keydown.enter.prevent="handleSubmit">
    <!-- Title Section -->
    <div class="mb-7 sm:mx-auto sm:w-full sm:max-w-md">
      <h2
        class="text-foreground mb-3 text-3xl font-bold leading-9 tracking-tight lg:text-4xl"
      >
        🔒 密码已过期
      </h2>
      <p class="text-muted-foreground lg:text-md text-sm">
        为了您的账户安全，请修改密码后继续使用
      </p>
    </div>

    <!-- Form Section -->
    <form @submit="handleSubmit">
      <!-- Old Password Field -->
      <div class="mb-4">
        <label class="text-sm font-medium text-foreground mb-2 block">
          旧密码
        </label>
        <VbenInputPassword
          v-model="oldPassword"
          placeholder="请输入旧密码"
          :class="{ 'border-red-500': oldPasswordError }"
          @blur="validateField('oldPassword', oldPassword)"
        />
        <div v-if="oldPasswordError" class="text-sm text-red-500 mt-1">
          {{ oldPasswordError }}
        </div>
      </div>

      <!-- New Password Field -->
      <div class="mb-4">
        <label class="text-sm font-medium text-foreground mb-2 block">
          新密码
        </label>
        <VbenInputPassword
          v-model="newPassword"
          placeholder="请输入新密码（至少6位，包含大小写字母和数字）"
          :class="{ 'border-red-500': newPasswordError }"
          @blur="validateField('newPassword', newPassword)"
        />
        <div v-if="newPasswordError" class="text-sm text-red-500 mt-1">
          {{ newPasswordError }}
        </div>
      </div>

      <!-- Confirm Password Field -->
      <div class="mb-6">
        <label class="text-sm font-medium text-foreground mb-2 block">
          确认新密码
        </label>
        <VbenInputPassword
          v-model="confirmPassword"
          placeholder="请再次输入新密码"
          :class="{ 'border-red-500': confirmPasswordError }"
          @blur="validateField('confirmPassword', confirmPassword)"
        />
        <div v-if="confirmPasswordError" class="text-sm text-red-500 mt-1">
          {{ confirmPasswordError }}
        </div>
      </div>
    </form>

    <!-- Action Buttons -->
    <div class="flex gap-3">
      <VbenButton
        variant="outline"
        class="flex-1"
        :disabled="submitting"
        @click="handleCancel"
      >
        返回登录
      </VbenButton>
      <VbenButton
        class="flex-1"
        :loading="submitting"
        :class="{ 'cursor-wait': submitting }"
        @click="handleSubmit"
      >
        确认修改
      </VbenButton>
    </div>
  </div>
</template>
