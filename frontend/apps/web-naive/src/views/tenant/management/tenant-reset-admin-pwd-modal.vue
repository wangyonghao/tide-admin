<script setup lang="ts">
import type { TenantResp } from '#/api';

import { computed, ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';
import { $t } from '@vben/locales';
import { encryptByRsa } from '@vben/utils';

import { useMessage } from 'naive-ui';

import { useVbenForm } from '#/adapter/form';
import { updateTenantAdminUserPwd } from '#/api';
import { defaultFormValueGetter, useBeforeCloseDiff } from '#/utils/popup';

import { useRestTenantAdminUserPwdFormSchema } from './TenantData';

const emits = defineEmits(['success']);
const detailInfo = ref<TenantResp>();
const message = useMessage();

const [EditForm, editFormApi] = useVbenForm({
  commonConfig: {
    componentProps: {
      class: 'w-full',
    },
  },
  layout: 'horizontal',
  resetButtonOptions: {
    show: false,
  },
  schema: useRestTenantAdminUserPwdFormSchema(),
  submitButtonOptions: {
    show: false,
  },
  wrapperClass: 'grid-cols-1 md:grid-cols-1 lg:grid-cols-1',
});

const { onBeforeClose, markInitialized, resetInitialized } = useBeforeCloseDiff(
  {
    initializedGetter: defaultFormValueGetter(editFormApi),
    currentGetter: defaultFormValueGetter(editFormApi),
  },
);

async function handleClosed() {
  await editFormApi.resetForm();
  resetInitialized();
}

const [EditWindow, editWindowApi] = useVbenModal({
  onBeforeClose,
  onClosed: handleClosed,
  async onConfirm() {
    const { valid } = await editFormApi.validate();
    if (!valid) return false;
    editWindowApi.lock();
    try {
      if (!detailInfo.value) {
        message.warning('数据detailInfo不能为空');
        return;
      }
      await updateTenantAdminUserPwd(
        { password: encryptByRsa(editFormApi.form.values.password) || '' },
        detailInfo.value.id,
      );
      message.success($t('pages.common.modifySuccess'));

      resetInitialized();
      emits('success');
      editWindowApi.close();
      return true;
    } catch (error) {
      console.error(error);
    } finally {
      editWindowApi.unlock();
    }
  },
  async onOpenChange(isOpen) {
    if (isOpen) {
      try {
        editWindowApi.lock(true);
        // 租户表id
        const data = editWindowApi.getData<TenantResp>();
        if (data && data.id) {
          detailInfo.value = data;
          editFormApi.form.setValues(data);
        }
      } finally {
        await markInitialized();
        editWindowApi.unlock();
      }
    }
  },
});

const getWindowTitle = computed(() => {
  return `${$t('pages.common.reset')} ${detailInfo.value?.name} ${$t('tenant.management.adminPassword')}`;
});
</script>

<template>
  <EditWindow :title="getWindowTitle" class="w-[40%]">
    <div class="mx-auto flex h-full w-full flex-col">
      <EditForm />
    </div>
  </EditWindow>
</template>
<style lang="scss" scoped></style>
