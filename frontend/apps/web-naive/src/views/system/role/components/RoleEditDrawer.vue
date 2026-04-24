<script setup lang="ts">
import type { FormInst, FormRules } from 'naive-ui';

import type { RoleResp } from '#/api/system/role';

import { computed, reactive, ref, watch } from 'vue';

import { $t } from '@vben/locales';

import {
  NButton,
  NDrawer,
  NDrawerContent,
  NForm,
  NFormItem,
  NInput,
  NInputNumber,
  NRadioButton,
  NRadioGroup,
  useMessage,
} from 'naive-ui';

import { roleApi } from '#/api/system/role';

interface Props {
  visible: boolean;
  editingRole?: RoleResp | null;
  copyMode?: boolean;
}

interface Emits {
  (e: 'update:visible', value: boolean): void;
  (e: 'success', roleId?: string): void;
}

const props = withDefaults(defineProps<Props>(), {
  visible: false,
  editingRole: null,
  copyMode: false,
});

const emit = defineEmits<Emits>();

const message = useMessage();

const formRef = ref<FormInst | null>(null);
const submitLoading = ref(false);
const copiedMenuIds = ref<number[]>([]);

const isUpdate = computed(() => !!props.editingRole?.id && !props.copyMode);
const isCopy = computed(() => props.copyMode);

const formData = reactive({
  name: '',
  code: '',
  description: '',
  sort: 0,
  dataScope: '1',
});

const formRules: FormRules = {
  name: [
    {
      required: true,
      message: () => $t('system.role.name') + $t('ui.formRules.required'),
      trigger: 'blur',
    },
  ],
  code: [
    {
      required: true,
      message: () => $t('system.role.code') + $t('ui.formRules.required'),
      trigger: 'blur',
    },
  ],
  sort: [
    {
      required: true,
      type: 'number',
      message: () => $t('system.role.sort') + $t('ui.formRules.required'),
      trigger: 'blur',
    },
  ],
  dataScope: [
    {
      required: true,
      message: () => $t('system.role.dataScope') + $t('ui.formRules.required'),
      trigger: 'change',
    },
  ],
};

const dataScopeOptions = [
  { label: '全部', value: '1' },
  { label: '本部门及以下', value: '2' },
  { label: '本部门', value: '3' },
  { label: '仅本人', value: '4' },
  { label: '自定义', value: '5' },
];

function resetForm() {
  formData.name = '';
  formData.code = '';
  formData.description = '';
  formData.sort = 0;
  formData.dataScope = '1';
  copiedMenuIds.value = [];
}

const loadRoleDetail = async (roleId: string, isCopyMode = false) => {
  try {
    const res = await roleApi.detail(roleId);
    if (isCopyMode) {
      // 复制模式：添加"副本"后缀，并保存权限ID
      formData.name = `${res.name ?? ''} - 副本`;
      formData.code = `${res.code ?? ''}_copy`;
      formData.description = res.description ?? '';
      formData.sort = Number(res.sort) || 0;
      formData.dataScope = res.dataScope ?? '1';
      copiedMenuIds.value = res.menuIds || [];
    } else {
      // 编辑模式：直接使用原数据
      formData.name = res.name ?? '';
      formData.code = res.code ?? '';
      formData.description = res.description ?? '';
      formData.sort = Number(res.sort) || 0;
      formData.dataScope = res.dataScope ?? '1';
    }
  } catch {
    message.error('加载角色详情失败');
  }
};

const handleSubmit = async () => {
  try {
    await formRef.value?.validate();
  } catch {
    return;
  }
  submitLoading.value = true;
  try {
    if (isUpdate.value && props.editingRole?.id) {
      await roleApi.update({ ...formData }, props.editingRole.id);
      message.success($t('pages.common.modifySuccess'));
      emit('update:visible', false);
      emit('success');
    } else {
      // 创建新角色（包括复制模式）
      const createRes = await roleApi.create({ ...formData });
      message.success($t('pages.common.addSuccess'));
      
      // 如果是复制模式且有权限数据，复制权限
      if (isCopy.value && copiedMenuIds.value.length > 0 && createRes?.id) {
        try {
          await roleApi.updatePermission(createRes.id, {
            menuIds: copiedMenuIds.value,
          });
          message.success('权限复制成功');
        } catch {
          message.warning('角色创建成功，但权限复制失败');
        }
      }
      
      emit('update:visible', false);
      emit('success', createRes?.id);
    }
  } catch {
    // ignore
  } finally {
    submitLoading.value = false;
  }
};

const handleClose = () => {
  emit('update:visible', false);
};

// 监听编辑角色变化，加载详情
watch(
  () => [props.editingRole, props.copyMode] as const,
  ([role, copyMode]) => {
    resetForm();
    if (role?.id) {
      loadRoleDetail(role.id, copyMode);
    }
  },
  { immediate: true },
);

// 监听抽屉关闭，重置表单
watch(
  () => props.visible,
  (visible) => {
    if (!visible) {
      resetForm();
    }
  },
);
</script>

<template>
  <NDrawer
    :show="visible"
    :width="420"
    placement="right"
    @update:show="handleClose"
  >
    <NDrawerContent
      :title="
        isUpdate
          ? $t('pages.common.edit')
          : isCopy
            ? '复制角色'
            : $t('pages.common.add')
      "
      closable
    >
      <NForm
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-placement="left"
        label-width="80"
      >
        <NFormItem :label="$t('system.role.name')" path="name">
          <NInput
            v-model:value="formData.name"
            :placeholder="$t('system.role.name')"
          />
        </NFormItem>
        <NFormItem :label="$t('system.role.code')" path="code">
          <NInput
            v-model:value="formData.code"
            :placeholder="$t('system.role.code')"
          />
        </NFormItem>
        <NFormItem :label="$t('system.role.description')" path="description">
          <NInput
            v-model:value="formData.description"
            type="textarea"
            :autosize="{ minRows: 2 }"
          />
        </NFormItem>
        <NFormItem :label="$t('system.role.sort')" path="sort">
          <NInputNumber v-model:value="formData.sort" class="w-full" :min="0" />
        </NFormItem>
        <NFormItem :label="$t('system.role.dataScope')" path="dataScope">
          <NRadioGroup v-model:value="formData.dataScope">
            <NRadioButton
              v-for="opt in dataScopeOptions"
              :key="opt.value"
              :value="opt.value"
            >
              {{ opt.label }}
            </NRadioButton>
          </NRadioGroup>
        </NFormItem>
      </NForm>

      <template #footer>
        <div class="flex justify-end gap-2">
          <NButton @click="handleClose">
            {{ $t('common.cancel') }}
          </NButton>
          <NButton
            type="primary"
            :loading="submitLoading"
            @click="handleSubmit"
          >
            {{ $t('common.confirm') }}
          </NButton>
        </div>
      </template>
    </NDrawerContent>
  </NDrawer>
</template>
