<script setup lang="ts">
import type { JobHandlerOption, JobResp, ScheduleMode, ScheduleSpec } from '#/api/schedule';

import { computed, reactive, ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import {
  NCheckbox,
  NCheckboxGroup,
  NForm,
  NFormItem,
  NInput,
  NInputNumber,
  NSelect,
  NTimePicker,
  useMessage,
} from 'naive-ui';

import { addJob, listJobHandlers, updateJob } from '#/api/schedule';

const emits = defineEmits(['success']);
const message = useMessage();
const dataId = ref('');
const handlers = ref<JobHandlerOption[]>([]);

const weekdayOptions = [
  { label: '周一', value: 1 },
  { label: '周二', value: 2 },
  { label: '周三', value: 3 },
  { label: '周四', value: 4 },
  { label: '周五', value: 5 },
  { label: '周六', value: 6 },
  { label: '周日', value: 7 },
];

const modeOptions = [
  { label: '每天', value: 'DAILY' },
  { label: '每周', value: 'WEEKLY' },
  { label: '每月', value: 'MONTHLY' },
  { label: '每隔一段时间', value: 'INTERVAL' },
  { label: '高级（Cron）', value: 'CRON' },
];

const form = reactive({
  name: '',
  handlerCode: '',
  remark: '',
  params: '',
  mode: 'DAILY' as ScheduleMode,
  hour: 9,
  minute: 0,
  daysOfWeek: [1] as number[],
  dayOfMonth: 1,
  interval: 5,
  intervalUnit: 'MINUTE' as 'HOUR' | 'MINUTE',
  cron: '0 0 9 * * ?',
});

const selectedHandler = computed(() =>
  handlers.value.find((item) => item.code === form.handlerCode),
);

const clockValue = computed({
  get() {
    const date = new Date();
    date.setHours(form.hour, form.minute, 0, 0);
    return date.getTime();
  },
  set(value: number | null) {
    if (value == null) return;
    const date = new Date(value);
    form.hour = date.getHours();
    form.minute = date.getMinutes();
  },
});

function buildSchedule(): ScheduleSpec {
  return {
    mode: form.mode,
    hour: form.hour,
    minute: form.minute,
    daysOfWeek: form.daysOfWeek,
    dayOfMonth: form.dayOfMonth,
    interval: form.interval,
    intervalUnit: form.intervalUnit,
    cron: form.cron,
  };
}

function applyRecord(record?: Partial<JobResp>) {
  form.name = record?.name ?? '';
  form.handlerCode = record?.handlerCode ?? '';
  form.remark = record?.remark ?? '';
  form.params = record?.params ?? '';
  const schedule = record?.schedule;
  form.mode = schedule?.mode ?? 'DAILY';
  form.hour = schedule?.hour ?? 9;
  form.minute = schedule?.minute ?? 0;
  form.daysOfWeek = schedule?.daysOfWeek?.length ? schedule.daysOfWeek : [1];
  form.dayOfMonth = schedule?.dayOfMonth ?? 1;
  form.interval = schedule?.interval ?? 5;
  form.intervalUnit = schedule?.intervalUnit ?? 'MINUTE';
  form.cron = schedule?.cron ?? record?.cron ?? '0 0 9 * * ?';
}

const isUpdate = computed(() => !!dataId.value);

const [Modal, drawerApi] = useVbenModal({
  async onConfirm() {
    if (!form.name.trim()) {
      message.warning('请填写任务名称');
      return false;
    }
    if (!form.handlerCode) {
      message.warning('请选择要执行的任务');
      return false;
    }
    drawerApi.lock();
    try {
      const payload = {
        name: form.name.trim(),
        handlerCode: form.handlerCode,
        schedule: buildSchedule(),
        params: form.params || undefined,
        remark: form.remark || undefined,
        enabled: false,
      };
      if (isUpdate.value) {
        await updateJob(payload, dataId.value);
        message.success('修改成功');
      } else {
        await addJob(payload);
        message.success('新增成功，默认已停止，可在列表中激活');
      }
      emits('success');
      drawerApi.close();
      return true;
    } finally {
      drawerApi.unlock();
    }
  },
  async onOpenChange(isOpen) {
    if (!isOpen) return;
    drawerApi.lock(true);
    try {
      if (handlers.value.length === 0) {
        handlers.value = await listJobHandlers();
      }
      const record = drawerApi.getData<Partial<JobResp>>();
      dataId.value = record?.id ? String(record.id) : '';
      applyRecord(record);
    } finally {
      drawerApi.unlock();
    }
  },
});
</script>

<template>
  <Modal :title="isUpdate ? '编辑任务' : '新增任务'" class="w-[560px]">
    <NForm label-placement="left" label-width="96" class="pt-2">
      <NFormItem label="任务名称" required>
        <NInput v-model:value="form.name" placeholder="例如：每天发布公告" />
      </NFormItem>
      <NFormItem label="执行任务" required>
        <NSelect
          v-model:value="form.handlerCode"
          filterable
          :options="handlers.map((item) => ({ label: item.name, value: item.code }))"
          placeholder="请选择已注册的任务"
        />
      </NFormItem>
      <p v-if="selectedHandler?.description" class="text-secondary mb-3 ml-24 text-sm">
        {{ selectedHandler.description }}
      </p>
      <NFormItem label="执行频率" required>
        <NSelect v-model:value="form.mode" :options="modeOptions" />
      </NFormItem>
      <NFormItem v-if="form.mode === 'DAILY' || form.mode === 'WEEKLY' || form.mode === 'MONTHLY'" label="时间">
        <NTimePicker v-model:value="clockValue" format="HH:mm" />
      </NFormItem>
      <NFormItem v-if="form.mode === 'WEEKLY'" label="星期">
        <NCheckboxGroup v-model:value="form.daysOfWeek">
          <NCheckbox v-for="item in weekdayOptions" :key="item.value" :value="item.value" :label="item.label" />
        </NCheckboxGroup>
      </NFormItem>
      <NFormItem v-if="form.mode === 'MONTHLY'" label="每月">
        <NInputNumber v-model:value="form.dayOfMonth" :min="1" :max="31" />
        <span class="ml-2">号</span>
      </NFormItem>
      <NFormItem v-if="form.mode === 'INTERVAL'" label="间隔">
        <NInputNumber v-model:value="form.interval" :min="1" class="w-28" />
        <NSelect
          v-model:value="form.intervalUnit"
          class="ml-2 w-28"
          :options="[
            { label: '分钟', value: 'MINUTE' },
            { label: '小时', value: 'HOUR' },
          ]"
        />
      </NFormItem>
      <NFormItem v-if="form.mode === 'CRON'" label="Cron">
        <NInput v-model:value="form.cron" placeholder="0 0 9 * * ?" />
      </NFormItem>
      <NFormItem label="备注">
        <NInput v-model:value="form.remark" type="textarea" :rows="2" />
      </NFormItem>
    </NForm>
  </Modal>
</template>
