<script setup lang="ts">
import type {
  FormInst,
  FormRules,
  SelectOption,
  TreeSelectOption,
} from 'naive-ui';
import { computed, reactive, ref, watch } from 'vue';

import { $t } from '@vben/locales';

import {
  NDrawer,
  NDrawerContent,
  NForm,
  NFormItem,
  NInput,
  NRadio,
  NRadioGroup,
} from 'naive-ui';

import { asSelectList } from '#/adapter/component/select-value';
import FormSelect from '#/adapter/component/FormSelect.vue';
import FormTreeSelect from '#/adapter/component/FormTreeSelect.vue';
import { userApi } from '#/api/system/user';
import { Button } from '#/ui/button';
import { toast } from '#/ui-patterns/toast';

interface Props {
  visible: boolean;
  userId?: string;
  deptData: TreeSelectOption[];
  roleOptions: SelectOption[];
  defaultDeptId?: string;
}

interface Emits {
  (e: 'update:visible', value: boolean): void;
  (e: 'success'): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();


const roleSelectOptions = computed(() =>
  props.roleOptions.flatMap((item) => {
    if (item.label == null || item.value == null) return [];
    if (typeof item.value !== 'string' && typeof item.value !== 'number') {
      return [];
    }
    return [{ label: String(item.label), value: String(item.value) }];
  }),
);

const formRef = ref<FormInst | null>(null);
const submitLoading = ref(false);
const isUpdate = ref(false);

const formData = reactive({
  username: '',
  displayName: '',
  password: '',
  gender: 0,
  email: '',
  phone: '',
  deptId: undefined as string | undefined,
  roleIds: [] as string[],
  status: 1,
  description: '',
});

const formRules: FormRules = {
  username: [
    {
      required: true,
      message: `请输入 ${$t('system.user.field.username')}`,
      trigger: 'blur',
    },
  ],
  displayName: [
    {
      required: true,
      message: '请输入显示名称',
      trigger: 'blur',
    },
  ],
  password: [
    {
      required: true,
      message: '请输入密码',
      trigger: 'blur',
    },
  ],
  email: [
    {
      type: 'email',
      message: '请输入正确的邮箱格式',
      trigger: 'blur',
    },
  ],
  phone: [
    {
      pattern: /^1[3-9]\d{9}$/,
      message: '请输入正确的手机号码',
      trigger: 'blur',
    },
  ],
};

watch(
  () => props.visible,
  async (newVal) => {
    if (newVal) {
      if (props.userId) {
        // 编辑模式
        isUpdate.value = true;
        await loadUserDetail();
      } else {
        // 新增模式
        resetForm();
      }
    }
  },
);

async function loadUserDetail() {
  if (!props.userId) return;

  try {
    const res = await userApi.detail(props.userId);
    formData.username = res.username ?? '';
    formData.displayName = res.displayName ?? '';
    formData.gender = res.gender ?? 0;
    formData.email = res.email ?? '';
    formData.phone = res.phone ?? '';
    formData.deptId =
      res.deptId == null || res.deptId === '' ? undefined : String(res.deptId);
    formData.roleIds = (res.roleIds ?? []).map(String);
    formData.status = res.status ?? 1;
    formData.description = res.description ?? '';
    // 编辑时不需要密码
    formData.password = '';
  } catch (error) {
    console.error('加载用户详情失败:', error);
    toast.error('加载用户详情失败');
  }
}

function resetForm() {
  isUpdate.value = false;
  formData.username = '';
  formData.displayName = '';
  formData.password = '';
  formData.gender = 0;
  formData.email = '';
  formData.phone = '';
  formData.deptId = props.defaultDeptId;
  formData.roleIds = [];
  formData.status = 1;
  formData.description = '';
  formRef.value?.restoreValidation();
}

async function handleSubmit() {
  try {
    await formRef.value?.validate();
  } catch {
    return;
  }

  submitLoading.value = true;
  try {
    if (isUpdate.value && props.userId) {
      // 编辑时不传递 username
      const { username: _username, ...updateData } = formData;
      await userApi.update(updateData, props.userId);
      toast.success('修改成功');
    } else {
      await userApi.create({ ...formData });
      toast.success('新增成功');
    }
    handleClose();
    emit('success');
  } catch (error) {
    console.error('保存用户失败:', error);
  } finally {
    submitLoading.value = false;
  }
}

function handleClose() {
  emit('update:visible', false);
}

function handleAfterLeave() {
  resetForm();
}
</script>

<template>
  <NDrawer
    :show="visible"
    :width="480"
    placement="right"
    @update:show="handleClose"
    @after-leave="handleAfterLeave"
  >
    <NDrawerContent
      :title="isUpdate ? '编辑用户' : '新增用户'"
      closable
    >
      <NForm
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-placement="left"
        label-width="80"
      >
        <NFormItem
          label="用户名"
          path="username"
        >
          <NInput
            v-model:value="formData.username"
            placeholder="请输入用户名"
            :disabled="isUpdate"
          />
        </NFormItem>
        <NFormItem
          label="显示名称"
          path="displayName"
        >
          <NInput
            v-model:value="formData.displayName"
            placeholder="请输入显示名称"
          />
        </NFormItem>
        <NFormItem
          v-if="!isUpdate"
          label="密码"
          path="password"
        >
          <NInput
            v-model:value="formData.password"
            type="password"
            show-password-on="click"
            placeholder="请输入密码"
          />
        </NFormItem>
        <NFormItem
          label="性别"
          path="gender"
        >
          <NRadioGroup v-model:value="formData.gender">
            <NRadio :value="0">
              未知
            </NRadio>
            <NRadio :value="1">
              男
            </NRadio>
            <NRadio :value="2">
              女
            </NRadio>
          </NRadioGroup>
        </NFormItem>
        <NFormItem
          label="邮箱"
          path="email"
        >
          <NInput
            v-model:value="formData.email"
            placeholder="请输入邮箱"
          />
        </NFormItem>
        <NFormItem
          label="手机号"
          path="phone"
        >
          <NInput
            v-model:value="formData.phone"
            placeholder="请输入手机号"
          />
        </NFormItem>
        <NFormItem
          label="部门"
          path="deptId"
        >
          <FormTreeSelect
            v-model:value="formData.deptId"
            :options="deptData"
            key-field="id"
            label-field="name"
            children-field="children"
            placeholder="请选择部门"
            clearable
            default-expand-all
          />
        </NFormItem>
        <NFormItem
          label="角色"
          path="roleIds"
        >
          <FormSelect
            :value="formData.roleIds"
            :options="roleSelectOptions"
            placeholder="请选择角色"
            filterable
            multiple
            clearable
            @update:value="formData.roleIds = asSelectList($event).map(String)"
          />
        </NFormItem>
        <NFormItem
          label="状态"
          path="status"
        >
          <NRadioGroup v-model:value="formData.status">
            <NRadio :value="1">
              启用
            </NRadio>
            <NRadio :value="0">
              禁用
            </NRadio>
          </NRadioGroup>
        </NFormItem>
        <NFormItem
          label="描述"
          path="description"
        >
          <NInput
            v-model:value="formData.description"
            type="textarea"
            :autosize="{ minRows: 3, maxRows: 5 }"
            placeholder="请输入描述"
          />
        </NFormItem>
      </NForm>

      <template #footer>
        <div class="flex justify-end gap-2">
          <Button
            type="button"
            variant="outline"
            @click="handleClose"
          >
            取消
          </Button>
          <Button
            type="button"
            :loading="submitLoading"
            @click="handleSubmit"
          >
            确定
          </Button>
        </div>
      </template>
    </NDrawerContent>
  </NDrawer>
</template>
