<script setup lang="ts">
import type { FormInst, FormRules, TreeSelectOption } from 'naive-ui';

import type { DeptResult } from '#/api/system/dept';

import { computed, ref, watch } from 'vue';

import { $t } from '@vben/locales';

import {
  NButton,
  NDrawer,
  NDrawerContent,
  NForm,
  NFormItem,
  NInput,
  NInputNumber,
  NSelect,
  NSpace,
  NSwitch,
  NTreeSelect,
  useMessage,
} from 'naive-ui';

import { deptApi } from '#/api/system/dept';
import { useDict } from '#/hooks/app';

interface Props {
  visible?: boolean;
  data?: DeptResult;
}

const props = withDefaults(defineProps<Props>(), {
  visible: false,
  data: undefined,
});

const emits = defineEmits<{
  success: [];
  'update:visible': [value: boolean];
}>();

const message = useMessage();

// 加载部门类型字典
const { dept_type } = useDict('dept_type');

// 状态
const formRef = ref<FormInst | null>(null);
const loading = ref(false);
const deptOptions = ref<TreeSelectOption[]>([]);

const formModel = ref({
  id: '',
  parentId: undefined as string | undefined,
  code: '',
  name: '',
  type: undefined as string | undefined,
  sort: 1,
  description: '',
  status: 1,
});

const isUpdate = computed(() => !!formModel.value.id);

const drawerTitle = computed(() => {
  return isUpdate.value ? $t('pages.common.edit') : $t('pages.common.add');
});

const rules: FormRules = {
  parentId: {
    type: 'string',
    required: true,
    message: $t('ui.formRules.selectRequired'),
    trigger: ['blur', 'change'],
  },
  code: {
    required: true,
    message: $t('ui.formRules.required'),
    trigger: ['blur', 'input'],
  },
  name: {
    required: true,
    message: $t('ui.formRules.required'),
    trigger: ['blur', 'input'],
  },
  type: {
    type: 'string',
    required: true,
    message: $t('ui.formRules.selectRequired'),
    trigger: ['blur', 'change'],
  },
  sort: {
    type: 'number',
    required: true,
    message: $t('ui.formRules.required'),
    trigger: ['blur', 'change'],
  },
};

// 加载部门列表
async function loadDeptOptions() {
  try {
    const deptArray = await deptApi.tree({});
    deptOptions.value = convertToTreeSelectOptions(deptArray);
  } catch (error) {
    console.error('Failed to load dept options:', error);
  }
}

// 转换为TreeSelect需要的格式
function convertToTreeSelectOptions(depts: DeptResult[]): TreeSelectOption[] {
  return depts.map((dept) => ({
    label: dept.name,
    key: dept.id,
    value: dept.id,
    children: dept.children ? convertToTreeSelectOptions(dept.children) : undefined,
  }));
}

// 重置表单
function resetForm() {
  formModel.value = {
    id: '',
    parentId: undefined,
    code: '',
    name: '',
    type: undefined,
    sort: 1,
    description: '',
    status: 1,
  };
  formRef.value?.restoreValidation();
}

// 加载部门详情
async function loadDeptDetail(id: string) {
  try {
    loading.value = true;
    const res = await deptApi.get(id);
    formModel.value = {
      id: res.id,
      parentId: res.parentId,
      code: res.code,
      name: res.name,
      type: res.type?.toString(),
      sort: res.sort,
      description: res.description,
      status: res.status,
    };
  } catch (error) {
    console.error('Failed to load dept detail:', error);
  } finally {
    loading.value = false;
  }
}

// 提交表单
async function handleSubmit() {
  try {
    await formRef.value?.validate();
    loading.value = true;

    const submitData = {
      ...formModel.value,
      type: parseInt(formModel.value.type || '1'),
    };

    if (isUpdate.value) {
      await deptApi.update(submitData, formModel.value.id);
      message.success($t('pages.common.modifySuccess'));
    } else {
      await deptApi.create(submitData);
      message.success($t('pages.common.addSuccess'));
    }

    emits('success');
    handleClose();
  } catch (error) {
    console.error('Form validation or submission failed:', error);
  } finally {
    loading.value = false;
  }
}

// 关闭抽屉
function handleClose() {
  emits('update:visible', false);
  resetForm();
}

// 监听visible变化
watch(
  () => props.visible,
  async (newVal) => {
    if (newVal) {
      await loadDeptOptions();
      if (props.data?.id) {
        await loadDeptDetail(props.data.id);
      } else {
        resetForm();
      }
    }
  },
);
</script>

<template>
  <NDrawer
    :show="visible"
    :width="600"
    :on-update:show="(val: boolean) => emits('update:visible', val)"
  >
    <NDrawerContent :title="drawerTitle" closable>
      <NForm
        ref="formRef"
        :model="formModel"
        :rules="rules"
        label-placement="left"
        label-width="100"
        require-mark-placement="right-hanging"
      >
        <NFormItem :label="$t('system.dept.parentId')" path="parentId">
          <NTreeSelect
            v-model:value="formModel.parentId"
            :options="deptOptions"
            :placeholder="$t('ui.formRules.selectRequired')"
            clearable
            default-expand-all
          />
        </NFormItem>
        <NFormItem :label="$t('system.dept.code')" path="code">
          <NInput
            v-model:value="formModel.code"
            :placeholder="$t('ui.formRules.required')"
          />
        </NFormItem>
        <NFormItem :label="$t('system.dept.name')" path="name">
          <NInput
            v-model:value="formModel.name"
            :placeholder="$t('ui.formRules.required')"
          />
        </NFormItem>
        <NFormItem :label="$t('system.dept.type')" path="type">
          <NSelect
            v-model:value="formModel.type"
            :options="dept_type"
            :placeholder="$t('ui.formRules.selectRequired')"
            clearable
            label-field="label"
            value-field="value"
          />
        </NFormItem>
        <NFormItem :label="$t('system.dept.sort')" path="sort">
          <NInputNumber
            v-model:value="formModel.sort"
            :placeholder="$t('ui.formRules.required')"
            class="w-full"
            :min="0"
          />
        </NFormItem>
        <NFormItem :label="$t('system.dept.description')" path="description">
          <NInput
            v-model:value="formModel.description"
            type="textarea"
            :placeholder="$t('system.dept.description')"
            :rows="3"
          />
        </NFormItem>
        <NFormItem :label="$t('system.dept.status')" path="status">
          <NSwitch
            :checked-value="1"
            :unchecked-value="2"
            v-model:value="formModel.status"
          >
            <template #checked>
              {{ $t('pages.common.enable') }}
            </template>
            <template #unchecked>
              {{ $t('pages.common.disable') }}
            </template>
          </NSwitch>
        </NFormItem>
      </NForm>

      <template #footer>
        <NSpace justify="end">
          <NButton @click="handleClose">
            {{ $t('common.cancel') }}
          </NButton>
          <NButton type="primary" :loading="loading" @click="handleSubmit">
            {{ $t('common.confirm') }}
          </NButton>
        </NSpace>
      </template>
    </NDrawerContent>
  </NDrawer>
</template>

<style lang="scss" scoped></style>
