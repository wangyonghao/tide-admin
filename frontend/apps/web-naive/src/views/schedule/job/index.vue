<script setup lang="ts">
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { JobResp } from '#/api/schedule';

import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';

import { Page, useVbenModal } from '@vben/common-ui';
import { $t } from '@vben/locales';

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
import {
  deleteJob,
  listGroup,
  listJob,
  triggerJob,
  updateJobStatus,
} from '#/api/schedule';
import { useDict } from '#/hooks';
import { useUserStore } from '#/store/user';
import { parseCron } from '#/utils/cron';

import { useGridFieldColumns, useGridSearchFormSchema } from './data-scope';
import PackageEditDrawer from './edit-drawer.vue';

const message = useMessage();
const {
  job_status_enum,
  job_trigger_type_enum,
  job_task_type_enum,
  job_route_strategy_enum,
  job_block_strategy_enum,
} = useDict(
  'job_status_enum',
  'job_trigger_type_enum',
  'job_task_type_enum',
  'job_route_strategy_enum',
  'job_block_strategy_enum',
);
const groupList = ref<{ label: string; value: string }[]>([]);
const userStore = useUserStore()

// 查询任务组列表
const getGroupList = async () => {
  const data = await listGroup();
  groupList.value = data?.map((item: string) => ({
    label: item,
    value: item,
  }));
  tableGridApi.formApi.form.setValues({
    groupName: groupList.value.length > 0 ? groupList.value[0]?.label : '',
  });
  tableGridApi.query();
};

const [TableGrid, tableGridApi] = useVbenVxeGrid({
  formOptions: {
    schema: useGridSearchFormSchema(groupList, job_status_enum),
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
          const res = await listJob({
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
  } as VxeTableGridOptions<JobResp>,
});

const [EditorWindow, editorApi] = useVbenModal({
  connectedComponent: PackageEditDrawer,
  destroyOnClose: true,
});

const handleEdit = (record: JobResp) => {
  editorApi.setData(record);
  editorApi.open();
};

const handleAdd = () => {
  editorApi.setData({
    triggerType: job_trigger_type_enum?.value[0]?.value,
    taskType: job_task_type_enum?.value[0]?.value,
    routeKey: job_route_strategy_enum?.value[0]?.value,
    blockStrategy: job_block_strategy_enum?.value[0]?.value,
  });
  editorApi.open();
};

const handleDelete = async (row: JobResp) => {
  try {
    await deleteJob(row.id);
    message.success($t('pages.common.deleteSuccess'));
    await tableGridApi.query();
    return true;
  } catch {
    return false;
  }
};

// 执行
const onTrigger = (record: JobResp) => {
  triggerJob(record.id).then(() => {
    message.success($t('schedule.job.executeRequestIssued'));
  });
};

// 修改状态
const onUpdateStatus = (record: JobResp) => {
  const msg =
    record.jobStatus === 1
      ? $t('common.enabledSuccess')
      : $t('common.disabledSuccess');
  updateJobStatus({ jobStatus: record.jobStatus }, record.id)
    .then(() => {
      message.success(msg);
    })
    .catch(() => {
      record.jobStatus = record.jobStatus === 1 ? 0 : 1;
    });
};

const router = useRouter();
// 日志
const onLog = (record: JobResp) => {
  router.push({
    path: '/schedule/log',
    query: {
      jobId: record.id,
      jobName: record.jobName,
      groupName: record.groupName,
    },
  });
};

onMounted(() => {
  getGroupList();
});
</script>

<template>
  <Page auto-content-height>
    <TableGrid>
      <template #toolbar-tools>
        <NSpace>
          <span v-access:code="['tenant:package:create']">
            <NButton type="primary" @click="handleAdd">
              {{ $t('pages.common.add') }}
            </NButton>
          </span>
        </NSpace>
      </template>
      <template #triggerType="{ row }">
        <div class="flex flex-row items-center justify-center">
          <!-- <DictTag
            :value="row.triggerType"
            :dict-list="(job_trigger_type_enum) ?? []"
          /> -->
          :&nbsp;
          <span v-if="row.triggerType === 2">{{ row.triggerInterval }} 秒</span>
          <span v-else>
            <NPopover
              :title="$t('schedule.job.lastFiveTimes')"
              placement="bottom"
              style="width: 225px"
            >
              <template #trigger>
                <a class="text-primary cursor-pointer">{{ row.triggerInterval }}</a>
              </template>
              <NTimeline style="max-width: 225px; margin-top: 20px">
                <NTimelineItem
                  v-for="(item, index) in parseCron(row.triggerInterval).split(
                    '\n',
                  )"
                  :key="index"
                  :title="item"
                />
              </NTimeline>
            </NPopover>
          </span>
        </div>
      </template>
      <template #taskType="{ row }">
        <div class="flex flex-row items-center justify-center gap-2">
          <span
            class="py-4px rounded-sm border-2 border-solid bg-blue-100 px-2"
          >
            <DictTag
              :value="row.taskType"
              :dict-list="(job_task_type_enum) ?? []"
            />
          </span>
          <span>{{ row.executorInfo }}</span>
        </div>
      </template>
      <template #jobStatus="{ row }">
        <NSwitch
          v-model:value="row.jobStatus"
          :checked-value="1"
          :unchecked-value="0"
          :disabled="!userStore.hasPermission('tool:job:update')"
          @update:value="onUpdateStatus(row)"
        />
      </template>
      <template #action="{ row }">
        <NSpace>
          <span v-access:code="['schedule:job:trigger']">
            <NPopconfirm
              :title="$t('ui.actionMessage.confirmExecute', [row.jobName])"
              positive-text="确认"
              negative-text="取消"
              @positive-click="onTrigger(row)"
            >
              <template #trigger>
                <NButton type="primary" text>
                  {{ $t('pages.common.execute') }}
                </NButton>
              </template>
            </NPopconfirm>
          </span>
          <span v-access:code="['schedule:job:update']">
            <NButton type="primary" @click="handleEdit(row)" text>
              {{ $t('pages.common.edit') }}
            </NButton>
          </span>
          <span v-access:code="['schedule:log:list']">
            <NButton type="primary" @click="onLog(row)" text>
              {{ $t('schedule.job.viewLog') }}
            </NButton>
          </span>
          <span v-access:code="['schedule:job:delete']">
            <NPopconfirm
              :title="$t('ui.actionMessage.deleteConfirm', [row.jobName])"
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
    <EditorWindow @success="tableGridApi.query()" />
  </Page>
</template>
<style lang="scss" scoped></style>
