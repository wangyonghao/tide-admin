<script setup lang="ts">
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { TenantPackageResp } from '#/api/tenant/package';

import { Page, useVbenDrawer } from '@vben/common-ui';
import { $t } from '@vben/locales';

import { NButton, NPopconfirm, NSpace, NTag, useMessage } from 'naive-ui';

import { useVbenVxeGrid } from '#/adapter/vxe-table';
import { deleteTenantPackage, listTenantPackage } from '#/api/tenant/package';

import {
  usePackageGridFieldColumns,
  usePackageGridSearchFormSchema,
} from './PackageData';
import PackageEditDrawer from './PackageEditDrawer.vue';

const [TableGrid, tableGridApi] = useVbenVxeGrid({
  formOptions: {
    schema: usePackageGridSearchFormSchema(),
    submitOnChange: true,
    showCollapseButton: false,
    wrapperClass: 'grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4',
  },
  gridOptions: {
    columns: usePackageGridFieldColumns(),
    border: true,
    height: 'auto',
    keepSource: true,
    columnConfig: {
      resizable: true,
    },
    proxyConfig: {
      response: {
        list: 'list',
      },
      ajax: {
        query: async ({ page }, formValues) => {
          const res = await listTenantPackage({
            page: page.currentPage,
            size: page.pageSize,
            ...formValues,
          });
          return res;
        },
      },
    },
    rowConfig: {
      keyField: 'id',
      isHover: true,
    },
    toolbarConfig: {
      custom: true,
      export: false,
      refresh: true,
      refreshOptions: {
        code: 'query',
      },
      search: true,
      zoom: true,
      zoomOptions: {},
    },
  } as VxeTableGridOptions<TenantPackageResp>,
});

const message = useMessage();

const [EditorWindow, editorApi] = useVbenDrawer({
  connectedComponent: PackageEditDrawer,
  destroyOnClose: true,
});

const handleEdit = (record: TenantPackageResp) => {
  editorApi.setData(record);
  editorApi.open();
};

const handleAdd = () => {
  editorApi.setData({});
  editorApi.open();
};

const handleDelete = async (row: TenantPackageResp) => {
  try {
    await deleteTenantPackage(row.id);
    message.success($t('pages.common.deleteSuccess'));
    await tableGridApi.query();
    return true;
  } catch {
    return false;
  }
};
</script>

<template>
  <Page auto-content-height>
    <TableGrid :table-title="$t('tenant.package.listTitle')">
      <template #toolbar-tools>
        <NSpace>
          <span v-access:code="['tenant:package:create']">
            <NButton type="primary" @click="handleAdd">
              {{ $t('pages.common.add') }}
            </NButton>
          </span>
        </NSpace>
      </template>
      <template #status="{ row }">
        <NTag v-if="row.status === 1" type="success">
          {{ $t('common.enabled') }}
        </NTag>
        <NTag v-else type="error">
          {{ $t('common.disabled') }}
        </NTag>
      </template>
      <template #action="{ row }">
        <NSpace>
          <span v-access:code="['tenant:package:update']">
            <NButton type="primary" @click="handleEdit(row)" text>
              {{ $t('pages.common.edit') }}
            </NButton>
          </span>
          <span v-access:code="['tenant:package:delete']">
            <NPopconfirm
              :positive-text="$t('common.confirm')"
              :negative-text="$t('common.cancel')"
              @positive-click="handleDelete(row)"
            >
              <template #trigger>
                <NButton type="error" text>
                  {{ $t('pages.common.delete') }}
                </NButton>
              </template>
              {{ $t('ui.actionMessage.deleteConfirm', [row.name]) }}
            </NPopconfirm>
          </span>
        </NSpace>
      </template>
    </TableGrid>
    <EditorWindow @success="tableGridApi.query()" />
  </Page>
</template>
<style lang="scss" scoped></style>
