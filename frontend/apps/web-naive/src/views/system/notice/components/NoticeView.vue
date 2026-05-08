<script setup lang="ts">
import type { NoticeDetailResp } from '#/api/system/notice';

import { computed } from 'vue';

import { IconifyIcon } from '@vben/icons';
import { $t } from '@vben/locales';

import { NDivider, NSpace } from 'naive-ui';

import { VbenTiptapPreview } from '@vben/plugins/tiptap';

defineOptions({ name: 'NoticeView' });

const props = defineProps<{
  notice: NoticeDetailResp;
}>();

const content = computed(() => props.notice?.content ?? '');
</script>

<template>
  <div class="notice-view">
    <!-- 标题 -->
    <h1 class="text-3xl font-bold text-center mb-6">{{ notice.title }}</h1>

    <!-- 信息栏 -->
    <div class="text-sm text-gray-600 text-center mb-6">
      <NSpace justify="center" align="center">
        <span class="flex items-center gap-1">
          <IconifyIcon icon="lucide:user" />
          <span class="font-medium">{{ $t('system.notice.createUser') }}：</span>
          <span>{{ notice.createUserString }}</span>
        </span>

        <NDivider vertical />

        <span class="flex items-center gap-1">
          <IconifyIcon icon="lucide:clock" />
          <span class="font-medium">{{ $t('system.notice.publishTime') }}：</span>
          <span>{{ notice.publishTime }}</span>
        </span>

        <template v-if="notice.updateTime">
          <NDivider vertical />
          <span class="flex items-center gap-1">
            <IconifyIcon icon="lucide:calendar" />
            <span class="font-medium">{{ $t('system.notice.updateTime') }}：</span>
            <span>{{ notice.updateTime }}</span>
          </span>
        </template>
      </NSpace>
    </div>

    <!-- 分割线 -->
    <NDivider />

    <!-- 内容区域 -->
    <div class="mt-6">
      <VbenTiptapPreview :content="content" :min-height="300" />
    </div>
  </div>
</template>

<style scoped lang="scss">
.notice-view {
  min-height: 400px;
}
</style>
