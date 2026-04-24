<script setup lang="ts">
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { DictItemResp } from '#/api';

import { ref } from 'vue';

import { Card, CardContent, useVbenDrawer } from '@vben/common-ui';
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

import { NTag, useMessage } from 'naive-ui';

import { useVbenVxeGrid } from '#/adapter/vxe-table';
import { deleteDictItem, listDictItem } from '#/api';

import { useDictItemColumns, useDictItemSearchFormFields } from '../data';
import { emitter } from '../mitt';
import dictItem from './dictItem.vue';

// 字典id
const dictId = ref('');
const message = useMessage();

const [Grid, gridApi] = useVbenVxeGrid({
  formOptions: {
    schema: useDictItemSearchFormFields(),
    submitOnChange: true,
    resetButtonOptions: {
      show: false,
    },
    showCollapseButton: false,
    wrapperClass: 'grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4',
  },
  gridOptions: {
    columns: useDictItemColumns(),
    border: true,
    height: 'auto',
    keepSource: true,
    columnConfig: {
      resizable: true,
    },
    proxyConfig: {
      autoLoad: false,
      ajax: {
        query: async ({ page }, formValues) => {
          return await listDictItem({
            page: page.currentPage,
            size: page.pageSize,
            sort: 'createTime,desc',
            ...formValues,
          });
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
  } as VxeTableGridOptions<DictItemResp>,
});
emitter.on('rowClick', async (value) => {
  await gridApi.formApi.setFieldValue('dictId', value);
  dictId.value = value;
});

const [FormDrawer, formDrawerApi] = useVbenDrawer({
  connectedComponent: dictItem,
  destroyOnClose: true,
});

const handleEdit = (record: DictItemResp) => {
  formDrawerApi.setData({ id: record.id });
  formDrawerApi.open();
};

const handleAdd = () => {
  formDrawerApi.setData({ dictId: dictId.value });
  formDrawerApi.open();
};

const deleteDialogVisible = ref(false);
const deleteRow = ref<DictItemResp | null>(null);

const showDeleteDialog = (row: DictItemResp) => {
  deleteRow.value = row;
  deleteDialogVisible.value = true;
};

const handleDelete = async () => {
  if (!deleteRow.value) return;

  try {
    await deleteDictItem(deleteRow.value.id);
    message.success('删除成功');
    await gridApi.query();
    deleteDialogVisible.value = false;
    deleteRow.value = null;
    return true;
  } catch {
    return false;
  }
};

const onSuccess = () => {
  gridApi.query();
};
</script>
<template>
  <Card class="h-full">
    <CardContent class="h-full overflow-auto">
      <Grid :table-title="$t('system.dictItem.list')">
        <template #toolbar-tools>
          <div class="flex items-center gap-2">
            <span v-access:code="['system:dict:add']">
              <VbenButton @click="handleAdd">
                {{ $t('pages.common.add') }}
              </VbenButton>
            </span>
          </div>
        </template>
        <template #value="{ row }">
          <NTag :type="row.color">{{ row.value }}</NTag>
        </template>
        <template #status="{ row }">
          {{ row.status ? $t('common.enabled') : $t('common.disable') }}
        </template>
        <template #action="{ row }">
          <div class="flex items-center gap-2">
            <span v-access:code="['system:dictItem:create']">
              <VbenButton variant="ghost" size="icon" @click="handleEdit(row)">
                <IconifyIcon icon="lucide:pencil" class="w-4 h-4" />
              </VbenButton>
            </span>
            <span v-access:code="['system:dictItem:delete']">
              <VbenButton
                variant="ghost"
                size="icon"
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
    <FormDrawer @success="onSuccess" />

    <!-- Delete Confirmation Dialog -->
    <Dialog
      :open="deleteDialogVisible"
      @update:open="(val) => (deleteDialogVisible = val)"
    >
      <DialogContent class="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle>确认删除</DialogTitle>
          <DialogDescription>
            确认删除字典项「{{ deleteRow?.label }}」？
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
  </Card>
</template>
<style lang="scss" scoped></style>
