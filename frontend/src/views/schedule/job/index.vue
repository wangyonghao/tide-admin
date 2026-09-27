<script setup lang="ts">
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { JobResp } from '#/api/schedule';

import { onMounted } from 'vue';
import { useRouter } from 'vue-router';

import { Page, useVbenModal } from '@vben/common-ui';

import {
  NButton,
  NPopconfirm,
  NPopover,
  NSpace,
  NSwitch,
  NTimeline,
  NTimelineItem,
  useMessage,
} from 'naive-ui';

import { useVbenVxeGrid } from '#/adapter/vxe-table';
import { deleteJob, listJob, triggerJob, updateJobStatus } from '#/api/schedule';
import { useUserStore } from '#/store/user';

import { useGridFieldColumns, useGridSearchFormSchema } from './data-scope';
import JobEditDrawer from './edit-drawer.vue';

const message = useMessage();
const userStore = useUserStore();
const router = useRouter();

const [TableGrid, tableGridApi] = useVbenVxeGrid({
  formOptions: {
    schema: useGridSearchFormSchema(),
    submitOnChange: true,
    showCollapseButton: false,
    wrapperClass: 'grid-cols-1 md:grid-cols-2 lg:grid-cols-3',
  },
  gridOptions: {
    columns: useGridFieldColumns(),
    border: true,
    height: 'auto',
    keepSource: true,
    columnConfig: { resizable: true },
    proxyConfig: {
      autoLoad: true,
      response: { list: 'records' },
      ajax: {
        query: async ({ page }, formValues) => {
          return await listJob({
            page: page.currentPage,
            pageSize: page.pageSize,
            ...formValues,
          });
        },
      },
    },
    rowConfig: { keyField: 'id', isHover: true },
    toolbarConfig: {
      custom: true,
      refresh: true,
      refreshOptions: { code: 'query' },
      search: true,
      zoom: true,
    },
  } as VxeTableGridOptions<JobResp>,
});

const [EditorWindow, editorApi] = useVbenModal({
  connectedComponent: JobEditDrawer,
  destroyOnClose: true,
});

function handleAdd() {
  editorApi.setData({});
  editorApi.open();
}

function handleEdit(record: JobResp) {
  editorApi.setData(record);
  editorApi.open();
}

async function handleDelete(row: JobResp) {
  await deleteJob(row.id);
  message.success('删除成功');
  await tableGridApi.query();
}

function onTrigger(record: JobResp) {
  triggerJob(record.id).then(() => {
    message.success('已触发执行');
  });
}

function onUpdateStatus(record: JobResp) {
  updateJobStatus(record.status, record.id)
    .then(() => {
      message.success(record.status === 1 ? '已激活' : '已停止');
    })
    .catch(() => {
      record.status = record.status === 1 ? 0 : 1;
    });
}

function onLog(record: JobResp) {
  router.push({
    path: '/schedule/log',
    query: { jobId: record.id, jobName: record.name },
  });
}

onMounted(() => {
  tableGridApi.query();
});
</script>

<template>
  <Page auto-content-height>
    <TableGrid>
      <template #toolbar-tools>
        <NSpace>
          <span v-access:code="['schedule:job:create']">
            <NButton type="primary" @click="handleAdd">新增</NButton>
          </span>
        </NSpace>
      </template>
      <template #scheduleLabel="{ row }">
        <NPopover placement="bottom" style="width: 240px">
          <template #trigger>
            <a class="text-primary cursor-pointer">{{ row.scheduleLabel }}</a>
          </template>
          <div class="mb-2 text-sm">接下来 5 次</div>
          <NTimeline>
            <NTimelineItem
              v-for="item in row.upcomingTimes ?? []"
              :key="item"
              :title="item"
            />
          </NTimeline>
        </NPopover>
      </template>
      <template #status="{ row }">
        <NSwitch
          v-model:value="row.status"
          :checked-value="1"
          :unchecked-value="0"
          :disabled="!userStore.hasPermission('schedule:job:update')"
          @update:value="onUpdateStatus(row)"
        />
      </template>
      <template #action="{ row }">
        <NSpace>
          <span v-access:code="['schedule:job:trigger']">
            <NPopconfirm @positive-click="onTrigger(row)">
              <template #trigger>
                <NButton type="primary" text>执行</NButton>
              </template>
              确定立即执行「{{ row.name }}」一次？
            </NPopconfirm>
          </span>
          <span v-access:code="['schedule:job:update']">
            <NButton type="primary" text @click="handleEdit(row)">编辑</NButton>
          </span>
          <span v-access:code="['schedule:log:list']">
            <NButton type="primary" text @click="onLog(row)">日志</NButton>
          </span>
          <span v-access:code="['schedule:job:delete']">
            <NPopconfirm @positive-click="handleDelete(row)">
              <template #trigger>
                <NButton type="error" text>删除</NButton>
              </template>
              确定删除「{{ row.name }}」？
            </NPopconfirm>
          </span>
        </NSpace>
      </template>
    </TableGrid>
    <EditorWindow @success="tableGridApi.query()" />
  </Page>
</template>
