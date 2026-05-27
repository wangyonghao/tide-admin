<script setup lang="ts">
import type { VbenFormSchema } from '@vben/common-ui';

import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { SmsLogQuery, SmsLogResp } from '#/api/system/sms-log';

import { Page } from '@vben/common-ui';
import { $t } from '@vben/locales';

import { NButton, useMessage, NPopconfirm, NSpace } from 'naive-ui';

import { useVbenVxeGrid } from '#/adapter/vxe-table';
import { smsLogApi} from '#/api/system/sms-log';
import { useDownload } from '#/hooks/app/useDownload';

const message = useMessage();

function useSmsLogGridSearchFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'configId',
      label: $t('system.smsLog.configId'),
      component: 'Input',
    },
    {
      fieldName: 'phone',
      label: $t('system.smsLog.phone'),
      component: 'Input',
    },
    {
      fieldName: 'status',
      label: $t('system.smsLog.status'),
      component: 'Select',
      componentProps: {
        placeholder: $t('system.smsLog.status'),
        options: [
          { type: 'success', label: $t('common.success'), value: 1 },
          { type: 'danger', label: $t('common.failed'), value: 0 },
        ],
      },
    },
  ];
}

// Table 字段配置
function useSmsLogGridFieldColumns(): VxeTableGridOptions['columns'] {
  return [
    { type: 'seq', width: 70, fixed: 'left' },
    {
      field: 'phone',
      title: $t('system.smsLog.phone'),
      align: 'center',
    },
    {
      field: 'params',
      title: $t('system.smsLog.params'),
      align: 'center',
    },
    {
      field: 'status',
      title: $t('system.smsLog.status'),
      align: 'center',
      cellRender: {
        name: 'CellSuccErrTag',
      },
    },
    {
      field: 'resMsg',
      title: $t('system.smsLog.resMsg'),
      align: 'center',
    },
    {
      field: 'createUserString',
      title: $t('system.smsLog.createUserString'),
      align: 'center',
    },
    {
      field: 'createTime',
      title: $t('system.smsLog.createTime'),
      align: 'center',
    },
    {
      align: 'center',
      field: 'action',
      fixed: 'right',
      slots: { default: 'action' },
      title: $t('common.operation'),
      width: 150,
    },
  ];
}

const [TableGrid, tableGridApi] = useVbenVxeGrid({
  formOptions: {
    schema: useSmsLogGridSearchFormSchema(),
    submitOnChange: true,
    showCollapseButton: false,
    wrapperClass: 'grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4',
  },
  gridOptions: {
    columns: useSmsLogGridFieldColumns(),
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
          const res = await smsLogApi.list({
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
      isCurrent: true,
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
  } as VxeTableGridOptions<SmsLogResp>,
});

const handleDelete = async (row: SmsLogResp) => {
  try {
    await smsLogApi.delete(row.id);
    message.success($t('pages.common.deleteSuccess'));
    await tableGridApi.query();
    return true;
  } catch {
    return false;
  }
};

const handleExport = () => {
  useDownload(async () =>
    exportSmsLog(await tableGridApi.formApi.getValues<SmsLogQuery>()),
  );
};
</script>

<template>
  <Page auto-content-height>
    <TableGrid :table-title="$t('system.smsLog.listTitle')">
      <template #toolbar-tools>
        <NSpace>
          <span v-access:code="['system:smsLog:export']">
            <NButton type="error" @click="handleExport">
              {{ $t('pages.common.export') }}
            </NButton>
          </span>
        </NSpace>
      </template>
      <template #action="{ row }">
        <NSpace>
          <span v-access:code="['system:smsLog:delete']">
            <NPopconfirm
              :title="$t('ui.actionMessage.deleteConfirm', [row.phone])"
              positive-text="确认"
              negative-text="取消"
              @positive-click="handleDelete(row)"
            >
              <template #trigger>
                <NButton type="error" text>
                  {{ $t('pages.common.delete') }}
                </NButton>
              </template>
            </NPopconfirm>
          </span>
        </NSpace>
      </template>
    </TableGrid>
  </Page>
</template>
<style lang="scss" scoped></style>
