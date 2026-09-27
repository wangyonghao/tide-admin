<script setup lang="ts">
import type { UploadCustomRequestOptions, UploadFileInfo } from 'naive-ui';

import { ref, watch } from 'vue';

import { message } from '#/adapter/naive';
import { fileApi, resolveFilePreviewUrl, toFileId } from '#/api/system/file';

const props = defineProps<{
  /** 头像/图片对应的 fileId */
  modelValue?: string | null;
}>();

const emit = defineEmits<{
  (e: 'update:modelValue', value: string | undefined): void;
}>();

const fileList = ref<UploadFileInfo[]>([]);

watch(
  () => props.modelValue,
  (val) => {
    const fileId = toFileId(val);
    if (fileId != null) {
      fileList.value = [
        {
          id: String(fileId),
          name: 'image',
          status: 'finished',
          url: resolveFilePreviewUrl(fileId),
        },
      ];
    } else {
      fileList.value = [];
    }
  },
  { immediate: true },
);

async function handleUpload({ file, onFinish, onError }: UploadCustomRequestOptions) {
  try {
    if (!file.file) {
      onError();
      return;
    }
    const result = await fileApi.upload(file.file as File);
    emit('update:modelValue', result.fileId);
    file.url = resolveFilePreviewUrl(result.fileId);
    file.status = 'finished';
    onFinish();
  } catch (error) {
    console.error('上传失败', error);
    message.error('上传失败');
    onError();
  }
}

function handleRemove() {
  emit('update:modelValue', undefined);
  return true;
}
</script>

<template>
  <div class="image-upload">
    <n-upload
      :max="1"
      list-type="image-card"
      v-model:file-list="fileList"
      :custom-request="handleUpload"
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

:deep(.n-upload-file-list .n-upload-file.n-upload-file--image-card-type) {
  width: 100px;
  height: 100px;
}

:deep(.n-upload-trigger.n-upload-trigger--image-card) {
  width: 100px;
  height: 100px;
}
</style>
