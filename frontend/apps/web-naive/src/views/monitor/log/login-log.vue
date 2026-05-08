<script setup lang="ts">
import type { DataTableColumns } from 'naive-ui';
import type { LoginLogResult } from '#/api/system/login-log';

import { h, onMounted, ref } from 'vue';

import { $t } from '#/locales';
import { IconifyIcon } from '@vben/icons';
import { SearchOutline } from '@vicons/ionicons5';
import {
  NButton,
  NDataTable,
  NDatePicker,
  NIcon,
  NInput,
  NSelect,
  NTag,
} from 'naive-ui';

import { loginLogApi } from '#/api/system/login-log';

// ==================== 搜索表单 ====================
const searchForm = ref({
  username: '',
  ipAddress: '',
  loginStatus: null as 'SUCCESS' | 'FAILURE' | null,
  loginTime: null as [number, number] | null,
});

// ==================== 表格数据 ====================
const tableData = ref<LoginLogResult[]>([]);
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

// ==================== 登录状态选项 ====================
const loginStatusOptions = [
  { label: '成功', value: 'SUCCESS' },
  { label: '失败', value: 'FAILURE' },
];

// ==================== 表格列定义 ====================
const tableColumns: DataTableColumns<LoginLogResult> = [
  { title: '序号', key: 'index', width: 60,
    render: (_row, index) => (tablePagination.value.page - 1) * tablePagination.value.pageSize + index + 1,
  },
  { title: '用户名', key: 'username', minWidth: 100, },
  { title: $t('monitor.loginLog.loginTime'), key: 'loginTime', minWidth: 160, sorter: 'default', },

  { title: $t('monitor.loginLog.loginStatus'), key: 'loginStatus', minWidth: 100,
    render(row) {
      return h(
        NTag,
        {
          type: row.loginStatus === 'SUCCESS' ? 'success' : 'error',
          size: 'small',
        },
        { default: () => (row.loginStatus === 'SUCCESS' ? '成功' : '失败') },
      );
    },
  },
  { title: $t('monitor.loginLog.ipAddress'), key: 'ipAddress', minWidth: 100, },
  { title: $t('monitor.loginLog.location'), key: 'location', minWidth: 120, },
  { title: $t('monitor.loginLog.deviceType'), key: 'deviceType', minWidth: 100,
    render(row) {
      const deviceTypeMap: Record<string, string> = {
        MOBILE: '应用程序',
        WEB: '网页端',
        OTHER: '其他',
      };
      return deviceTypeMap[row.deviceType] || row.deviceType;
    },
  },
  { title: $t('monitor.loginLog.browser'), key: 'browser', minWidth: 150, },
  { title: $t('monitor.loginLog.os'), key: 'os', minWidth: 120, },
  { title: $t('monitor.loginLog.failureReason'), key: 'failureReason', minWidth: 180,
    render(row) { return row.failureReason || '-'; },
  },
];

// ==================== 加载数据 ====================
async function loadTableData() {
  tableLoading.value = true;
  try {
    let loginTimeStart: string | undefined;
    let loginTimeEnd: string | undefined;

    if (searchForm.value.loginTime) {
      loginTimeStart = new Date(searchForm.value.loginTime[0]) .toISOString() .slice(0, 19) .replace('T', ' ');
      loginTimeEnd = new Date(searchForm.value.loginTime[1]) .toISOString() .slice(0, 19) .replace('T', ' ');
    }

    const res = await loginLogApi.list({
      page: tablePagination.value.page,
      size: tablePagination.value.pageSize,
      username: searchForm.value.username || undefined,
      ipAddress: searchForm.value.ipAddress || undefined,
      loginStatus: searchForm.value.loginStatus || undefined,
      loginTimeStart,
      loginTimeEnd,
    });

    tableData.value = res.list;
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

// ==================== 重置 ====================
function handleReset() {
  searchForm.value = {
    username: '',
    ipAddress: '',
    loginStatus: null,
    loginTime: null,
  };
  handleSearch();
}

// ==================== 导出 ====================
function handleExport() {
  let loginTimeStart: string | undefined;
  let loginTimeEnd: string | undefined;

  if (searchForm.value.loginTime) {
    loginTimeStart = new Date(searchForm.value.loginTime[0])
      .toISOString()
      .slice(0, 19)
      .replace('T', ' ');
    loginTimeEnd = new Date(searchForm.value.loginTime[1])
      .toISOString()
      .slice(0, 19)
      .replace('T', ' ');
  }

  loginLogApi.export({
    username: searchForm.value.username || undefined,
    ipAddress: searchForm.value.ipAddress || undefined,
    loginStatus: searchForm.value.loginStatus || undefined,
    loginTimeStart,
    loginTimeEnd,
  });
}

// ==================== 初始化 ====================
onMounted(() => {
  loadTableData();
});
</script>

<template>
  <div class="h-full bg-background p-4">
    <!-- 搜索和操作栏 -->
    <div class="mb-4">
      <!-- 搜索表单 - 响应式网格布局 -->
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-3 mb-3">
        <NInput
          v-model:value="searchForm.username"
          placeholder="用户名"
          clearable
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <NIcon><SearchOutline /></NIcon>
          </template>
        </NInput>
        <NInput
          v-model:value="searchForm.ipAddress"
          placeholder="IP地址"
          clearable
          @keyup.enter="handleSearch"
        />
        <NSelect
          v-model:value="searchForm.loginStatus"
          :options="loginStatusOptions"
          placeholder="登录状态"
          clearable
        />
        <div class="sm:col-span-2 lg:col-span-1 xl:col-span-1">
          <NDatePicker
            v-model:value="searchForm.loginTime"
            type="datetimerange"
            clearable
            class="w-full"
            format="yyyy-MM-dd HH:mm:ss"
          />
        </div>
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
      scroll-x="1400px"
    />
  </div>
</template>

<style lang="scss" scoped></style>
