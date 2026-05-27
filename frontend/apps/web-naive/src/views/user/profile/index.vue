<script setup lang="ts">
import { computed, ref } from 'vue';
import { NCard, NTabs, NTabPane } from 'naive-ui';
import { useUserStore } from '#/store/user';
import { $t } from '#/locales';

import ProfileSummary from './components/profile-summary.vue';
import BasicInfo from './components/basic-info.vue';
import SecuritySettings from './components/security-settings.vue';
import NotificationSettings from './components/notification-settings.vue';
import OperationLogs from './components/operation-logs.vue';
import PreferencesSettings from './components/preferences-settings.vue';

const userStore = useUserStore();

// 当前激活的标签页
const activeTab = ref('basic');

// 用户信息
const userInfo = computed(() => userStore.user);

// 标签页配置
const tabs = [
  { key: 'basic', label: $t('page.profile.tabs.basic'), icon: 'i-carbon-user' },
  { key: 'security', label: $t('page.profile.tabs.security'), icon: 'i-carbon-security' },
  { key: 'notification', label: $t('page.profile.tabs.notification'), icon: 'i-carbon-notification' },
  { key: 'logs', label: $t('page.profile.tabs.logs'), icon: 'i-carbon-document' },
  { key: 'preferences', label: $t('page.profile.tabs.preferences'), icon: 'i-carbon-settings' },
];
</script>

<template>
  <div class="profile-container">
    <div class="profile-layout">
      <!-- 左侧摘要卡片 -->
      <div class="profile-sidebar">
        <ProfileSummary :user-info="userInfo" />
      </div>

      <!-- 右侧内容区域 -->
      <div class="profile-content">
        <NCard :bordered="false" class="shadow-sm">
          <NTabs
            v-model:value="activeTab"
            type="line"
            animated
            :tabs-padding="20"
          >
            <NTabPane
              v-for="tab in tabs"
              :key="tab.key"
              :name="tab.key"
              :tab="tab.label"
            >
              <div class="tab-content py-4">
                <BasicInfo v-if="tab.key === 'basic'" />
                <SecuritySettings v-else-if="tab.key === 'security'" />
                <NotificationSettings v-else-if="tab.key === 'notification'" />
                <OperationLogs v-else-if="tab.key === 'logs'" />
                <PreferencesSettings v-else-if="tab.key === 'preferences'" />
              </div>
            </NTabPane>
          </NTabs>
        </NCard>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.profile-container {
  min-height: calc(100vh - 120px);
  padding: 16px;
  background: var(--vben-background-color);
}

.profile-layout {
  display: grid;
  grid-template-columns: 1fr;
  gap: 16px;
  max-width: 1600px;
  margin: 0 auto;
}

.profile-sidebar {
  width: 100%;
}

.profile-content {
  width: 100%;
}

.tab-content {
  min-height: 400px;
}

// 平板端（≥768px）
@media (min-width: 768px) {
  .profile-container {
    padding: 20px;
  }

  .profile-layout {
    gap: 20px;
  }
}

// 桌面端（≥1024px）- 左右布局
@media (min-width: 1024px) {
  .profile-container {
    padding: 24px;
  }

  .profile-layout {
    grid-template-columns: 320px 1fr;
    gap: 24px;
  }
}

// 大屏（≥1280px）
@media (min-width: 1280px) {
  .profile-layout {
    grid-template-columns: 360px 1fr;
  }
}

// 超大屏（≥1536px）
@media (min-width: 1536px) {
  .profile-layout {
    grid-template-columns: 380px 1fr;
  }
}
</style>
