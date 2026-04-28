<script setup lang="ts">
import type { DataTableColumns } from 'naive-ui';
import type { OperationLogResp } from '#/api/monitor/log';

import { h, onMounted, ref } from 'vue';

import { IconifyIcon } from '@vben/icons';

import { SearchOutline } from '@vicons/ionicons5';
import {
  NButton,
  NDataTable,
  NDatePicker,
  NDrawer,
  NDrawerContent,
  NIcon,
  NInput,
  NTag,
  useMessage,
} from 'naive-ui';

import { logApi } from '#/api/monitor/log';

const message = useMessage();

// ==================== 搜索表单 ====================
const searchForm = ref({
  operatorName: '',
  operation: '',
  operatorIp: '',
  createTime: null as [number, number] | null,
});

// ==================== 表格数据 ====================
const tableData = ref<OperationLogResp[]>([]);
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

// ==================== 表格列定义 ====================
const tableColumns: DataTableColumns<OperationLogResp> = [
  { title: '序号', key: 'index', width: 60,
    render: (_row, index) =>
      (tablePagination.value.page - 1) * tablePagination.value.pageSize + index + 1,
  },
  { title: '操作时间', key: 'operateTime', minWidth: 160, sorter: 'default', },
  { title: '操作人', key: 'operatorName', minWidth: 120, },
  { title: '操作类型', key: 'operation', minWidth: 120,
    render(row) {
      const typeMap: Record<string, { type: 'default' | 'error' | 'info' | 'success' | 'warning'; label: string }> = {
        create: { type: 'success', label: '新增' },
        update: { type: 'info', label: '修改' },
        delete: { type: 'error', label: '删除' },
        login: { type: 'success', label: '登录' },
        logout: { type: 'warning', label: '登出' },
        send: { type: 'info', label: '发送' },
      };
      const config = typeMap[row.operation] || { type: 'default', label: row.operation };
      return h(NTag, { type: config.type, size: 'small' }, { default: () => config.label });
    },
  },
  { title: '业务对象', key: 'objectType', minWidth: 120, },
  { title: 'IP地址', key: 'operatorIp', minWidth: 140, },
  { title: '操作地点', key: 'operatorLocation', minWidth: 150, },
  { title: '操作', key: 'action', width: 100, fixed: 'right',
    render(row) {
      return h(
        NButton,
        {
          size: 'small',
          type: 'primary',
          text: true,
          onClick: () => handleDetail(row),
        },
        { default: () => '详情' },
      );
    },
  },
];

// ==================== 加载数据 ====================
async function loadTableData() {
  tableLoading.value = true;
  try {
    const createTime = searchForm.value.createTime
      ? [
          new Date(searchForm.value.createTime[0]).toISOString().slice(0, 19).replace('T', ' '),
          new Date(searchForm.value.createTime[1]).toISOString().slice(0, 19).replace('T', ' '),
        ]
      : undefined;

    const res = await logApi.list({
      page: tablePagination.value.page,
      size: tablePagination.value.pageSize,
      operatorName: searchForm.value.operatorName || undefined,
      operation: searchForm.value.operation || undefined,
      operatorIp: searchForm.value.operatorIp || undefined,
      createTime,
      sort: ['operateTime,desc'],
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
    operatorName: '',
    operation: '',
    operatorIp: '',
    createTime: null,
  };
  handleSearch();
}

// ==================== 导出 ====================
function handleExport() {
  const createTime = searchForm.value.createTime
    ? [
        new Date(searchForm.value.createTime[0]).toISOString().slice(0, 19).replace('T', ' '),
        new Date(searchForm.value.createTime[1]).toISOString().slice(0, 19).replace('T', ' '),
      ]
    : undefined;

  logApi.exportOperationLog({
    operatorName: searchForm.value.operatorName || undefined,
    operation: searchForm.value.operation || undefined,
    operatorIp: searchForm.value.operatorIp || undefined,
    createTime,
  });
}

// ==================== 详情抽屉 ====================
const detailDrawerVisible = ref(false);
const detailData = ref<any>(null);
const detailLoading = ref(false);

async function handleDetail(row: OperationLogResp) {
  detailDrawerVisible.value = true;
  detailLoading.value = true;
  try {
    detailData.value = await logApi.detail(row.id);
  } catch (error) {
    console.error('加载日志详情失败:', error);
    message.error('加载详情失败');
  } finally {
    detailLoading.value = false;
  }
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
      <div class="flex items-center gap-2 flex-wrap">
        <NInput
          v-model:value="searchForm.operatorName"
          placeholder="操作人"
          clearable
          class="w-[180px]"
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <NIcon><SearchOutline /></NIcon>
          </template>
        </NInput>
        <NInput
          v-model:value="searchForm.operation"
          placeholder="操作类型"
          clearable
          class="w-[180px]"
          @keyup.enter="handleSearch"
        />
        <NInput
          v-model:value="searchForm.operatorIp"
          placeholder="IP地址"
          clearable
          class="w-[180px]"
          @keyup.enter="handleSearch"
        />
        <NDatePicker
          v-model:value="searchForm.createTime"
          type="datetimerange"
          clearable
          class="w-[360px]"
          format="yyyy-MM-dd HH:mm:ss"
        />
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

    <!-- 详情抽屉 -->
    <NDrawer v-model:show="detailDrawerVisible" :width="600">
      <NDrawerContent title="操作日志详情" closable>
        <div v-if="detailLoading" class="flex justify-center items-center h-64">
          加载中...
        </div>
        <div v-else-if="detailData" class="space-y-4">
          <div class="grid grid-cols-2 gap-4">
            <div>
              <div class="text-sm text-gray-500 mb-1">操作人</div>
              <div class="font-medium">{{ detailData.operatorName }}</div>
            </div>
            <div>
              <div class="text-sm text-gray-500 mb-1">操作时间</div>
              <div class="font-medium">{{ detailData.operateTime }}</div>
            </div>
            <div>
              <div class="text-sm text-gray-500 mb-1">操作类型</div>
              <div class="font-medium">{{ detailData.operation }}</div>
            </div>
            <div>
              <div class="text-sm text-gray-500 mb-1">业务对象</div>
              <div class="font-medium">{{ detailData.objectType }}</div>
            </div>
            <div>
              <div class="text-sm text-gray-500 mb-1">IP地址</div>
              <div class="font-medium">{{ detailData.operatorIp }}</div>
            </div>
            <div>
              <div class="text-sm text-gray-500 mb-1">操作地点</div>
              <div class="font-medium">{{ detailData.operatorLocation }}</div>
            </div>
            <div class="col-span-2">
              <div class="text-sm text-gray-500 mb-1">状态</div>
              <NTag :type="detailData.status === 'success' ? 'success' : 'error'">
                {{ detailData.status === 'success' ? '成功' : '失败' }}
              </NTag>
            </div>
            <div v-if="detailData.remark" class="col-span-2">
              <div class="text-sm text-gray-500 mb-1">备注</div>
              <div class="font-medium">{{ detailData.remark }}</div>
            </div>
            <div v-if="detailData.extra" class="col-span-2">
              <div class="text-sm text-gray-500 mb-1">额外信息</div>
              <pre class="bg-gray-100 dark:bg-gray-800 p-3 rounded text-sm overflow-auto">{{ JSON.stringify(JSON.parse(detailData.extra), null, 2) }}</pre>
            </div>
          </div>
        </div>
      </NDrawerContent>
    </NDrawer>
  </div>
</template>

<style lang="scss" scoped></style>
