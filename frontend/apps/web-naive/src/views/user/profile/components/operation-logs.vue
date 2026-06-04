<script setup lang="ts">
import { ref, onMounted, h } from 'vue';
import {
  NCard,
  NDataTable,
  NSpace,
  NButton,
  NTag,
  NDatePicker,
  NSelect,
  type DataTableColumns,
} from 'naive-ui';
import { $t } from '#/locales';
import { useUserStore } from '#/store/user';
import { loginLogApi, type LoginLogResult } from '#/api/auth/login-log';
import { message } from '#/adapter/naive';

const userStore = useUserStore();

// 表格数据
const tableData = ref<LoginLogResult[]>([]);
const loading = ref(false);
const pagination = ref({
  page: 1,
  pageSize: 10,
  itemCount: 0,
  showSizePicker: true,
  pageSizes: [10, 20, 50, 100],
});

// 筛选条件
const filters = ref({
  loginStatus: null as 'SUCCESS' | 'FAILURE' | null,
  dateRange: null as [number, number] | null,
});

// 状态选项
const statusOptions = [
  { label: $t('page.profile.logs.all'), value: null },
  { label: $t('page.profile.logs.success'), value: 'SUCCESS' },
  { label: $t('page.profile.logs.failure'), value: 'FAILURE' },
];

// 表格列定义
const columns: DataTableColumns<LoginLogResult> = [
  {
    title: $t('page.profile.logs.operationType'),
    key: 'loginStatus',
    width: 100,
    render: (row) => {
      return row.loginStatus === 'SUCCESS'
        ? h(NTag, { type: 'success', size: 'small' }, { default: () => $t('page.profile.logs.login') })
        : h(NTag, { type: 'error', size: 'small' }, { default: () => $t('page.profile.logs.failure') });
    },
  },
  {
    title: $t('page.profile.logs.operationTime'),
    key: 'loginTime',
    width: 180,
  },
  {
    title: $t('page.profile.logs.ipAddress'),
    key: 'ipAddress',
    width: 140,
  },
  {
    title: $t('page.profile.logs.location'),
    key: 'location',
    width: 150,
  },
  {
    title: $t('page.profile.logs.device'),
    key: 'device',
    render: (row) => `${row.browser} / ${row.os}`,
  },
  {
    title: $t('page.profile.logs.operationResult'),
    key: 'failureReason',
    width: 200,
    render: (row) => row.failureReason || '-',
  },
];

// 获取日志列表
const fetchLogs = async () => {
  try {
    loading.value = true;
    const params: any = {
      username: userStore.user?.username,
      page: pagination.value.page,
      size: pagination.value.pageSize,
    };

    if (filters.value.loginStatus) {
      params.loginStatus = filters.value.loginStatus;
    }

    if (filters.value.dateRange) {
      params.loginTimeStart = new Date(filters.value.dateRange[0]).toISOString();
      params.loginTimeEnd = new Date(filters.value.dateRange[1]).toISOString();
    }

    const res = await loginLogApi.lis$t(params);
    tableData.value = res.list || [];
    pagination.value.itemCount = res.total || 0;
  } catch (error) {
    console.error('获取日志失败:', error);
  } finally {
    loading.value = false;
  }
};

// 导出日志
const handleExport = async () => {
  try {
    const params: any = {
      username: userStore.user?.username,
    };

    if (filters.value.loginStatus) {
      params.loginStatus = filters.value.loginStatus;
    }

    if (filters.value.dateRange) {
      params.loginTimeStart = new Date(filters.value.dateRange[0]).toISOString();
      params.loginTimeEnd = new Date(filters.value.dateRange[1]).toISOString();
    }

    await loginLogApi.expor$t(params);
    message.success($t('page.profile.logs.exportSuccess'));
  } catch (error) {
    console.error('导出失败:', error);
  }
};

// 重置筛选
const handleReset = () => {
  filters.value = {
    loginStatus: null,
    dateRange: null,
  };
  pagination.value.page = 1;
  fetchLogs();
};

// 分页变化
const handlePageChange = (page: number) => {
  pagination.value.page = page;
  fetchLogs();
};

// 每页数量变化
const handlePageSizeChange = (pageSize: number) => {
  pagination.value.pageSize = pageSize;
  pagination.value.page = 1;
  fetchLogs();
};

onMounted(() => {
  fetchLogs();
});
</script>

<template>
  <div class="operation-logs">
    <!-- <h3 class="text-lg font-semibold mb-6">{{ $t('page.profile.tabs.logs') }}</h3> -->

    <!-- 筛选区域 -->
    <div class="mb-4">
      <NSpace :size="16" align="center">
        <NSelect
          v-model:value="filters.loginStatus"
          :options="statusOptions"
          :placeholder="$t('page.profile.logs.operationResult')"
          style="width: 150px"
          clearable
        />
        
        <NDatePicker
          v-model:value="filters.dateRange"
          type="daterange"
          :placeholder="$t('page.profile.logs.operationTime')"
          style="width: 300px"
          clearable
        />

        <NButton type="primary" @click="fetchLogs">
          {{ $t('page.profile.logs.filter') }}
        </NButton>

        <NButton @click="handleReset">
          {{ $t('common.reset') }}
        </NButton>

        <NButton @click="handleExport">
          {{ $t('page.profile.logs.export') }}
        </NButton>
      </NSpace>
    </div>

    <!-- 表格 -->
    <NDataTable
      :columns="columns"
      :data="tableData"
      :loading="loading"
      :pagination="pagination"
      :scroll-x="1000"
      @update:page="handlePageChange"
      @update:page-size="handlePageSizeChange"
    />
  </div>
</template>

<style lang="scss" scoped>
.operation-logs {
  max-width: 100%;
}
</style>
