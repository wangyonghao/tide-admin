<script setup lang="ts">
import { computed } from 'vue';
import { NCard, NTag, NSpace, NDivider, NText } from 'naive-ui';
import { VbenAvatar } from '@vben-core/shadcn-ui';
import { $t } from '#/locales';
import type { UserProfile } from '#/api/auth';

interface Props {
  userInfo?: UserProfile;
}

const props = defineProps<Props>();

// 性别显示
const genderText = computed(() => {
  if (!props.userInfo) return '';
  switch (props.userInfo.gender) {
    case 1:
      return $t('page.profile.basic.male');
    case 2:
      return $t('page.profile.basic.female');
    default:
      return $t('page.profile.basic.unknown');
  }
});

// 账号状态
const accountStatus = computed(() => {
  return {
    text: $t('page.profile.summary.active'),
    type: 'success' as const,
  };
});

// 安全等级（根据绑定情况计算）
const securityLevel = computed(() => {
  if (!props.userInfo) return { text: $t('page.profile.summary.low'), type: 'error' as const };
  
  let score = 0;
  if (props.userInfo.phone) score += 1;
  if (props.userInfo.email) score += 1;
  
  if (score >= 2) {
    return { text: $t('page.profile.summary.high'), type: 'success' as const };
  } else if (score === 1) {
    return { text: $t('page.profile.summary.medium'), type: 'warning' as const };
  }
  return { text: $t('page.profile.summary.low'), type: 'error' as const };
});

// 手机号脱敏
const maskedPhone = computed(() => {
  if (!props.userInfo?.phone) return $t('page.profile.summary.phoneUnbound');
  const phone = props.userInfo.phone;
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2');
});

// 邮箱脱敏
const maskedEmail = computed(() => {
  if (!props.userInfo?.email) return $t('page.profile.summary.emailUnbound');
  const email = props.userInfo.email;
  const [name, domain] = email.split('@');
  if (name.length <= 2) return email;
  return `${name.slice(0, 2)}***@${domain}`;
});
</script>

<template>
  <NCard :bordered="false" class="profile-summary-card shadow-sm">
    <div class="text-center">
      <!-- 头像 -->
      <VbenAvatar
        :src="userInfo?.avatar"
        :alt="userInfo?.nickname || 'User'"
        :size="100"
        class="mb-4 mx-auto"
      />

      <!-- 昵称 -->
      <h3 class="text-xl font-semibold mb-2">
        {{ userInfo?.nickname || '-' }}
      </h3>

      <!-- 用户名 -->
      <NText depth="3" class="text-sm">
        @{{ userInfo?.username || '-' }}
      </NText>

      <!-- 账号状态 -->
      <div class="mt-4">
        <NTag :type="accountStatus.type" size="small">
          {{ accountStatus.text }}
        </NTag>
      </div>
    </div>

    <NDivider class="my-4" />

    <!-- 基本信息 -->
    <NSpace vertical :size="12">
      <div class="info-item">
        <span class="info-label">{{ $t('page.profile.basic.gender') }}</span>
        <span class="info-value">{{ genderText }}</span>
      </div>

      <div class="info-item">
        <span class="info-label">{{ $t('page.profile.basic.dept') }}</span>
        <span class="info-value">{{ userInfo?.deptName || '-' }}</span>
      </div>

      <div class="info-item">
        <span class="info-label">{{ $t('page.profile.basic.phone') }}</span>
        <span class="info-value">{{ maskedPhone }}</span>
      </div>

      <div class="info-item">
        <span class="info-label">{{ $t('page.profile.basic.email') }}</span>
        <span class="info-value">{{ maskedEmail }}</span>
      </div>

      <div class="info-item">
        <span class="info-label">{{ $t('page.profile.basic.registrationDate') }}</span>
        <span class="info-value">{{ userInfo?.registrationDate || '-' }}</span>
      </div>
    </NSpace>

    <NDivider class="my-4" />

    <!-- 安全等级 -->
    <div class="security-level">
      <div class="flex items-center justify-between mb-2">
        <span class="text-sm text-gray-600">{{ $t('page.profile.summary.securityLevel') }}</span>
        <NTag :type="securityLevel.type" size="small">
          {{ securityLevel.text }}
        </NTag>
      </div>

      <!-- 绑定状态 -->
      <NSpace vertical :size="8" class="mt-3">
        <div class="flex items-center text-xs">
          <span :class="userInfo?.phone ? 'text-green-600' : 'text-gray-400'">
            <span class="i-carbon-checkmark-filled mr-1" v-if="userInfo?.phone" />
            <span class="i-carbon-close-filled mr-1" v-else />
            {{ userInfo?.phone ? $t('page.profile.summary.phoneBound') : $t('page.profile.summary.phoneUnbound') }}
          </span>
        </div>
        <div class="flex items-center text-xs">
          <span :class="userInfo?.email ? 'text-green-600' : 'text-gray-400'">
            <span class="i-carbon-checkmark-filled mr-1" v-if="userInfo?.email" />
            <span class="i-carbon-close-filled mr-1" v-else />
            {{ userInfo?.email ? $t('page.profile.summary.emailBound') : $t('page.profile.summary.emailUnbound') }}
          </span>
        </div>
      </NSpace>
    </div>
  </NCard>
</template>

<style lang="scss" scoped>
.profile-summary-card {
  position: sticky;
  top: 80px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;

  .info-label {
    color: var(--vben-text-secondary-color);
  }

  .info-value {
    font-weight: 500;
    color: var(--vben-text-primary-color);
  }
}

.security-level {
  padding: 12px;
  background: var(--vben-background-color-deep);
  border-radius: 8px;
}

// 响应式：移动端取消 sticky
@media (max-width: 1024px) {
  .profile-summary-card {
    position: static;
  }
}
</style>
