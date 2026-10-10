<script setup lang="ts">
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { JobResp } from '#/api/schedule';

import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';

import { Page, useVbenModal } from '@vben/common-ui';

import { NPopover, NTimeline, NTimelineItem, useMessage } from 'naive-ui';

import { useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  deleteJob,
  listJob,
  triggerJob,
  updateJobStatus,
} from '#/api/schedule';
import { useUserStore } from '#/store/user';
import { Button } from '#/ui/button';
import { Switch } from '#/ui/switch';
import {
  ConfirmAction,
  type ConfirmActionExpose,
} from '#/ui-patterns/confirm-action';

import { useGridFieldColumns, useGridSearchFormSchema } from './data-scope';
import JobEditDrawer from './edit-drawer.vue';

const message = useMessage();
const confirmAction = ref<ConfirmActionExpose | null>(null);
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
  const ok = await confirmAction.value?.ask({
    title: '删除任务',
    description: `确定删除「${row.name}」？`,
    tone: 'destructive',
  });
  if (!ok) return;
  await deleteJob(row.id);
  message.success('删除成功');
  await tableGridApi.query();
}

function onTrigger(record: JobResp) {
  triggerJob(record.id).then(() => {
    message.success('已触发执行');
  });
}

async function confirmTrigger(record: JobResp) {
  const ok = await confirmAction.value?.ask({
    title: '立即执行',
    description: `确定立即执行「${record.name}」一次？`,
  });
  if (!ok) return;
  onTrigger(record);
}

function onUpdateStatus(record: JobResp, status: number) {
  record.status = status;
  updateJobStatus(status, record.id)
    .then(() => {
      message.success(status === 1 ? '已激活' : '已停止');
    })
    .catch(() => {
      record.status = status === 1 ? 0 : 1;
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
    <ConfirmAction ref="confirmAction" />
    <TableGrid>
      <template #toolbar-tools>
        <span v-access:code="['schedule:job:create']">
          <Button type="button" @click="handleAdd">新增</Button>
        </span>
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
        <Switch
          :model-value="row.status"
          :checked-value="1"
          :unchecked-value="0"
          :disabled="!userStore.hasPermission('schedule:job:update')"
          @update:model-value="(value) => onUpdateStatus(row, Number(value))"
        />
      </template>
      <template #action="{ row }">
        <div class="inline-flex items-center gap-2">
          <span v-access:code="['schedule:job:trigger']">
            <Button
              type="button"
              variant="link"
              size="sm"
              class="h-auto px-1"
              @click="confirmTrigger(row)"
            >
              执行
            </Button>
          </span>
          <span v-access:code="['schedule:job:update']">
            <Button
              type="button"
              variant="link"
              size="sm"
              class="h-auto px-1"
              @click="handleEdit(row)"
            >
              编辑
            </Button>
          </span>
          <span v-access:code="['schedule:log:list']">
            <Button
              type="button"
              variant="link"
              size="sm"
              class="h-auto px-1"
              @click="onLog(row)"
            >
              日志
            </Button>
          </span>
          <span v-access:code="['schedule:job:delete']">
            <Button
              type="button"
              variant="link"
              size="sm"
              class="text-destructive h-auto px-1"
              @click="handleDelete(row)"
            >
              删除
            </Button>
          </span>
        </div>
      </template>
    </TableGrid>
    <EditorWindow @success="tableGridApi.query()" />
  </Page>
</template>
