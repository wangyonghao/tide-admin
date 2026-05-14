<script setup lang="ts">
import type { UploadFileInfo } from 'naive-ui';

import { computed, ref, watch } from 'vue';

import { useUserStore } from '@/stores/user';

import { message } from '#/adapter/naive';

const props = defineProps<{
  modelValue?: string;
}>();

const emit = defineEmits<{
  (e: 'update:modelValue', value: string | undefined): void;
}>();

const userStore = useUserStore();

const uploadUrl = '/api/sys/file/upload/image';
const headers = computed(() => ({
  Authorization: userStore.token || '',
}));

// 文件列表
const fileList = ref<UploadFileInfo[]>([]);

// 监听 modelValue 变化，回显图片
watch(
  () => props.modelValue,
  (val) => {
    if (val && fileList.value.length === 0) {
      fileList.value = [
        {
          id: 'default',
          name: 'image',
          status: 'finished',
          url: val,
        },
      ];
    } else if (!val) {
      fileList.value = [];
    }
  },
  { immediate: true },
);

// 上传完成
function handleFinish({
  file,
  event,
}: {
  event?: ProgressEvent;
  file: UploadFileInfo;
}) {
  try {
    // 方案1：先校验非空，再断言（推荐）
    if (!event?.target) {
      message.error('上传响应解析失败：事件目标为空');
      return;
    }
    const response = JSON.parse((event.target as XMLHttpRequest).response);
    if ((response.code === 0 || response.code === 200) && response.data) {
      // 返回的是 SysFile 对象，优先使用 url
      const data = response.data as
        | string
        | { filePath?: string; url?: string };
      const url = typeof data === 'string' ? data : data.url || data.filePath;
      if (url) {
        emit('update:modelValue', url);
        file.url = url;
        file.status = 'finished';
      }
    }
  } catch (error) {
    console.error('上传失败', error);
  }
  return file;
}

// 移除图片
function handleRemove() {
  emit('update:modelValue', undefined);
  return true;
}
</script>

<template>
  <div class="image-upload">
    <n-upload
      :action="uploadUrl"
      :headers="headers"
      :max="1"
      list-type="image-card"
      v-model:file-list="fileList"
      @finish="handleFinish"
      @remove="handleRemove"
      accept="image/*"
    >
      上传图片
    </n-upload>
  </div>
</template>

<style scoped>
.image-upload {
  width: 100%;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  gap: 4px;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  color: #999;
}

:deep(.n-upload-file-list .n-upload-file.n-upload-file--image-card-type) {
  width: 100px;
  height: 100px;
}

:deep(.n-upload-trigger.n-upload-trigger--image-card) {
  width: 100px;
  height: 100px;
}
</style>
