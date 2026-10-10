<script setup lang="ts">
import type { DataTableColumns } from 'naive-ui';

import type { DeptResult } from '#/api/system/dept';

import { h, ref, watch } from 'vue';

import { Page } from '@vben/common-ui';
import { IconifyIcon } from '@vben/icons';
import { $t } from '@vben/locales';

import { useDebounceFn } from '@vueuse/core';
import { NBadge, NDataTable, NInput, NTag, useMessage } from 'naive-ui';

import { deptApi } from '#/api/system/dept';
import { useDownload } from '#/hooks/app/useDownload';
import { useUserStore } from '#/store/user';
import { Button } from '#/ui/button';
import {
  ConfirmAction,
  type ConfirmActionExpose,
} from '#/ui-patterns/confirm-action';
import { ToolbarActions } from '#/ui-patterns/toolbar-actions';

import EditModal from './dept-drawer.vue';

// 搜索表单
const searchForm = ref({
  name: '',
});

const message = useMessage();
const confirmAction = ref<ConfirmActionExpose | null>(null);
const userStore = useUserStore();
const loading = ref(false);
const tableData = ref<DeptResp[]>([]);
const expandedRowKeys = ref<string[]>([]);

// 创建列配置
const createColumns = (): DataTableColumns<DeptResult> => {
  return [
    { title: $t('system.dept.name'), key: 'name', align: 'left', width: 300 },
    { title: $t('system.dept.code'), key: 'code', align: 'left', width: 140 },
    {
      title: $t('system.dept.status'),
      key: 'status',
      align: 'center',
      width: 80,
      render: (row) => {
        return h(
          'span',
          {
            style: { display: 'inline-flex', alignItems: 'center', gap: '6px' },
          },
          [
            h(NBadge, {
              dot: true,
              color:
                row.status === 1
                  ? 'oklch(76.8% 0.233 130.85)'
                  : 'oklch(50.5% 0.213 27.518)',
            }),
            row.status === 1 ? $t('common.enabled') : $t('common.disabled'),
          ],
        );
      },
    },
    {
      title: $t('system.dept.description'),
      key: 'description',
      align: 'left',
      ellipsis: { tooltip: true },
    },
    {
      title: $t('pages.common.operation'),
      key: 'action',
      align: 'center',
      width: 150,
      fixed: 'right',
      render: (row) => {
        const actions = [];
        if (userStore.hasPermission('system:dept:update')) {
          actions.push(
            h(
              Button,
              {
                type: 'button',
                variant: 'ghost',
                size: 'icon',
                onClick: () => handleEdit(row),
              },
              {
                default: () =>
                  h(IconifyIcon, { icon: 'lucide:pencil', class: 'size-4' }),
              },
            ),
          );
        }
        if (userStore.hasPermission('system:dept:delete')) {
          actions.push(
            h(
              Button,
              {
                type: 'button',
                variant: 'ghost',
                size: 'icon',
                class: 'text-destructive',
                onClick: () => handleDelete(row),
              },
              {
                default: () =>
                  h(IconifyIcon, { icon: 'lucide:trash-2', class: 'size-4' }),
              },
            ),
          );
        }
        return h(
          'div',
          { class: 'flex items-center justify-center gap-2' },
          actions,
        );
      },
    },
  ];
};

const columns = ref(createColumns());

// 加载数据
async function loadData() {
  try {
    loading.value = true;
    const res = await deptApi.tree({ keyword: searchForm.value.name });
    tableData.value = res;
    // 默认展开所有节点
    if (res.length > 0) {
      expandAllNodes(res);
    }
  } catch (error) {
    console.error('Failed to load dept data:', error);
  } finally {
    loading.value = false;
  }
}

// 递归展开所有节点
function expandAllNodes(data: DeptResult[]) {
  const keys: string[] = [];
  const traverse = (items: DeptResult[]) => {
    items.forEach((item) => {
      if (item.children && item.children.length > 0) {
        keys.push(item.id);
        traverse(item.children);
      }
    });
  };
  traverse(data);
  expandedRowKeys.value = keys;
}

// 折叠所有节点
function collapseAllNodes() {
  expandedRowKeys.value = [];
}

const editModalVisible = ref(false);
const editModalData = ref<DeptResp | undefined>(undefined);

const handleEdit = (record: DeptResp) => {
  editModalData.value = record;
  editModalVisible.value = true;
};

const handleAdd = () => {
  editModalData.value = undefined;
  editModalVisible.value = true;
};

const handleDelete = async (row: DeptResp) => {
  const ok = await confirmAction.value?.ask({
    title: $t('system.dept.deleteTitle'),
    description: $t('ui.actionMessage.deleteConfirm', [row.name]),
    confirmText: $t('common.confirm'),
    cancelText: $t('common.cancel'),
    tone: 'destructive',
  });
  if (!ok) return;
  try {
    await deptApi.delete(row.id);
    message.success($t('pages.common.deleteSuccess'));
    await loadData();
  } catch (error) {
    console.error('Failed to delete dept:', error);
  }
};

const handleExport = () => {
  useDownload(() =>
    deptApi.export({ description: searchForm.value.name || undefined }),
  );
};

// 搜索（防抖）
const handleSearch = useDebounceFn(() => {
  loadData();
}, 300);

// 监听搜索表单变化
watch(
  () => searchForm.value.name,
  () => {
    handleSearch();
  },
);

// 树列表折叠状态
const expanded = ref<boolean>(true);
const handleExpand = () => {
  expanded.value = !expanded.value;
  if (expanded.value) {
    expandAllNodes(tableData.value);
  } else {
    collapseAllNodes();
  }
};

// 初始加载
loadData();
</script>

<template>
  <Page auto-content-height>
    <ConfirmAction ref="confirmAction" />
    <div class="flex flex-col h-full bg-background p-4">
      <!-- 工具栏 -->
      <div class="flex items-center justify-between w-full pb-4 gap-4">
        <!-- 左侧搜索框 -->
        <div class="w-64">
          <NInput
            v-model:value="searchForm.name"
            :placeholder="$t('system.dept.name')"
            clearable
          >
            <template #prefix>
              <IconifyIcon icon="lucide:search" class="w-4 h-4" />
            </template>
          </NInput>
        </div>

        <!-- 右侧操作按钮 -->
        <ToolbarActions>
          <span v-access:code="['system:dept:create']">
            <Button type="button" variant="secondary" @click="handleAdd">
              <IconifyIcon icon="lucide:plus" class="mr-1 size-4" />
              {{ $t('pages.common.add') }}
            </Button>
          </span>
          <span v-access:code="['system:dept:export']">
            <Button type="button" variant="secondary" @click="handleExport">
              <IconifyIcon icon="lucide:download" class="mr-1 size-4" />
              {{ $t('pages.common.export') }}
            </Button>
          </span>
          <Button type="button" variant="secondary" @click="handleExpand">
            <IconifyIcon
              :icon="expanded ? 'lucide:chevrons-up' : 'lucide:chevrons-down'"
              class="mr-1 size-4"
            />
            {{
              expanded ? $t('pages.common.collapse') : $t('pages.common.expand')
            }}
          </Button>
        </ToolbarActions>
      </div>

      <!-- 表格 -->
      <div class="flex-1 overflow-hidden">
        <NDataTable
          :columns="columns"
          :data="tableData"
          :loading="loading"
          :row-key="(row: DeptResult) => row.id"
          :expanded-row-keys="expandedRowKeys"
          @update:expanded-row-keys="
            (keys) => (expandedRowKeys = keys as string[])
          "
          children-key="children"
          striped
          bordered
          scroll-x="800"
          size="small"
          flex-height
        />
      </div>
    </div>

    <EditModal
      v-model:visible="editModalVisible"
      :data="editModalData"
      @success="loadData()"
    />
  </Page>
</template>

<style scoped>
:deep(.n-data-table) {
  height: 100%;
}
</style>
