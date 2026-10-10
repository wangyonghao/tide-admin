<script setup lang="ts">
import type { DataTableColumns } from 'naive-ui';
import type { OnlineUser } from '#/api/monitor/online';

import { h, onMounted, ref } from 'vue';

import { IconifyIcon } from '@vben/icons';
import { Page } from '@vben/common-ui';
import { useAccessStore } from '@vben/stores';

import { SearchOutline } from '@vicons/ionicons5';
import { NDataTable, NIcon, NInput, useMessage } from 'naive-ui';

import { onlineApi } from '#/api/monitor/online';
import { Button } from '#/ui/button';
import {
  ConfirmAction,
  type ConfirmActionExpose,
} from '#/ui-patterns/confirm-action';

const message = useMessage();
const confirmAction = ref<ConfirmActionExpose | null>(null);
const accessStore = useAccessStore();
const currentToken = accessStore.accessToken;

// ==================== 搜索表单 ====================
const searchForm = ref({ keyword: '' });

// ==================== 表格数据 ====================
const tableData = ref<OnlineUser[]>([]);
const tableLoading = ref(false);
const selectedRowKeys = ref<string[]>([]);
const tablePagination = ref({
  page: 1,
  pageSize: 10,
  itemCount: 0,
  showSizePicker: true,
  pageSizes: [10, 20, 50],
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

// ==================== 表格列定义 ====================
const tableColumns: DataTableColumns<OnlineUser> = [
  {
    type: 'selection',
    disabled: (row) => row.token === currentToken,
  },
  {
    title: '序号',
    key: 'index',
    width: 60,
    render: (_row, index) =>
      (tablePagination.value.page - 1) * tablePagination.value.pageSize +
      index +
      1,
  },
  { title: '用户名', key: 'loginName', minWidth: 120 },
  { title: 'IP地址', key: 'ip', minWidth: 140 },
  { title: '登录地点', key: 'location', minWidth: 150 },
  { title: '浏览器', key: 'browser', minWidth: 150 },
  { title: '操作系统', key: 'os', minWidth: 120 },
  { title: '登录时间', key: 'loginTime', minWidth: 160 },
  { title: '最后活跃时间', key: 'lastActiveTime', minWidth: 160 },
  {
    title: '操作',
    key: 'action',
    width: 100,
    fixed: 'right',
    render(row) {
      return h(
        Button,
        {
          type: 'button',
          variant: 'link',
          size: 'sm',
          class: 'text-destructive h-auto px-1',
          disabled: row.token === currentToken,
          onClick: () => confirmKickout(row),
        },
        { default: () => '强退' },
      );
    },
  },
];

// ==================== 加载数据 ====================
async function loadTableData() {
  tableLoading.value = true;
  try {
    const res = await onlineApi.list({
      page: tablePagination.value.page,
      pageSize: tablePagination.value.pageSize,
      keyword: searchForm.value.keyword,
    });
    tableData.value = res.records;
    tablePagination.value.itemCount = res.total;
  } finally {
    tableLoading.value = false;
  }
}

// ==================== 搜索 ====================
function handleSearch() {
  tablePagination.value.page = 1;
  loadTableData();
}

// ==================== 强退单个用户 ====================
async function confirmKickout(row: OnlineUser) {
  const ok = await confirmAction.value?.ask({
    title: '强退用户',
    description: `确定要强退用户"${row.loginName}"吗？`,
    tone: 'destructive',
  });
  if (!ok) return;
  await handleKickout(row.token);
}

async function handleKickout(token: string) {
  await onlineApi.kickout(token);
  message.success('强退成功');
  loadTableData();
  selectedRowKeys.value = [];
}

// ==================== 批量强退 ====================
async function handleBatchKickout() {
  if (selectedRowKeys.value.length === 0) {
    message.warning('请选择要强退的用户');
    return;
  }

  const ok = await confirmAction.value?.ask({
    title: '批量强退',
    description: `确定要强退选中的 ${selectedRowKeys.value.length} 个用户吗？`,
    tone: 'destructive',
  });
  if (!ok) return;
  await onlineApi.batchKickout(selectedRowKeys.value);
  message.success('批量强退成功');
  loadTableData();
  selectedRowKeys.value = [];
}

// ==================== 行选择 ====================
function handleCheck(rowKeys: string[]) {
  selectedRowKeys.value = rowKeys;
}

// ==================== 初始化 ====================
onMounted(() => {
  loadTableData();
});
</script>

<template>
  <Page>
    <ConfirmAction ref="confirmAction" />
    <div class="h-full bg-background p-4">
      <!-- 搜索和操作栏 -->
      <div class="flex items-center justify-between mb-4 gap-3">
        <div class="flex items-center gap-2">
          <NInput
            v-model:value="searchForm.keyword"
            placeholder="搜索用户名"
            clearable
            class="w-[240px]"
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <NIcon><SearchOutline /></NIcon>
            </template>
          </NInput>
          <Button type="button" @click="handleSearch">
            <IconifyIcon icon="lucide:search" class="mr-1 size-4" />
            查询
          </Button>
        </div>
      </div>

      <!-- 已选中提示和批量操作 -->
      <div
        v-if="selectedRowKeys.length > 0"
        class="flex items-center justify-between mb-4 p-3 bg-primary/10 rounded"
      >
        <span class="text-sm">
          已选中
          <span class="font-bold text-primary">{{
            selectedRowKeys.length
          }}</span>
          项
        </span>
        <Button
          type="button"
          size="sm"
          variant="destructive"
          @click="handleBatchKickout"
        >
          <IconifyIcon icon="lucide:user-x" class="mr-1 size-4" />
          批量强退
        </Button>
      </div>

      <!-- 数据表格 -->
      <NDataTable
        :columns="tableColumns"
        :data="tableData"
        :loading="tableLoading"
        :row-key="(row) => row.token"
        :pagination="tablePagination"
        :checked-row-keys="selectedRowKeys"
        scroll-x="1200px"
        @update:checked-row-keys="handleCheck"
      />
    </div>
  </Page>
</template>

<style lang="scss" scoped></style>
