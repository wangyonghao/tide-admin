<script setup lang="ts">
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { JobLogResp } from '#/api/schedule';

import { onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';

import { Page } from '@vben/common-ui';
import { $t } from '@vben/locales';

import { NButton, NPopconfirm, NSpace, useMessage } from 'naive-ui';

import { useVbenVxeGrid } from '#/adapter/vxe-table';
import { listGroup, listJobLog, retryJob, stopJob } from '#/api/schedule';
import { useDict } from '#/hooks';

import { useGridFieldColumns, useGridSearchFormSchema } from './data-scope';

const groupList = ref<{ label: string; value: string }[]>([]);

const message = useMessage();

const { job_execute_reason_enum, job_execute_status_enum } = useDict(
  'job_execute_reason_enum',
  'job_execute_status_enum',
);

// 查询任务组列表
const getGroupList = async () => {
  const data = await listGroup();
  groupList.value = data?.map((item: string) => ({
    label: item,
    value: item,
  }));
};

const [TableGrid, tableGridApi] = useVbenVxeGrid({
  formOptions: {
    schema: useGridSearchFormSchema(groupList, job_execute_status_enum),
    submitOnChange: true,
    showCollapseButton: false,
    wrapperClass: 'grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4',
  },
  gridOptions: {
    columns: useGridFieldColumns(),
    border: true,
    height: 'auto',
    keepSource: true,
    columnConfig: {
      resizable: true,
    },
    proxyConfig: {
      autoLoad: false,
      response: {
        list: 'list',
      },
      ajax: {
        query: async ({ page }, formValues) => {
          const res = await listJobLog({
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
  } as VxeTableGridOptions<JobLogResp>,
});

// 停止
const onStop = (record: JobLogResp) => {
  stopJob(record.id).then(() => {
    message.success($t('ui.actionMessage.stopSuccess', [record.jobName]));
  });
};

// 重试
const onRetry = (record: JobLogResp) => {
  retryJob(record.id).then(() => {
    message.success($t('ui.actionMessage.retrySuccess', [record.jobName]));
  });
};

const route = useRoute();
onMounted(async () => {
  await getGroupList();
  if (route.query) {
    tableGridApi.formApi.form.setValues({
      jobId: route.query.jobId
        ? Number.parseInt(route.query.jobId as string, 10)
        : undefined,
      groupName: route.query.groupName ?? undefined,
      jobName: route.query.jobName ?? undefined,
    });
  }
  tableGridApi.query();
});
</script>

<template>
  <Page auto-content-height>
    <TableGrid>
      <template #taskBatchStatus="{ row }">
        <DictTag
          :value="row.taskBatchStatus"
          :dict-list="
            (job_execute_status_enum as unknown as DictItemResp[]) ?? []
          "
        />
      </template>
      <template #operationReason="{ row }">
        <DictTag
          :value="row.operationReason"
          :dict-list="
            (job_execute_reason_enum as unknown as DictItemResp[]) ?? []
          "
        />
      </template>

      <template #action="{ row }">
        <NSpace>
          <span
            v-access:code="['schedule:log:stop']"
            v-if="row.taskBatchStatus === 2"
          >
            <NPopconfirm
              :title="$t('ui.actionMessage.confirmStop', [row.jobName])"
              positive-text="确认"
              negative-text="取消"
              @positive-click="onStop(row)"
            >
              <template #trigger>
                <NButton type="error" text>
                  {{ $t('common.stop') }}
                </NButton>
              </template>
            </NPopconfirm>
          </span>

          <span
            v-access:code="['schedule:log:retry']"
            v-if="
              row.taskBatchStatus === 4 ||
              row.taskBatchStatus === 5 ||
              row.taskBatchStatus === 6
            "
          >
            <NPopconfirm
              :title="$t('ui.actionMessage.confirmRetry', [row.jobName])"
              positive-text="确认"
              negative-text="取消"
              @positive-click="onRetry(row)"
            >
              <template #trigger>
                <NButton type="error" text>
                  {{ $t('common.retry') }}
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
