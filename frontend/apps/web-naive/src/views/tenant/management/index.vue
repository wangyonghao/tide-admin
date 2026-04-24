<script setup lang="ts">
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { TenantQuery, TenantResp } from '#/api';

import { Page, useVbenDrawer, useVbenModal } from '@vben/common-ui';
import { $t } from '@vben/locales';

import { NButton, NPopconfirm, NSpace, NTag, useMessage } from 'naive-ui';

import { useVbenVxeGrid } from '#/adapter/vxe-table';
import { deleteTenant, exportTenant, listTenant } from '#/api';
import { useDownload } from '#/hooks/app/useDownload';

import {
  useTenantGridFieldColumns,
  useTenantGridSearchFormSchema,
} from './TenantData';
import TenantEditDrawer from './TenantEditDrawer.vue';
import TenantResetAdminPwdModal from './TenantResetAdminPwdModal.vue';

const [TableGrid, tableGridApi] = useVbenVxeGrid({
  formOptions: {
    schema: useTenantGridSearchFormSchema(),
    submitOnChange: true,
    showCollapseButton: false,
    wrapperClass: 'grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4',
  },
  gridOptions: {
    columns: useTenantGridFieldColumns(),
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
          const res = await listTenant({
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
  } as VxeTableGridOptions<TenantResp>,
});

const message = useMessage();

const [EditorWindow, editorApi] = useVbenDrawer({
  connectedComponent: TenantEditDrawer,
  destroyOnClose: true,
});

const handleEdit = (record: TenantResp) => {
  editorApi.setData({ id: record.id });
  editorApi.open();
};

const handleAdd = () => {
  editorApi.setData({});
  editorApi.open();
};

const handleDelete = async (row: TenantResp) => {
  try {
    await deleteTenant(row.id);
    message.success($t('pages.common.deleteSuccess'));
    await tableGridApi.query();
    return true;
  } catch {
    return false;
  }
};

const handleExport = () => {
  useDownload(
    async () =>
      exportTenant(await tableGridApi.formApi.getValues<TenantQuery>()),
    `${$t('tenant.management.listTitle')}-${Date.now()}.xlsx`,
  );
};

const [ResetPwdWindow, resetPwdApi] = useVbenModal({
  connectedComponent: TenantResetAdminPwdModal,
  destroyOnClose: true,
});

const handleUpdateAdminUserPwd = (row: TenantResp) => {
  resetPwdApi.setData(row).open();
};
</script>

<template>
  <Page auto-content-height>
    <TableGrid :table-title="$t('tenant.management.listTitle')">
      <template #toolbar-tools>
        <NSpace>
          <span v-access:code="['tenant:management:create']">
            <NButton type="primary" @click="handleAdd">
              {{ $t('pages.common.add') }}
            </NButton>
          </span>
          <span v-access:code="['tenant:management:export']">
            <NButton type="error" @click="handleExport">
              {{ $t('pages.common.export') }}
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
          <span v-access:code="['tenant:management:update']">
            <NButton type="primary" @click="handleEdit(row)" text>
              {{ $t('pages.common.edit') }}
            </NButton>
          </span>
          <span v-access:code="['tenant:management:updateAdminUserPwd']">
            <NButton type="warning" @click="handleUpdateAdminUserPwd(row)" text>
              {{ $t('pages.common.resetAdminPwd') }}
            </NButton>
          </span>
          <span v-access:code="['tenant:management:delete']">
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
    <ResetPwdWindow />
  </Page>
</template>
<style lang="scss" scoped></style>
