<script lang="ts" setup>
import type { OpenAppApi } from '#/api/open/app';

import { computed, ref } from 'vue';

import { useVbenDrawer } from '@vben/common-ui';
import { $t } from '@vben/locales';

import {
  NButton,
  NDescriptions,
  NDescriptionsItem,
  NTag,
  useMessage,
} from 'naive-ui';

const appData = ref<OpenAppApi.AppResp>();
const message = useMessage();

const [Drawer, drawerApi] = useVbenDrawer({
  onOpenChange(isOpen) {
    if (isOpen) {
      const data = drawerApi.getData<OpenAppApi.AppResp>();
      if (data) {
        appData.value = data;
      }
    }
  },
  showConfirmButton: false,
  cancelText: $t('common.cancel'),
});

const getDrawerTitle = computed(() => {
  return $t('open.app.expireTime');
});

// 复制到剪贴板
const copyToClipboard = async (text: string) => {
  try {
    await navigator.clipboard.writeText(text);
    message.success('复制成功');
  } catch (error) {
    console.error('复制失败:', error);
    message.error('复制失败');
  }
};
</script>

<template>
  <Drawer :title="getDrawerTitle">
    <div class="p-4">
      <NDescriptions :column="2" size="large" class="general-description">
        <NDescriptionsItem label="ID">{{ appData?.id }}</NDescriptionsItem>
        <NDescriptionsItem :label="$t('open.app.name')">
          {{ appData?.name }}
        </NDescriptionsItem>
        <NDescriptionsItem :label="$t('open.app.accessKey')" :span="2">
          <div class="inline-block">
            <span class="font-mono text-sm">{{ appData?.accessKey }}</span>
            <NButton
              class="ml-2"
              v-if="appData?.accessKey"
              type="primary"
              size="small"
              @click="copyToClipboard(appData.accessKey)"
            >
              {{ $t('open.app.copy') }}
            </NButton>
          </div>
        </NDescriptionsItem>
        <NDescriptionsItem :label="$t('open.app.status')">
          <NTag v-if="appData?.status === 1" type="success">启用</NTag>
          <NTag v-else type="error">禁用</NTag>
        </NDescriptionsItem>
        <NDescriptionsItem :label="$t('open.app.expireTime')">
          {{ appData?.expireTime }}
        </NDescriptionsItem>
        <NDescriptionsItem :label="$t('open.app.createUser')">
          {{ appData?.createUserString }}
        </NDescriptionsItem>
        <NDescriptionsItem :label="$t('open.app.createTime')">
          {{ appData?.createTime }}
        </NDescriptionsItem>
        <NDescriptionsItem :label="$t('open.app.updateUser')">
          {{ appData?.updateUserString }}
        </NDescriptionsItem>
        <NDescriptionsItem :label="$t('open.app.updateTime')">
          {{ appData?.updateTime }}
        </NDescriptionsItem>
        <NDescriptionsItem :label="$t('open.app.description')" :span="2">
          {{ appData?.description }}
        </NDescriptionsItem>
      </NDescriptions>
    </div>
  </Drawer>
</template>

<style scoped lang="scss"></style>
