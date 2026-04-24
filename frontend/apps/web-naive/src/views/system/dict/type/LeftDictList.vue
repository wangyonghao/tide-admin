<script setup lang="ts">
import type {
  VxeGridListeners,
  VxeTableGridOptions,
} from '#/adapter/vxe-table';
import type { DictResp } from '#/api';

import { ref } from 'vue';

import { Card, CardContent, useVbenModal } from '@vben/common-ui';
import { IconifyIcon } from '@vben/icons';

import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  VbenButton,
} from '@vben-core/shadcn-ui';

import { NModal, useMessage } from 'naive-ui';

import { useVbenVxeGrid } from '#/adapter/vxe-table';
import { clearDictCache, deleteDict, listDict } from '#/api';

import { useDictColumns, useDictSearchFormFields } from '../data';
import { emitter } from '../mitt';
import dictTypeModal from './dict-type-modal.vue';

const [Grid, gridApi] = useVbenVxeGrid({
  formOptions: {
    schema: useDictSearchFormFields(),
    submitOnChange: true,
    showCollapseButton: false,
    wrapperClass: 'grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4',
  },
  gridEvents: {
    cellClick: ({ row }) => {
      emitter.emit('rowClick', row.id);
    },
  } as VxeGridListeners<DictResp>,
  gridOptions: {
    columns: useDictColumns(),
    border: true,
    height: 'auto',
    keepSource: true,
    pagerConfig: {
      enabled: false,
    },
    columnConfig: {
      resizable: true,
    },
    proxyConfig: {
      response: {
        list: 'list',
      },
      ajax: {
        query: async ({ page }, formValues) => {
          const res = await listDict({
            page: page.currentPage,
            size: page.pageSize,
            ...formValues,
          });
          if (res.length > 0) {
            emitter.emit('rowClick', res[0]?.id);
          }
          return { list: res, total: res.length };
        },
      },
    },
    rowConfig: {
      keyField: 'id',
      isHover: true,
    },
    checkboxConfig: {
      highlight: true,
    },
    toolbarConfig: {
      custom: true,
      export: false,
      refreshOptions: { code: 'query' },
      refresh: true,
      search: true,
      zoom: true,
      zoomOptions: {},
    },
  } as VxeTableGridOptions<DictResp>,
});

const [DictTypeModal, modalApi] = useVbenModal({
  connectedComponent: dictTypeModal,
});

const handleEdit = (record: DictResp) => {
  modalApi.setData({ id: record.id });
  modalApi.open();
};

const handleAdd = () => {
  modalApi.setData({});
  modalApi.open();
};

const deleteDialogVisible = ref(false);
const deleteRow = ref<DictResp | null>(null);
const message = useMessage();

const showDeleteDialog = (row: DictResp) => {
  deleteRow.value = row;
  deleteDialogVisible.value = true;
};

const handleDelete = async () => {
  if (!deleteRow.value) return;

  try {
    await deleteDict(deleteRow.value.id);
    message.success('删除成功');
    await gridApi.query();
    deleteDialogVisible.value = false;
    deleteRow.value = null;
    return true;
  } catch {
    return false;
  }
};

const clearDictNames = ref('');
const centerDialogVisible = ref(false);
// 清除缓存
const handleClearCache = () => {
  const selectData = gridApi.grid.getCheckboxRecords();
  if (selectData.length === 0) {
    return message.warning('请先选择字典');
  }
  clearDictNames.value = selectData.map((row: DictResp) => row.name).join(',');
  centerDialogVisible.value = true;
};

const clearCache = async () => {
  const selectData = gridApi.grid.getCheckboxRecords();
  for (const item of selectData) {
    await clearDictCache(item.code);
  }
  message.success('清除成功');
  centerDialogVisible.value = false;
};
</script>
<template>
  <Card class="h-full">
    <CardContent class="h-full overflow-auto">
      <Grid :table-title="$t('system.dict.list')">
        <template #toolbar-tools>
          <div class="flex items-center gap-2">
            <span v-access:code="['system:dict:add']">
              <VbenButton @click="handleAdd">
                {{ $t('pages.common.add') }}
              </VbenButton>
            </span>
            <span v-access:code="['system:dict:item:clearCache']">
              <VbenButton variant="destructive" @click="handleClearCache">
                {{ $t('pages.common.clearCache') }}
              </VbenButton>
            </span>
          </div>
        </template>
        <template #isBuiltin="{ row }">
          {{ row.isBuiltin ? $t('common.yes') : $t('common.no') }}
        </template>
        <template #action="{ row }">
          <div class="flex items-center gap-2">
            <span v-access:code="['system:dict:update']">
              <VbenButton variant="ghost" size="icon" @click="handleEdit(row)">
                <IconifyIcon icon="lucide:pencil" class="w-4 h-4" />
              </VbenButton>
            </span>
            <span v-access:code="['system:dict:delete']">
              <VbenButton
                variant="ghost"
                size="icon"
                :disabled="row.isBuiltin"
                @click="showDeleteDialog(row)"
              >
                <IconifyIcon
                  icon="lucide:trash-2"
                  class="w-4 h-4 text-destructive"
                />
              </VbenButton>
            </span>
          </div>
        </template>
      </Grid>
    </CardContent>
    <DictTypeModal @reload="gridApi.query()" />

    <!-- Delete Confirmation Dialog -->
    <Dialog
      :open="deleteDialogVisible"
      @update:open="(val) => (deleteDialogVisible = val)"
    >
      <DialogContent class="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle>确认删除</DialogTitle>
          <DialogDescription>
            确认删除字典「{{ deleteRow?.name }}」？
          </DialogDescription>
        </DialogHeader>
        <DialogFooter>
          <VbenButton variant="outline" @click="deleteDialogVisible = false">
            取消
          </VbenButton>
          <VbenButton variant="destructive" @click="handleDelete">
            确认
          </VbenButton>
        </DialogFooter>
      </DialogContent>
    </Dialog>

    <div v-access:code="['system:dict:item:clearCache']">
      <NModal
        v-model:show="centerDialogVisible"
        preset="dialog"
        title="清空缓存"
        :positive-text="$t('common.confirm')"
        :negative-text="$t('common.cancel')"
        @positive-click="clearCache"
      >
        是否确定清除字典「{{ clearDictNames }}」缓存？
      </NModal>
    </div>
  </Card>
</template>
<style lang="scss" scoped></style>
