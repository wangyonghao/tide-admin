<script setup lang="ts">
import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { SmsLogResp } from '#/api/system/sms-log';

import { ref } from 'vue';

import { IconifyIcon } from '@vben/icons';

import { useVbenVxeGrid } from '#/adapter/vxe-table';
import { smsLogApi } from '#/api/system/sms-log';
import { Badge } from '#/ui/badge';
import { Button } from '#/ui/button';
import { toast } from '#/ui-patterns/toast';
import {
  ConfirmAction,
  type ConfirmActionExpose,
} from '#/ui-patterns/confirm-action';

const confirmAction = ref<ConfirmActionExpose | null>(null);

function useSearchSchema(): VbenFormSchema[] {
  return [
    { component: 'Input', fieldName: 'configId', label: '配置ID' },
    { component: 'Input', fieldName: 'phone', label: '手机号' },
    {
      component: 'Select',
      fieldName: 'status',
      label: '发送状态',
      componentProps: {
        clearable: true,
        options: [
          { label: '成功', value: 1 },
          { label: '失败', value: 0 },
        ],
      },
    },
  ];
}

function textValue(value: unknown) {
  return typeof value === 'string' ? value.trim() : '';
}

function smsQuery(formValues: Record<string, unknown>) {
  const configId = textValue(formValues.configId);
  const phone = textValue(formValues.phone);
  const status = formValues.status;
  return {
    configId: configId || undefined,
    phone: phone || undefined,
    status: status === 0 || status === 1 ? status : undefined,
    sort: ['createTime,desc'] as string[],
  };
}

const [Grid, gridApi] = useVbenVxeGrid({
  formOptions: {
    schema: useSearchSchema(),
    showCollapseButton: false,
    wrapperClass: 'grid-cols-1 md:grid-cols-2 lg:grid-cols-3',
  },
  gridOptions: {
    columns: [
      { type: 'seq', width: 70, fixed: 'left' },
      { field: 'phone', title: '手机号', minWidth: 120 },
      { field: 'params', title: '参数配置', minWidth: 200, align: 'left' },
      {
        field: 'status',
        title: '发送状态',
        minWidth: 100,
        slots: { default: 'status' },
      },
      { field: 'resMsg', title: '返回数据', minWidth: 200, align: 'left' },
      { field: 'createUserString', title: '创建人', minWidth: 120 },
      {
        field: 'createTime',
        title: '创建时间',
        minWidth: 160,
        sortable: true,
      },
      {
        field: 'action',
        title: '操作',
        width: 100,
        fixed: 'right',
        slots: { default: 'action' },
      },
    ],
    height: 'auto',
    keepSource: true,
    pagerConfig: { pageSize: 10 },
    proxyConfig: {
      ajax: {
        query: async ({ page }, formValues) => {
          return await smsLogApi.list({
            page: page.currentPage,
            pageSize: page.pageSize,
            ...smsQuery(formValues ?? {}),
          });
        },
      },
    },
    rowConfig: { keyField: 'id' },
    toolbarConfig: {
      custom: true,
      export: false,
      refresh: { code: 'query' },
      search: true,
      zoom: true,
    },
  } as VxeTableGridOptions<SmsLogResp>,
});

async function handleExport() {
  const formValues = (await gridApi.formApi?.getValues?.()) ?? {};
  smsLogApi.export(smsQuery(formValues));
}

async function handleDelete(row: SmsLogResp) {
  const ok = await confirmAction.value?.ask({
    title: '删除确认',
    description: `确定删除手机号为 ${row.phone} 的短信日志吗？`,
    tone: 'destructive',
  });
  if (!ok) return;
  try {
    await smsLogApi.delete(row.id);
    toast.success('删除成功');
    await gridApi.query();
  } catch (error) {
    console.error('删除短信日志失败:', error);
    toast.error('删除失败');
  }
}
</script>

<template>
  <div class="h-full">
    <ConfirmAction ref="confirmAction" />
    <Grid>
      <template #toolbar-tools>
        <Button type="button" variant="outline" @click="handleExport">
          <IconifyIcon icon="lucide:download" class="mr-1 size-4" />
          导出
        </Button>
      </template>
      <template #status="{ row }">
        <Badge :variant="row.status === 1 ? 'success' : 'destructive'">
          {{ row.status === 1 ? '成功' : '失败' }}
        </Badge>
      </template>
      <template #action="{ row }">
        <Button
          type="button"
          variant="link"
          size="sm"
          class="text-destructive h-auto px-1"
          @click="handleDelete(row)"
        >
          删除
        </Button>
      </template>
    </Grid>
  </div>
</template>
