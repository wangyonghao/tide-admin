<script setup lang="ts">
import type { VbenFormSchema } from '@vben/common-ui';

import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { NoticeResp } from '#/api/system/notice';

import { useRouter } from 'vue-router';

import { Page } from '@vben/common-ui';
import { $t } from '@vben/locales';

import { NButton, NTag } from 'naive-ui';

import { useVbenVxeGrid } from '#/adapter/vxe-table';
import { listUserNotice } from '#/api/system/user-message';
import { DictTag } from '#/components/dict';
import { useDict } from '#/hooks';

defineOptions({ name: 'UserMyNotice' });

const { notice_type } = useDict('notice_type');

function useTenantGridFieldColumns(): VxeTableGridOptions['columns'] {
  return [
    { type: 'seq', width: 70, fixed: 'left' },
    {
      field: 'title',
      title: $t('system.msg.title'),
      align: 'center',
      fixed: 'left',
      slots: { default: 'title' },
    },
    {
      field: 'type',
      title: $t('system.msg.type'),
      align: 'center',
      slots: { default: 'type' },
    },
    {
      field: 'isRead',
      title: $t('system.msg.isRead'),
      align: 'center',
      slots: { default: 'isRead' },
    },
    {
      field: 'createUserString',
      title: $t('system.msg.createUserString'),
      align: 'center',
    },
    {
      field: 'publishTime',
      title: $t('system.msg.publishTime'),
      align: 'center',
      width: 180,
    },
  ];
}

function useTenantGridSearchFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'title',
      label: $t('system.msg.search.title'),
      component: 'Input',
    },
    {
      fieldName: 'type',
      label: $t('system.msg.search.type'),
      component: 'Select',
      componentProps: {
        options: notice_type,
      },
    },
  ];
}

const [TableGrid] = useVbenVxeGrid({
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
          const res = await listUserNotice({
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
  } as VxeTableGridOptions<NoticeResp>,
});

const router = useRouter();
// 查看
const onView = (record: NoticeResp) => {
  router.push({ path: '/user/notice', query: { id: record.id } });
};
</script>

<template>
  <Page auto-content-height>
    <TableGrid>
      <template #title="{ row }">
        <NButton text type="primary" @click="onView(row)">
          {{ row.title }}
        </NButton>
      </template>

      <template #type="{ row }">
        <DictTag :value="row.type" :dict-list="notice_type as []" />
      </template>

      <template #isRead="{ row }">
        <NTag :type="row.isRead ? 'default' : 'info'">
          {{ row.isRead ? '已读' : '未读' }}
        </NTag>
      </template>
    </TableGrid>
  </Page>
</template>

<style scoped lang="scss"></style>
