<script setup lang="ts">
import type { FormInst, FormRules, SelectOption } from 'naive-ui';
import type { NoticeCreateReq, NoticeUpdateReq } from '#/api/system/notice';

import { computed, ref, watch } from 'vue';

import { IconifyIcon } from '@vben/icons';
import { $t } from '@vben/locales';

import {
  NButton,
  NCheckbox,
  NCheckboxGroup,
  NDatePicker,
  NForm,
  NFormItem,
  NInput,
  NRadioGroup,
  NSelect,
  NSpace,
  useMessage,
} from 'naive-ui';

import { VbenTiptap } from '@vben/plugins/tiptap';

import { noticeApi } from '#/api/system/notice';
import { userApi } from '#/api/system/user';
import { useDict } from '#/hooks';

defineOptions({ name: 'NoticeForm' });

const props = defineProps<{
  noticeId?: string;
}>();

const emit = defineEmits<{
  success: [];
  cancel: [];
}>();

const message = useMessage();

// ==================== 字典数据 ====================
const {
  notice_type,
  notice_scope_enum,
  notice_method_enum,
} = useDict(
  'notice_type',
  'notice_scope_enum',
  'notice_method_enum',
);

// ==================== 表单数据 ====================
const formRef = ref<FormInst | null>(null);
const isUpdate = computed(() => !!props.noticeId);
const disabledEdit = ref(false);
const loading = ref(false);

const formData = ref<{
  title: string;
  content: string;
  type: string;
  noticeScope: string;
  noticeUsers?: string[];
  noticeMethods: string[];
  isTiming: string;
  publishTime?: number;
  isTop: string;
}>({
  title: '',
  content: '',
  type: '',
  noticeScope: '1',
  noticeUsers: undefined,
  noticeMethods: [],
  isTiming: 'false',
  publishTime: undefined,
  isTop: 'false',
});

// 用户列表
const userList = ref<SelectOption[]>([]);

// ==================== 表单验证规则 ====================
const formRules: FormRules = {
  title: [
    { required: true, message: '请输入公告标题', trigger: 'blur' },
  ],
  type: [
    { required: true, message: '请选择公告类型', trigger: 'change' },
  ],
  noticeScope: [
    { required: true, message: '请选择通知范围', trigger: 'change' },
  ],
  noticeUsers: [
    {
      required: true,
      type: 'array',
      message: '请选择通知用户',
      trigger: 'change',
      validator: (_rule, value) => {
        if (formData.value.noticeScope === '2' && (!value || value.length === 0)) {
          return new Error('请选择通知用户');
        }
        return true;
      },
    },
  ],
  noticeMethods: [
    { required: true, message: '请选择通知方式', trigger: 'change' },
  ],
  isTiming: [
    { required: true, message: '请选择是否定时', trigger: 'change' },
  ],
  publishTime: [
    {
      required: true,
      type: 'number',
      message: '请选择发布时间',
      trigger: 'change',
      validator: (_rule, value) => {
        if (formData.value.isTiming === 'true' && !value) {
          return new Error('请选择发布时间');
        }
        return true;
      },
    },
  ],
  isTop: [
    { required: true, message: '请选择是否置顶', trigger: 'change' },
  ],
  content: [
    { required: true, message: '请输入公告内容', trigger: 'blur' },
  ],
};

// ==================== 是否/否选项 ====================
const yesNoOptions = [
  { label: '是', value: 'true' },
  { label: '否', value: 'false' },
];

// ==================== 重置 ====================
function handleReset() {
  formData.value = {
    title: '',
    content: '',
    type: '',
    noticeScope: '1',
    noticeUsers: undefined,
    noticeMethods: [],
    isTiming: 'false',
    publishTime: undefined,
    isTop: 'false',
  };
}

// ==================== 提交 ====================
async function handleSubmit(_status: number) {
  try {
    await formRef.value?.validate();
    loading.value = true;

    const submitData: NoticeCreateReq | NoticeUpdateReq = {
      ...formData.value,
      noticeUsers: formData.value.noticeScope === '1' 
        ? undefined 
        : formData.value.noticeUsers?.join(','),
      noticeMethods: formData.value.noticeMethods.join(','),
      publishTime: formData.value.isTiming === 'true' && formData.value.publishTime
        ? new Date(formData.value.publishTime).toISOString().slice(0, 19).replace('T', ' ')
        : undefined,
    };

    if (isUpdate.value) {
      await noticeApi.update(props.noticeId!, submitData as NoticeUpdateReq);
      message.success($t('pages.common.modifySuccess'));
    } else {
      await noticeApi.create(submitData);
      message.success($t('pages.common.addSuccess'));
    }

    emit('success');
  } catch (error) {
    console.error('提交失败:', error);
  } finally {
    loading.value = false;
  }
}

// ==================== 加载数据 ====================
async function loadData() {
  try {
    loading.value = true;

    // 加载用户列表
    const users = await userApi.dict({ status: 1 });
    userList.value = users.map((item) => ({
      label: item.label,
      value: String(item.value),
    }));

    // 如果是编辑模式，加载公告详情
    if (props.noticeId) {
      const detail = await noticeApi.detail(props.noticeId);
      formData.value = {
        title: detail.title,
        content: detail.content,
        type: detail.type,
        noticeScope: detail.noticeScope,
        noticeUsers: detail.noticeUsers ? detail.noticeUsers.split(',') : undefined,
        noticeMethods: detail.noticeMethods ? detail.noticeMethods.split(',') : [],
        isTiming: detail.isTiming,
        publishTime: detail.publishTime ? new Date(detail.publishTime).getTime() : undefined,
        isTop: detail.isTop,
      };

      if (detail.status === 3) {
        disabledEdit.value = true;
      }
    }
  } catch (error) {
    console.error('加载数据失败:', error);
    message.error('加载数据失败');
  } finally {
    loading.value = false;
  }
}

// ==================== 监听 noticeId 变化 ====================
watch(() => props.noticeId, () => {
  handleReset();
  loadData();
}, { immediate: true });
</script>

<template>
  <div class="notice-form">
    <!-- 表单内容 -->
    <NForm
      ref="formRef"
      :model="formData"
      :rules="formRules"
      :disabled="disabledEdit || loading"
      label-placement="left"
      label-width="120"
      require-mark-placement="left"
    >
      <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <NFormItem :label="$t('system.notice.title')" path="title" class="md:col-span-2">
          <NInput
            v-model:value="formData.title"
            :placeholder="$t('system.notice.title')"
          />
        </NFormItem>

        <NFormItem :label="$t('system.notice.type')" path="type">
          <NSelect
            v-model:value="formData.type"
            :options="notice_type as any"
            :placeholder="$t('system.notice.type')"
          />
        </NFormItem>

        <NFormItem :label="$t('system.notice.noticeScope')" path="noticeScope">
          <NRadioGroup v-model:value="formData.noticeScope">
            <NSpace>
              <template v-for="item in notice_scope_enum" :key="item.value">
                <NButton
                  :type="formData.noticeScope === String(item.value) ? 'primary' : 'default'"
                  @click="formData.noticeScope = String(item.value)"
                >
                  {{ item.label }}
                </NButton>
              </template>
            </NSpace>
          </NRadioGroup>
        </NFormItem>

        <NFormItem
          v-if="formData.noticeScope === '2'"
          :label="$t('system.notice.noticeUsers')"
          path="noticeUsers"
          class="md:col-span-2"
        >
          <NSelect
            v-model:value="formData.noticeUsers"
            :options="userList"
            :placeholder="$t('system.notice.noticeUsers')"
            multiple
            filterable
          />
        </NFormItem>

        <NFormItem :label="$t('system.notice.noticeMethods')" path="noticeMethods" class="md:col-span-2">
          <NCheckboxGroup v-model:value="formData.noticeMethods">
            <NSpace>
              <template v-for="item in notice_method_enum" :key="item.value">
                <NCheckbox :value="String(item.value)">
                  {{ item.label }}
                </NCheckbox>
              </template>
            </NSpace>
          </NCheckboxGroup>
        </NFormItem>

        <NFormItem :label="$t('system.notice.isTiming')" path="isTiming">
          <NRadioGroup v-model:value="formData.isTiming">
            <NSpace>
              <template v-for="item in yesNoOptions" :key="item.value">
                <NButton
                  :type="formData.isTiming === item.value ? 'primary' : 'default'"
                  @click="formData.isTiming = item.value"
                >
                  {{ item.label }}
                </NButton>
              </template>
            </NSpace>
          </NRadioGroup>
        </NFormItem>

        <NFormItem
          v-if="formData.isTiming === 'true'"
          :label="$t('system.notice.publishTime')"
          path="publishTime"
        >
          <NDatePicker
            v-model:value="formData.publishTime"
            type="datetime"
            :placeholder="$t('system.notice.publishTime')"
            format="yyyy-MM-dd HH:mm:ss"
            class="w-full"
          />
        </NFormItem>

        <NFormItem :label="$t('system.notice.isTop')" path="isTop">
          <NRadioGroup v-model:value="formData.isTop">
            <NSpace>
              <template v-for="item in yesNoOptions" :key="item.value">
                <NButton
                  :type="formData.isTop === item.value ? 'primary' : 'default'"
                  @click="formData.isTop = item.value"
                >
                  {{ item.label }}
                </NButton>
              </template>
            </NSpace>
          </NRadioGroup>
        </NFormItem>

        <NFormItem :label="$t('system.notice.content')" path="content" class="md:col-span-2">
          <VbenTiptap v-model="formData.content" :min-height="400" />
        </NFormItem>
      </div>
    </NForm>

    <!-- 操作按钮 -->
    <div class="mt-6 flex justify-end gap-3">
      <NButton v-if="!disabledEdit" type="info" :loading="loading" @click="handleSubmit(1)">
        <template #icon><IconifyIcon icon="lucide:save" /></template>
        保存为草稿
      </NButton>
      <NButton v-if="!disabledEdit" type="primary" :loading="loading" @click="handleSubmit(3)">
        <template #icon><IconifyIcon icon="lucide:send" /></template>
        发布
      </NButton>
      <NButton v-if="!disabledEdit" :disabled="loading" @click="handleReset">
        <template #icon><IconifyIcon icon="lucide:rotate-ccw" /></template>
        重置
      </NButton>
      <NButton :disabled="loading" @click="emit('cancel')">
        <template #icon><IconifyIcon icon="lucide:x" /></template>
        取消
      </NButton>
      <NButton v-if="disabledEdit" type="warning" disabled>
        已发布不可编辑
      </NButton>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.notice-form {
  :deep(.n-form-item-blank) {
    width: 100%;
  }
}
</style>
