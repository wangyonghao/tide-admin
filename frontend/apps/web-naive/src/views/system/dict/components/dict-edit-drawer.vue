<script setup lang="ts">
import type { FormInst, FormRules } from 'naive-ui';
import type { DictRequest, DictResult } from '#/api/system/dict';

import { computed, ref, watch } from 'vue';

import {
  NButton,
  NDrawer,
  NDrawerContent,
  NForm,
  NFormItem,
  NInput,
  NInputNumber,
  NSwitch,
  useMessage,
} from 'naive-ui';

import { dictApi } from '#/api/system';

interface Props {
  visible: boolean;
  dictData?: DictResult;
}

interface Emits {
  (e: 'update:visible', value: boolean): void;
  (e: 'success'): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();

const message = useMessage();

// ==================== 抽屉控制 ====================
const show = computed({
  get: () => props.visible,
  set: (value) => emit('update:visible', value),
});

const isEdit = computed(() => !!props.dictData);
const drawerTitle = computed(() => (isEdit.value ? '编辑字典项' : '新建字典项'));

// ==================== 表单数据 ====================
const formRef = ref<FormInst | null>(null);
const formData = ref<DictRequest>({
  dictType: '',
  value: '',
  label: '',
  extra: undefined,
  sort: 0,
  enabled: true,
  description: '',
});

const formRules: FormRules = {
  dictType: [
    {
      required: true,
      message: '请输入字典类型',
      trigger: ['blur', 'input'],
    },
    {
      pattern: /^[a-zA-Z][a-zA-Z0-9_]{1,29}$/,
      message: '字典类型长度为 2-30 个字符，支持大小写字母、数字、下划线，以字母开头',
      trigger: ['blur', 'input'],
    },
  ],
  value: [
    {
      required: true,
      message: '请输入字典值',
      trigger: ['blur', 'input'],
    },
    {
      max: 255,
      message: '字典值长度不能超过 255 个字符',
      trigger: ['blur', 'input'],
    },
  ],
  label: [
    {
      required: true,
      message: '请输入字典标签',
      trigger: ['blur', 'input'],
    },
    {
      max: 255,
      message: '字典标签长度不能超过 255 个字符',
      trigger: ['blur', 'input'],
    },
  ],
  description: [
    {
      max: 500,
      message: '描述长度不能超过 500 个字符',
      trigger: ['blur', 'input'],
    },
  ],
};

// ==================== 数据加载 ====================
function loadDictData() {
  if (!props.dictData) return;

  formData.value = {
    dictType: props.dictData.dictType,
    value: props.dictData.value,
    label: props.dictData.label,
    extra: props.dictData.ext,
    sort: props.dictData.sort,
    enabled: props.dictData.enabled,
    description: props.dictData.description || '',
  };
}

// ==================== 表单提交 ====================
const submitting = ref(false);

async function handleSubmit() {
  try {
    await formRef.value?.validate();
    submitting.value = true;

    if (isEdit.value && props.dictData) {
      await dictApi.update(props.dictData.id, formData.value);
      message.success('修改成功');
    } else {
      await dictApi.create(formData.value);
      message.success('新建成功');
    }

    show.value = false;
    emit('success');
  } catch (error: any) {
    if (error?.errorFields) {
      message.error('请检查表单填写是否正确');
    } else {
      console.error('提交失败:', error);
      message.error(isEdit.value ? '修改失败' : '新建失败');
    }
  } finally {
    submitting.value = false;
  }
}

function handleCancel() {
  show.value = false;
}

// ==================== 监听变化 ====================
watch(
  () => props.visible,
  (visible) => {
    if (visible) {
      if (isEdit.value) {
        loadDictData();
      } else {
        // 重置表单
        formData.value = {
          dictType: '',
          value: '',
          label: '',
          extra: undefined,
          sort: 0,
          enabled: true,
          description: '',
        };
        formRef.value?.restoreValidation();
      }
    }
  },
);
</script>

<template>
  <NDrawer v-model:show="show" :width="600" :trap-focus="false">
    <NDrawerContent :title="drawerTitle" closable>
      <NForm
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-placement="left"
        label-width="100"
        require-mark-placement="left"
      >
        <NFormItem label="字典类型" path="dictType">
          <NInput
            v-model:value="formData.dictType"
            placeholder="请输入字典类型，如：notice_type"
            :disabled="isEdit"
            maxlength="30"
            show-count
          />
        </NFormItem>

        <NFormItem label="字典值" path="value">
          <NInput
            v-model:value="formData.value"
            placeholder="请输入字典值，如：1"
            maxlength="255"
            show-count
          />
        </NFormItem>

        <NFormItem label="字典标签" path="label">
          <NInput
            v-model:value="formData.label"
            placeholder="请输入字典标签，如：产品新闻"
            maxlength="255"
            show-count
          />
        </NFormItem>

        <NFormItem label="排序" path="sort">
          <NInputNumber
            v-model:value="formData.sort"
            placeholder="请输入排序"
            :min="0"
            class="w-full"
          />
        </NFormItem>

        <NFormItem label="状态" path="enabled">
          <NSwitch v-model:value="formData.enabled">
            <template #checked>启用</template>
            <template #unchecked>禁用</template>
          </NSwitch>
        </NFormItem>

        <NFormItem label="描述" path="description">
          <NInput
            v-model:value="formData.description"
            type="textarea"
            placeholder="请输入描述"
            :rows="3"
            maxlength="500"
            show-count
          />
        </NFormItem>
      </NForm>

      <template #footer>
        <div class="flex justify-end gap-2">
          <NButton @click="handleCancel">取消</NButton>
          <NButton
            type="primary"
            :loading="submitting"
            @click="handleSubmit"
          >
            确定
          </NButton>
        </div>
      </template>
    </NDrawerContent>
  </NDrawer>
</template>
