<script setup lang="ts">
import type { DataTableColumns } from 'naive-ui';
import type { SmsLogResp } from '#/api/system/sms-log';

import { h, onMounted, ref } from 'vue';

import { IconifyIcon } from '@vben/icons';

import { SearchOutline } from '@vicons/ionicons5';
import {
  NButton,
  NDataTable,
  NIcon,
  NInput,
  NPopconfirm,
  NSelect,
  NSpace,
  NTag,
  useMessage,
} from 'naive-ui';

import {
  smsLogApi,
} from '#/api/system/sms-log';

const message = useMessage();

// ==================== 搜索表单 ====================
const searchForm = ref({
  configId: '',
  phone: '',
  status: null as number | null,
});

// ==================== 表格数据 ====================
const tableData = ref<SmsLogResp[]>([]);
const tableLoading = ref(false);
const tablePagination = ref({
  page: 1,
  pageSize: 10,
  itemCount: 0,
  showSizePicker: true,
  pageSizes: [10, 20, 50, 100],
  onChange: (page: number) => {
    tablePagination.value.page = page;
    loadTableData();
  },
  onUpdatePageSize: (pageSize: number) => {
    tablePagination.value.pageSize = pageSize;
    tablePagination.value.page = 1;
    loadTableData();
  },
});

// ==================== 状态选项 ====================
const statusOptions = [
  { label: '成功', value: 1 },
  { label: '失败', value: 0 },
];

// ==================== 表格列定义 ====================
const tableColumns: DataTableColumns<SmsLogResp> = [
  {
    title: '序号',
    key: 'index',
    width: 60,
    render: (_row, index) =>
      (tablePagination.value.page - 1) * tablePagination.value.pageSize +
      index +
      1,
  },
  {
    title: '手机号',
    key: 'phone',
    minWidth: 120,
  },
  {
    title: '参数配置',
    key: 'params',
    minWidth: 200,
  },
  {
    title: '发送状态',
    key: 'status',
    minWidth: 100,
    render(row) {
      return h(
        NTag,
        {
          type: row.status === 1 ? 'success' : 'error',
          size: 'small',
        },
        { default: () => (row.status === 1 ? '成功' : '失败') },
      );
    },
  },
  {
    title: '返回数据',
    key: 'resMsg',
    minWidth: 200,
  },
  {
    title: '创建人',
    key: 'createUserString',
    minWidth: 120,
  },
  {
    title: '创建时间',
    key: 'createTime',
    minWidth: 160,
    sorter: 'default',
  },
  {
    title: '操作',
    key: 'action',
    width: 100,
    fixed: 'right',
    render(row) {
      return h(
        NSpace,
        {},
        {
          default: () => [
            h(
              NPopconfirm,
              {
                onPositiveClick: () => handleDelete(row),
              },
              {
                trigger: () =>
                  h(
                    NButton,
                    {
                      size: 'small',
                      type: 'error',
                      text: true,
                    },
                    { default: () => '删除' },
                  ),
                default: () => `确定删除手机号为 ${row.phone} 的短信日志吗？`,
              },
            ),
          ],
        },
      );
    },
  },
];

// ==================== 加载数据 ====================
async function loadTableData() {
  tableLoading.value = true;
  try {
    const res = await smsLogApi.list({
      page: tablePagination.value.page,
      size: tablePagination.value.pageSize,
      configId: searchForm.value.configId || undefined,
      phone: searchForm.value.phone || undefined,
      status: searchForm.value.status ?? undefined,
      sort: ['createTime,desc'],
    });

    tableData.value = res.list;
    tablePagination.value.itemCount = res.total;
  } catch (error) {
    console.error('加载短信日志失败:', error);
    message.error('加载数据失败');
  } finally {
    tableLoading.value = false;
  }
}

// ==================== 搜索 ====================
function handleSearch() {
  tablePagination.value.page = 1;
  loadTableData();
}

// ==================== 重置 ====================
function handleReset() {
  searchForm.value = {
    configId: '',
    phone: '',
    status: null,
  };
  handleSearch();
}

// ==================== 删除 ====================
async function handleDelete(row: SmsLogResp) {
  try {
    await smsLogApi.delete(row.id);
    message.success('删除成功');
    await loadTableData();
  } catch (error) {
    console.error('删除短信日志失败:', error);
    message.error('删除失败');
  }
}

// ==================== 导出 ====================
function handleExport() {
  smsLogApi.export({
    configId: searchForm.value.configId || undefined,
    phone: searchForm.value.phone || undefined,
    status: searchForm.value.status ?? undefined,
    sort: ['createTime,desc'],
  });
}

// ==================== 初始化 ====================
onMounted(() => {
  loadTableData();
});
</script>

<template>
  <div class="h-full">
    <!-- 搜索和操作栏 -->
    <div class="mb-4">
      <!-- 搜索表单 - 响应式网格布局 -->
      <div
        class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-3 mb-3"
      >
        <NInput
          v-model:value="searchForm.configId"
          placeholder="配置ID"
          clearable
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <NIcon><SearchOutline /></NIcon>
          </template>
        </NInput>
        <NInput
          v-model:value="searchForm.phone"
          placeholder="手机号"
          clearable
          @keyup.enter="handleSearch"
        />
        <NSelect
          v-model:value="searchForm.status"
          :options="statusOptions"
          placeholder="发送状态"
          clearable
        />
      </div>

      <!-- 操作按钮 -->
      <div class="flex items-center gap-2 flex-wrap">
        <NButton type="primary" @click="handleSearch">
          <template #icon><IconifyIcon icon="lucide:search" /></template>
          查询
        </NButton>
        <NButton @click="handleReset">
          <template #icon><IconifyIcon icon="lucide:rotate-ccw" /></template>
          重置
        </NButton>
        <NButton @click="handleExport">
          <template #icon><IconifyIcon icon="lucide:download" /></template>
          导出
        </NButton>
      </div>
    </div>

    <!-- 数据表格 -->
    <NDataTable
      :columns="tableColumns"
      :data="tableData"
      :loading="tableLoading"
      :row-key="(row) => row.id"
      :pagination="tablePagination"
      scroll-x="1200px"
    />
  </div>
</template>

<style lang="scss" scoped></style>
