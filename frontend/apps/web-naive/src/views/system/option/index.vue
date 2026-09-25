<script setup lang="ts">
import type { DataTableColumns } from 'naive-ui';
import type { OptionResult } from '#/api/system/option';

import { h, onMounted, ref } from 'vue';
import { SearchOutline } from '@vicons/ionicons5';

import { Page } from '@vben/common-ui';
import {
  NButton,
  NDataTable,
  NIcon,
  NInput,
  NSpace,
  NTag,
  useDialog,
  useMessage,
} from 'naive-ui';

import { optionApi } from '#/api/system';

import OptionEditDrawer from './components/option-edit-drawer.vue';

const message = useMessage();
const dialog = useDialog();

const searchForm = ref({
  keyword: undefined as string | undefined,
});

const tableData = ref<OptionResult[]>([]);
const tableLoading = ref(false);
const pagination = ref({
  page: 1,
  pageSize: 10,
  itemCount: 0,
  showSizePicker: true,
  pageSizes: [10, 20, 50, 100],
  onChange: (page: number) => {
    pagination.value.page = page;
    loadTableData();
  },
  onUpdatePageSize: (pageSize: number) => {
    pagination.value.pageSize = pageSize;
    pagination.value.page = 1;
    loadTableData();
  },
});

const columns: DataTableColumns<OptionResult> = [
  {
    title: '序号',
    key: 'index',
    width: 80,
    fixed: 'left',
    render: (_row, index) =>
      (pagination.value.page - 1) * pagination.value.pageSize + index + 1,
  },
  {
    title: '选项类型',
    key: 'optionType',
    minWidth: 150,
    fixed: 'left',
  },
  {
    title: '选项值',
    key: 'value',
    minWidth: 120,
  },
  {
    title: '显示名称',
    key: 'label',
    minWidth: 150,
  },
  {
    title: '状态',
    key: 'enabled',
    width: 100,
    render(row) {
      return h(
        NTag,
        {
          type: row.enabled ? 'success' : 'error',
          size: 'small',
        },
        { default: () => (row.enabled ? '启用' : '禁用') },
      );
    },
  },
  {
    title: '排序',
    key: 'sort',
    width: 100,
  },
  {
    title: '描述',
    key: 'description',
    minWidth: 200,
    ellipsis: {
      tooltip: true,
    },
    render(row) {
      return row.description || '-';
    },
  },
  {
    title: '操作',
    key: 'action',
    width: 180,
    fixed: 'right',
    render(row) {
      return h(
        NSpace,
        { size: 'small' },
        {
          default: () => [
            h(
              NButton,
              {
                size: 'small',
                type: 'primary',
                text: true,
                onClick: () => handleEdit(row),
              },
              { default: () => '编辑' },
            ),
            h(
              NButton,
              {
                size: 'small',
                type: 'error',
                text: true,
                onClick: () => handleDelete(row),
              },
              { default: () => '删除' },
            ),
            h(
              NButton,
              {
                size: 'small',
                text: true,
                onClick: () => handleClearCache(row),
              },
              { default: () => '清除缓存' },
            ),
          ],
        },
      );
    },
  },
];

async function loadTableData() {
  tableLoading.value = true;
  try {
    const res = await optionApi.page({
      page: pagination.value.page,
      pageSize: pagination.value.pageSize,
      keyword: searchForm.value.keyword || undefined,
    });

    tableData.value = res.records;
    pagination.value.itemCount = res.total;
  } catch (error) {
    console.error('加载选项数据失败:', error);
    message.error('加载数据失败');
  } finally {
    tableLoading.value = false;
  }
}

function handleSearch() {
  pagination.value.page = 1;
  loadTableData();
}

const editDrawerVisible = ref(false);
const editOptionData = ref<OptionResult | undefined>();

function handleAdd() {
  editOptionData.value = undefined;
  editDrawerVisible.value = true;
}

function handleEdit(row: OptionResult) {
  editOptionData.value = row;
  editDrawerVisible.value = true;
}

function handleEditSuccess() {
  loadTableData();
}

function handleDelete(row: OptionResult) {
  dialog.warning({
    title: '删除确认',
    content: `确定要删除选项 "${row.label}" 吗？此操作不可恢复！`,
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        await optionApi.delete([row.id]);
        message.success('删除成功');
        loadTableData();
      } catch (error) {
        console.error('删除选项失败:', error);
        message.error('删除失败');
      }
    },
  });
}

function handleClearCache(row: OptionResult) {
  dialog.info({
    title: '清除缓存',
    content: `确定要清除选项类型 "${row.optionType}" 的缓存吗？`,
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        await optionApi.clearCache(row.optionType);
        message.success('缓存清除成功');
      } catch (error) {
        console.error('清除缓存失败:', error);
        message.error('清除缓存失败');
      }
    },
  });
}

onMounted(() => {
  loadTableData();
});
</script>

<template>
  <Page auto-content-height>
    <div class="flex flex-col bg-background w-full p-4">
      <div class="flex items-center justify-between gap-2 pb-4">
        <div class="w-64">
          <NInput
            v-model:value="searchForm.keyword"
            placeholder="搜索选项类型/选项值/显示名称"
            clearable
            class="w-[340px]"
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <NIcon><SearchOutline /></NIcon>
            </template>
          </NInput>
        </div>
        <NSpace>
          <NButton type="primary" @click="handleAdd">新建选项</NButton>
        </NSpace>
      </div>
      <NDataTable
        :columns="columns"
        :data="tableData"
        :loading="tableLoading"
        :row-key="(row) => row.id"
        :pagination="pagination"
        :scroll-x="1200"
        remote
      />
    </div>
    <OptionEditDrawer
      v-model:visible="editDrawerVisible"
      :option-data="editOptionData"
      @success="handleEditSuccess"
    />
  </Page>
</template>
