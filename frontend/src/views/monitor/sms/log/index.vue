<script setup lang="ts">
import type { VbenFormSchema } from '@vben/common-ui';

import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { SmsLogQuery, SmsLogResp } from '#/api/system/sms-log';

import { ref } from 'vue';

import { Page } from '@vben/common-ui';
import { $t } from '@vben/locales';

import { useMessage } from 'naive-ui';

import { useVbenVxeGrid } from '#/adapter/vxe-table';
import { smsLogApi } from '#/api/system/sms-log';
import { useDownload } from '#/hooks/app/useDownload';
import { Button } from '#/ui/button';
import {
  ConfirmAction,
  type ConfirmActionExpose,
} from '#/ui-patterns/confirm-action';

const message = useMessage();
const confirmAction = ref<ConfirmActionExpose | null>(null);

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
        list: 'records',
      },
      ajax: {
        query: async ({ page }, formValues) => {
          const res = await smsLogApi.list({
            page: page.currentPage,
            pageSize: page.pageSize,
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
  const ok = await confirmAction.value?.ask({
    title: $t('pages.common.delete'),
    description: $t('ui.actionMessage.deleteConfirm', [row.phone]),
    confirmText: '确认',
    cancelText: '取消',
    tone: 'destructive',
  });
  if (!ok) return;
  try {
    await smsLogApi.delete(row.id);
    message.success($t('pages.common.deleteSuccess'));
    await tableGridApi.query();
  } catch {
    // 错误已在拦截器处理
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
    <ConfirmAction ref="confirmAction" />
    <TableGrid :table-title="$t('system.smsLog.listTitle')">
      <template #toolbar-tools>
        <span v-access:code="['system:smsLog:export']">
          <Button type="button" variant="destructive" @click="handleExport">
            {{ $t('pages.common.export') }}
          </Button>
        </span>
      </template>
      <template #action="{ row }">
        <span v-access:code="['system:smsLog:delete']">
          <Button
            type="button"
            variant="link"
            size="sm"
            class="text-destructive h-auto px-1"
            @click="handleDelete(row)"
          >
            {{ $t('pages.common.delete') }}
          </Button>
        </span>
      </template>
    </TableGrid>
  </Page>
</template>
<style lang="scss" scoped></style>
