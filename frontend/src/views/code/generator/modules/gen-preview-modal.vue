<script setup lang="ts">
import type { GeneratePreviewResp } from '#/api/code';
import type { PreviewNode } from './preview-tree';

import { computed, ref, watch } from 'vue';

import { useVbenModal } from '@vben/common-ui';
import { IconifyIcon } from '@vben/icons';

import { useClipboard } from '@vueuse/core';

import { downloadCode, generateCode, genPreview } from '#/api/code';
import { CnCodeView } from '#/components/code-view';
import { Card, CardContent, CardHeader, CardTitle } from '#/ui/card';
import { VbenTree } from '#/ui/tree';
import { toast } from '#/ui-patterns/toast';

import { findPreviewNode, previewFileIcon } from './preview-tree';

const { copy, copied } = useClipboard();

const genPreviewList = ref<GeneratePreviewResp[]>([]);
const currentPreview = ref<GeneratePreviewResp>();
const previewTableNames = ref<string[]>([]);
const treeData = ref<PreviewNode[]>([]);
const selectedKey = ref<string | undefined>();

const mergeDir = (parent: PreviewNode) => {
  if (
    parent.children?.length === 1 &&
    typeof parent.children[0]?.key === 'number'
  ) {
    const mergeTitle = mergeDir(parent.children[0]);
    if (mergeTitle !== '') {
      parent.title = `${parent.title}/${mergeTitle}`;
    }
    parent.children = parent.children[0].children;
    return parent.title;
  }
  if (parent.children) {
    for (const child of parent.children) {
      mergeDir(child);
    }
  }
  return parent.title;
};

const pushDir = (
  children: PreviewNode[] | undefined,
  treeNode: PreviewNode,
) => {
  if (children) {
    for (const child of children) {
      if (child.title === treeNode.title) {
        return child.children;
      }
    }
  }
  children?.push(treeNode);
  return treeNode.children;
};

let autoIncrementKey = 0;

const assembleTree = (preview: GeneratePreviewResp) => {
  const separator = preview.path.includes('/') ? '/' : '\\';
  const paths: string[] = preview.path.split(separator);
  let tempChildren: PreviewNode[] | undefined = treeData.value;
  for (const path of paths) {
    autoIncrementKey++;
    tempChildren = pushDir(tempChildren, {
      title: path,
      key: `${autoIncrementKey}-0`,
      children: [],
    });
  }
  tempChildren?.push({
    title: preview.fileName,
    key: preview.fileName,
    children: [],
  });
};

const onDownload = async () => {
  const tableNames = previewTableNames.value;
  const res: any = await downloadCode(tableNames);
  const contentDisposition = res.headers['content-disposition'];
  const pattern = /filename=([^;]+\.[^.;]+);*/;
  const result = pattern.exec(contentDisposition) || '';
  const fileName = window.decodeURI(result[1] ?? '');
  const blob = new Blob([res.data]);
  const downloadElement = document.createElement('a');
  const href = window.URL.createObjectURL(blob);
  downloadElement.style.display = 'none';
  downloadElement.href = href;
  downloadElement.download = fileName;
  document.body.append(downloadElement);
  downloadElement.click();
  downloadElement.remove();
  window.URL.revokeObjectURL(href);
};

const onGenerator = async () => {
  const tableNames = previewTableNames.value;
  await generateCode(tableNames);
  toast.success('代码生成成功');
};

const onCopy = () => {
  if (currentPreview.value) {
    copy(currentPreview.value.content);
  }
};
watch(copied, () => {
  if (copied.value) {
    toast.success('复制成功');
  }
});

function onTreeSelect(item: { value: { key?: unknown } }) {
  const key = item.value?.key;
  if (typeof key !== 'string' || key === '') return;
  const node = findPreviewNode(treeData.value, key);
  if (!node) return;
  const preview = genPreviewList.value.find(
    (row) => row.fileName === node.key,
  );
  if (!preview) return;
  currentPreview.value = preview;
  selectedKey.value = node.key;
}

const onOpen = async (tableNames: Array<string>) => {
  treeData.value = [];
  previewTableNames.value = tableNames;
  const data = await genPreview(tableNames);
  genPreviewList.value = data;
  for (const preview of genPreviewList.value) {
    assembleTree(preview);
  }
  for (const valueElement of treeData.value) {
    mergeDir(valueElement);
  }
  const first = genPreviewList.value[0];
  selectedKey.value = first?.fileName;
  currentPreview.value = first;
};

const [Modal, modalApi] = useVbenModal({
  centered: true,
  showCancelButton: false,
  showConfirmButton: false,
  onOpenChange(isOpen) {
    if (isOpen) {
      const data = modalApi.getData<string[]>();
      if (data) {
        onOpen(data);
      }
    }
  },
});
const getTitle = computed(() => {
  return previewTableNames.value?.join(',');
});

const previewPath = computed(() => {
  const preview = currentPreview.value;
  if (!preview) return '';
  const separator = preview.path.includes('/') ? '/' : '\\';
  return `${preview.path}${separator}${preview.fileName}`;
});
</script>

<template>
  <Modal
    class="h-[90%] w-[90%]"
    :title="getTitle"
  >
    <template #title>
      <div class="flex items-end justify-center">
        {{
          previewTableNames.length === 1
            ? `生成 ${previewTableNames[0]} 表预览`
            : '批量生成预览'
        }}
        <a
          v-access:code="['code:generator:generate']"
          class="text-foreground ml-2.5 cursor-pointer"
          @click="onDownload"
        >
          下载源码
        </a>
        <a
          v-access:code="['code:generator:generate']"
          class="text-foreground ml-2.5 cursor-pointer"
          @click="onGenerator"
        >
          生成源码
        </a>
      </div>
    </template>
    <div class="flex h-full min-h-0 gap-2">
      <div
        class="border-border bg-card w-[250px] shrink-0 overflow-auto rounded-[var(--radius)] border p-2"
      >
        <VbenTree
          v-if="treeData.length > 0"
          v-model="selectedKey"
          :tree-data="treeData"
          value-field="key"
          label-field="title"
          children-field="children"
          :default-expanded-level="99"
          :show-icon="false"
          :transition="false"
          @select="onTreeSelect"
        >
          <template #node="{ value, hasChildren }">
            <IconifyIcon
              :icon="
                previewFileIcon(String(value?.title ?? ''), Boolean(hasChildren))
              "
              class="size-4 shrink-0"
            />
            <span class="truncate">{{ value?.title }}</span>
          </template>
        </VbenTree>
      </div>
      <Card class="flex min-h-0 min-w-0 flex-1 flex-col overflow-hidden">
        <CardHeader class="py-3">
          <CardTitle class="text-sm font-medium">
            {{ previewPath }}
          </CardTitle>
        </CardHeader>
        <CardContent class="relative min-h-0 flex-1 overflow-auto">
          <button
            type="button"
            class="text-foreground absolute top-2 right-4 z-10 inline-flex cursor-pointer items-center gap-1"
            title="复制"
            @click="onCopy"
          >
            <IconifyIcon
              icon="lucide:copy"
              class="size-4"
            />
            <span>复制</span>
          </button>
          <CnCodeView
            v-if="currentPreview"
            :type="
              'vue' === currentPreview.fileName.split('.')[1]
                ? 'vue'
                : 'javascript'
            "
            :code-json="currentPreview.content"
          />
        </CardContent>
      </Card>
    </div>
  </Modal>
</template>
