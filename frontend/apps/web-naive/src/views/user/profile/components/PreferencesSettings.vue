<script setup lang="ts">
import { ref, computed } from 'vue';
import {
  NCard,
  NSpace,
  NSelect,
  NRadioGroup,
  NRadio,
  NSwitch,
  NButton,
  NPopconfirm,
  NDivider,
  NInput,
} from 'naive-ui';
import { $t } from '#/locales';
import { preferences } from '@vben/preferences';
import { message } from '#/adapter/naive';

// 语言选项
const languageOptions = [
  { label: '简体中文', value: 'zh-CN' },
  { label: 'English', value: 'en-US' },
];

// 主题模式选项
const themeModeOptions = [
  { label: $t('page.profile.preferences.light'), value: 'light' },
  { label: $t('page.profile.preferences.dark'), value: 'dark' },
  { label: $t('page.profile.preferences.auto'), value: 'auto' },
];

// 布局模式选项
const layoutOptions = [
  { label: $t('page.profile.preferences.sidebarLayout'), value: 'sidebar-nav' },
  { label: $t('page.profile.preferences.topLayout'), value: 'header-nav' },
  { label: $t('page.profile.preferences.mixLayout'), value: 'mixed-nav' },
];

// 内容宽度选项
const contentWidthOptions = [
  { label: $t('page.profile.preferences.fixed'), value: 'fixed' },
  { label: $t('page.profile.preferences.fluid'), value: 'fluid' },
];

// 偏好设置
const settings = ref({
  language: preferences.app.locale,
  themeMode: preferences.theme.mode,
  colorScheme: preferences.theme.colorPrimary,
  layout: preferences.app.layout,
  contentWidth: preferences.app.contentCompact ? 'fixed' : 'fluid',
  compactMode: preferences.app.compact,
  showBreadcrumb: preferences.breadcrumb.enable,
  showTabs: preferences.tabbar.enable,
  showFooter: preferences.footer.enable,
  defaultHomePath: preferences.app.defaultHomePath,
});

// 保存设置
const handleSave = () => {
  // 更新语言
  locale.value = settings.value.language;
  
  // 更新主题
  preferences.theme.mode = settings.value.themeMode as any;
  preferences.theme.colorPrimary = settings.value.colorScheme;
  
  // 更新布局
  preferences.app.layout = settings.value.layout as any;
  preferences.app.contentCompact = settings.value.contentWidth === 'fixed';
  preferences.app.compact = settings.value.compactMode;
  
  // 更新显示设置
  preferences.breadcrumb.enable = settings.value.showBreadcrumb;
  preferences.tabbar.enable = settings.value.showTabs;
  preferences.footer.enable = settings.value.showFooter;
  
  // 更新首页路径
  preferences.app.defaultHomePath = settings.value.defaultHomePath;
  
  message.success(t('page.profile.preferences.saveSuccess'));
};

// 恢复默认
const handleReset = () => {
  settings.value = {
    language: 'zh-CN',
    themeMode: 'auto',
    colorScheme: '#1890ff',
    layout: 'sidebar-nav',
    contentWidth: 'fluid',
    compactMode: false,
    showBreadcrumb: true,
    showTabs: true,
    showFooter: true,
    defaultHomePath: '/dashboard',
  };
  handleSave();
};

// 预设主题色
const presetColors = [
  '#1890ff', // 拂晓蓝
  '#722ed1', // 酱紫
  '#eb2f96', // 法式洋红
  '#f5222d', // 薄暮
  '#fa8c16', // 日暮
  '#52c41a', // 极光绿
  '#13c2c2', // 明青
];
</script>

<template>
  <div class="preferences-settings">
    <h3 class="text-lg font-semibold mb-6">{{ $t('page.profile.tabs.preferences') }}</h3>

    <NSpace vertical :size="24">
      <!-- 基础设置 -->
      <NCard :bordered="false" class="shadow-sm">
        <h4 class="font-medium mb-4">基础设置</h4>
        
        <NSpace vertical :size="16">
          <div class="setting-item">
            <div class="setting-label">{{ $t('page.profile.preferences.language') }}</div>
            <NSelect
              v-model:value="settings.language"
              :options="languageOptions"
              style="width: 200px"
            />
          </div>

          <NDivider class="my-2" />

          <div class="setting-item">
            <div class="setting-label">{{ $t('page.profile.preferences.themeMode') }}</div>
            <NRadioGroup v-model:value="settings.themeMode">
              <NRadio
                v-for="option in themeModeOptions"
                :key="option.value"
                :value="option.value"
              >
                {{ option.label }}
              </NRadio>
            </NRadioGroup>
          </div>

          <NDivider class="my-2" />

          <div class="setting-item">
            <div class="setting-label">{{ $t('page.profile.preferences.colorScheme') }}</div>
            <div class="flex gap-3">
              <div
                v-for="color in presetColors"
                :key="color"
                class="color-item"
                :class="{ active: settings.colorScheme === color }"
                :style="{ backgroundColor: color }"
                @click="settings.colorScheme = color"
              />
            </div>
          </div>
        </NSpace>
      </NCard>

      <!-- 布局设置 -->
      <NCard :bordered="false" class="shadow-sm">
        <h4 class="font-medium mb-4">布局设置</h4>
        
        <NSpace vertical :size="16">
          <div class="setting-item">
            <div class="setting-label">{{ $t('page.profile.preferences.layoutMode') }}</div>
            <NRadioGroup v-model:value="settings.layout">
              <NSpace vertical :size="8">
                <NRadio
                  v-for="option in layoutOptions"
                  :key="option.value"
                  :value="option.value"
                >
                  {{ option.label }}
                </NRadio>
              </NSpace>
            </NRadioGroup>
          </div>

          <NDivider class="my-2" />

          <div class="setting-item">
            <div class="setting-label">{{ $t('page.profile.preferences.contentWidth') }}</div>
            <NRadioGroup v-model:value="settings.contentWidth">
              <NRadio
                v-for="option in contentWidthOptions"
                :key="option.value"
                :value="option.value"
              >
                {{ option.label }}
              </NRadio>
            </NRadioGroup>
          </div>

          <NDivider class="my-2" />

          <div class="setting-item">
            <div class="setting-label">{{ $t('page.profile.preferences.compactMode') }}</div>
            <NSwitch v-model:value="settings.compactMode" />
          </div>
        </NSpace>
      </NCard>

      <!-- 显示设置 -->
      <NCard :bordered="false" class="shadow-sm">
        <h4 class="font-medium mb-4">显示设置</h4>
        
        <NSpace vertical :size="16">
          <div class="setting-item">
            <div class="setting-label">{{ $t('page.profile.preferences.showBreadcrumb') }}</div>
            <NSwitch v-model:value="settings.showBreadcrumb" />
          </div>

          <NDivider class="my-2" />

          <div class="setting-item">
            <div class="setting-label">{{ $t('page.profile.preferences.showTabs') }}</div>
            <NSwitch v-model:value="settings.showTabs" />
          </div>

          <NDivider class="my-2" />

          <div class="setting-item">
            <div class="setting-label">{{ $t('page.profile.preferences.showFooter') }}</div>
            <NSwitch v-model:value="settings.showFooter" />
          </div>
        </NSpace>
      </NCard>

      <!-- 首页设置 -->
      <NCard :bordered="false" class="shadow-sm">
        <h4 class="font-medium mb-4">{{ $t('page.profile.preferences.homePage') }}</h4>
        
        <div class="setting-item">
          <div class="setting-label">{{ $t('page.profile.preferences.defaultHomePath') }}</div>
          <NInput
            v-model:value="settings.defaultHomePath"
            :placeholder="$t('page.profile.preferences.defaultHomePath')"
            style="width: 300px"
          />
        </div>
      </NCard>

      <!-- 操作按钮 -->
      <div class="flex justify-between">
        <NPopconfirm
          :positive-text="$t('common.confirm')"
          :negative-text="$t('common.cancel')"
          @positive-click="handleReset"
        >
          <template #trigger>
            <NButton>{{ $t('page.profile.preferences.resetToDefault') }}</NButton>
          </template>
          {{ $t('page.profile.preferences.resetConfirm') }}
        </NPopconfirm>

        <NButton type="primary" @click="handleSave">
          {{ $t('page.profile.basic.save') }}
        </NButton>
      </div>
    </NSpace>
  </div>
</template>

<style lang="scss" scoped>
.preferences-settings {
  max-width: 800px;
}

.setting-item {
  display: flex;
  justify-content: space-between;
  align-items: center;

  .setting-label {
    font-weight: 500;
    color: var(--vben-text-primary-color);
  }
}

.color-item {
  width: 32px;
  height: 32px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;

  &:hover {
    transform: scale(1.1);
  }

  &.active {
    border-color: var(--vben-text-primary-color);
    box-shadow: 0 0 0 2px rgba(0, 0, 0, 0.1);
  }
}
</style>
