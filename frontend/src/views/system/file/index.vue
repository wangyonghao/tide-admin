<script setup lang="ts">
import type {
  DataTableColumns,
  DropdownOption,
  UploadCustomRequestOptions,
  UploadFileInfo,
} from 'naive-ui';
import type { FileCategory, FileResult } from '#/api/system/file';

import { computed, h, onMounted, reactive, ref, watch } from 'vue';

import { IconifyIcon } from '@vben/icons';
import { Page } from '@vben/common-ui';

import { SearchOutline } from '@vicons/ionicons5';
import {
  NButton,
  NCard,
  NCascader,
  NDataTable,
  NDropdown,
  NIcon,
  NInput,
  NSpace,
  NUpload,
  useMessage,
} from 'naive-ui';

import { fileApi, resolveFilePreviewUrl } from '#/api/system/file';

const message = useMessage();

const searchKeyword = ref('');
const category = ref<FileCategory>('ALL');
const sortOrder = ref<'asc' | 'desc'>('desc');
const checkedRowKeys = ref<string[]>([]);
const tableData = ref<FileResult[]>([]);
const tableLoading = ref(false);
const uploadFileList = ref<UploadFileInfo[]>([]);

const pagination = reactive({
  page: 1,
  pageSize: 20,
  itemCount: 0,
  showSizePicker: true,
  pageSizes: [20, 50, 100],
  prefix: ({ itemCount }: { itemCount?: number }) => `共 ${itemCount ?? 0} 项`,
  onChange: (page: number) => {
    pagination.page = page;
    loadTableData();
  },
  onUpdatePageSize: (pageSize: number) => {
    pagination.pageSize = pageSize;
    pagination.page = 1;
    loadTableData();
  },
});

const categoryOptions = [
  { label: '全部', value: 'ALL' },
  { label: '压缩包', value: 'ARCHIVE' },
  {
    label: '文档',
    value: 'DOCUMENT',
    children: [
      { label: '全部', value: 'DOCUMENT' },
      { label: '文本', value: 'DOCUMENT_TEXT' },
      { label: '表格', value: 'DOCUMENT_SPREADSHEET' },
      { label: '幻灯片', value: 'DOCUMENT_PRESENTATION' },
      { label: 'PDF', value: 'DOCUMENT_PDF' },
    ],
  },
  { label: '图片', value: 'IMAGE' },
  { label: '多媒体', value: 'MEDIA' },
];

const hasSelection = computed(() => checkedRowKeys.value.length > 0);

function getExtension(fileName?: string) {
  if (!fileName || !fileName.includes('.')) {
    return '';
  }
  return fileName.slice(fileName.lastIndexOf('.') + 1).toLowerCase();
}

function getFileIcon(row: FileResult) {
  const ext = getExtension(row.fileName);
  if (row.contentType?.startsWith('image/') || ['jpg', 'jpeg', 'png', 'gif', 'webp', 'svg'].includes(ext)) {
    return { icon: 'vscode-icons:file-type-image', color: '' };
  }
  if (['doc', 'docx'].includes(ext)) {
    return { icon: 'vscode-icons:file-type-word', color: '' };
  }
  if (['xls', 'xlsx', 'csv'].includes(ext)) {
    return { icon: 'vscode-icons:file-type-excel', color: '' };
  }
  if (['ppt', 'pptx'].includes(ext)) {
    return { icon: 'vscode-icons:file-type-powerpoint', color: '' };
  }
  if (ext === 'pdf') {
    return { icon: 'vscode-icons:file-type-pdf2', color: '' };
  }
  if (['zip', 'rar', '7z', 'tar', 'gz'].includes(ext)) {
    return { icon: 'vscode-icons:file-type-zip', color: '' };
  }
  if (['mp4', 'avi', 'mov', 'mkv', 'mp3', 'wav'].includes(ext)) {
    return { icon: 'vscode-icons:file-type-video', color: '' };
  }
  return { icon: 'lucide:file', color: 'text-gray-400' };
}

function formatFileSize(bytes?: number) {
  if (bytes == null) return '-';
  if (bytes === 0) return '0B';
  if (bytes < 1024) return `${bytes}B`;
  if (bytes < 1024 * 1024) {
    const kb = bytes / 1024;
    return `${kb >= 10 ? kb.toFixed(0) : kb.toFixed(1)}K`;
  }
  if (bytes < 1024 * 1024 * 1024) {
    const mb = bytes / (1024 * 1024);
    return `${mb >= 10 ? mb.toFixed(0) : mb.toFixed(1)}M`;
  }
  return `${(bytes / (1024 * 1024 * 1024)).toFixed(1)}G`;
}

function formatRelativeTime(value?: string) {
  if (!value) return '-';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;

  const now = new Date();
  const startOfToday = new Date(now.getFullYear(), now.getMonth(), now.getDate());
  const startOfTarget = new Date(date.getFullYear(), date.getMonth(), date.getDate());
  const dayDiff = Math.round((startOfToday.getTime() - startOfTarget.getTime()) / 86_400_000);
  const hm = `${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;

  if (dayDiff === 0) return `今天 ${hm}`;
  if (dayDiff === 1) return `昨天 ${hm}`;
  if (date.getFullYear() === now.getFullYear()) {
    return `${String(date.getMonth() + 1).padStart(2, '0')}/${String(date.getDate()).padStart(2, '0')} ${hm}`;
  }
  return `${date.getFullYear()}/${String(date.getMonth() + 1).padStart(2, '0')}/${String(date.getDate()).padStart(2, '0')} ${hm}`;
}

function resolveStorageLabel(row: FileResult) {
  const storage = row.storageType;
  if (!storage) return '-';
  if (typeof storage === 'string') return storage;
  return storage.description || '-';
}

function rowActions(row: FileResult): DropdownOption[] {
  return [
    {
      label: '下载',
      key: 'download',
      icon: () => h(IconifyIcon, { icon: 'lucide:download' }),
    },
    {
      label: '预览',
      key: 'preview',
      icon: () => h(IconifyIcon, { icon: 'lucide:eye' }),
    },
    { type: 'divider', key: 'd1' },
    {
      label: '删除',
      key: 'delete',
      icon: () => h(IconifyIcon, { icon: 'lucide:trash-2', class: 'text-red-500' }),
    },
  ];
}

async function handleRowAction(key: string | number, row: FileResult) {
  switch (String(key)) {
    case 'download':
      await fileApi.download(row.id, row.fileName);
      break;
    case 'preview': {
      const url = resolveFilePreviewUrl(row.id);
      if (url) window.open(url, '_blank');
      break;
    }
    case 'delete':
      await handleDelete(row);
      break;
  }
}

const columns = computed<DataTableColumns<FileResult>>(() => [
  { type: 'selection', width: 48 },
  {
    title: '文件',
    key: 'fileName',
    ellipsis: { tooltip: true },
    render: (row) => {
      const meta = getFileIcon(row);
      return h('div', { class: 'flex min-w-0 items-center gap-2' }, [
        h(IconifyIcon, { icon: meta.icon, class: `text-xl shrink-0 ${meta.color}` }),
        h('span', { class: 'truncate', title: row.fileName }, row.fileName),
      ]);
    },
  },
  {
    title: '大小',
    key: 'fileSize',
    width: 100,
    render: (row) => formatFileSize(row.fileSize),
  },
  {
    title: () =>
      h(
        'button',
        {
          class: 'inline-flex items-center gap-1 hover:text-primary',
          onClick: toggleSortOrder,
        },
        [
          '时间',
          h(IconifyIcon, {
            icon: sortOrder.value === 'desc' ? 'lucide:arrow-down-narrow-wide' : 'lucide:arrow-up-narrow-wide',
            class: 'text-sm',
          }),
        ],
      ),
    key: 'createTime',
    width: 140,
    render: (row) => formatRelativeTime(row.createTime),
  },
  {
    title: '存储',
    key: 'storageType',
    ellipsis: { tooltip: true },
    width: 140,
    render: (row) => resolveStorageLabel(row),
  },
  {
    title: '操作',
    key: 'actions',
    width: 100,
    render: (row) =>
      h(
        NDropdown,
        {
          trigger: 'click',
          options: rowActions(row),
          onSelect: (key: string | number) => handleRowAction(key, row),
        },
        {
          default: () =>
            h(
              NButton,
              { text: true, size: 'small' },
              {
                icon: () => h(IconifyIcon, { icon: 'lucide:ellipsis', class: 'text-base' }),
              },
            ),
        },
      ),
  },
]);

function toggleSortOrder() {
  sortOrder.value = sortOrder.value === 'desc' ? 'asc' : 'desc';
  pagination.page = 1;
  loadTableData();
}

async function loadTableData() {
  try {
    tableLoading.value = true;
    const response = await fileApi.page({
      fileName: searchKeyword.value || undefined,
      category: category.value === 'ALL' ? undefined : category.value,
      sortOrder: sortOrder.value,
      page: pagination.page,
      pageSize: pagination.pageSize,
    });
    tableData.value = response.records;
    pagination.itemCount = response.total;
  } catch (error) {
    message.error('加载文件列表失败');
    console.error(error);
  } finally {
    tableLoading.value = false;
  }
}

watch(searchKeyword, () => {
  pagination.page = 1;
  loadTableData();
});

watch(category, () => {
  pagination.page = 1;
  loadTableData();
});

async function handleDelete(row: FileResult) {
  try {
    await fileApi.delete(row.id);
    message.success('删除成功');
    checkedRowKeys.value = checkedRowKeys.value.filter((id) => id !== row.id);
    await loadTableData();
  } catch (error) {
    message.error('删除失败');
    console.error(error);
  }
}

async function handleBatchDelete() {
  const ids = checkedRowKeys.value.filter((id) => id.length > 0);
  if (!ids.length) return;
  try {
    await fileApi.batchDelete(ids);
    message.success('批量删除成功');
    checkedRowKeys.value = [];
    await loadTableData();
  } catch (error) {
    message.error('批量删除失败');
    console.error(error);
  }
}

async function handleBatchDownload() {
  const selectedIds = new Set(checkedRowKeys.value);
  const rows = tableData.value.filter((item) => selectedIds.has(item.id));
  if (!rows.length) return;
  for (const row of rows) {
    await fileApi.download(row.id, row.fileName);
  }
  message.success(`已开始下载 ${rows.length} 个文件`);
}

async function handleUpload({ file, onFinish, onError }: UploadCustomRequestOptions) {
  if (!file.file) {
    onError();
    return;
  }
  try {
    await fileApi.upload(file.file as File);
    message.success('上传成功');
    onFinish();
    uploadFileList.value = [];
    await loadTableData();
  } catch (error) {
    message.error('上传失败');
    console.error(error);
    onError();
  }
}

onMounted(() => {
  loadTableData();
});
</script>

<template>
  <Page auto-content-height>
    <NCard :bordered="false" class="h-full" content-style="display:flex;flex-direction:column;gap:12px;height:100%">
      <div class="flex flex-wrap items-center justify-between gap-3">
        <NSpace align="center">
          <NButton :disabled="!hasSelection" @click="handleBatchDownload">
            <template #icon>
              <IconifyIcon icon="lucide:download" />
            </template>
            下载
          </NButton>
          <NCascader
            v-model:value="category"
            :options="categoryOptions"
            :show-path="false"
            check-strategy="child"
            expand-trigger="hover"
            placeholder="筛选"
            clearable
            class="w-40"
            @update:value="(v) => (category = (v as FileCategory) || 'ALL')"
          />
          <NButton
            v-if="hasSelection"
            type="error"
            secondary
            @click="handleBatchDelete"
          >
            删除所选 ({{ checkedRowKeys.length }})
          </NButton>
        </NSpace>

        <NSpace>
          <NInput
            v-model:value="searchKeyword"
            clearable
            placeholder="搜索文件名"
            class="w-56"
          >
            <template #prefix>
              <NIcon :component="SearchOutline" />
            </template>
          </NInput>
          <NUpload
            v-model:file-list="uploadFileList"
            :show-file-list="false"
            :custom-request="handleUpload"
          >
            <NButton type="primary">
              <template #icon>
                <IconifyIcon icon="lucide:upload" />
              </template>
              上传
            </NButton>
          </NUpload>
        </NSpace>
      </div>

      <NDataTable
        v-model:checked-row-keys="checkedRowKeys"
        :columns="columns"
        :data="tableData"
        :loading="tableLoading"
        :pagination="pagination"
        :row-key="(row) => String(row.id)"
        :bordered="false"
        :single-line="false"
        flex-height
        class="flex-1"
        size="small"
      />
    </NCard>
  </Page>
</template>
