<script setup lang="ts">
import type { UserResp } from '#/api/system/user';

import { ref, watch } from 'vue';

import { useVbenDrawer } from '@vben/common-ui';
import { IconifyIcon } from '@vben/icons';

import { NDescriptions, NDescriptionsItem } from 'naive-ui';

import { userApi } from '#/api/system/user';
import { Badge } from '#/ui/badge';
import { badgeVariantForTag } from '#/ui/badge/variant';
import { Button } from '#/ui/button';
import { toast } from '#/ui-patterns/toast';

interface Props {
  visible: boolean;
  userId?: string;
}

interface Emits {
  (e: 'update:visible', value: boolean): void;
  (e: 'edit', user: UserResp): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();


const detailData = ref<null | UserResp>(null);
const detailLoading = ref(false);

const [Drawer, drawerApi] = useVbenDrawer({
  class: 'w-[600px]',
  title: '用户详情',
  onOpenChange(isOpen) {
    if (!isOpen) emit('update:visible', false);
  },
});

watch(
  () => props.visible,
  async (open) => {
    if (open) {
      drawerApi.open();
      if (props.userId) await loadUserDetail();
    } else {
      drawerApi.close();
    }
  },
);

async function loadUserDetail() {
  if (!props.userId) return;

  detailLoading.value = true;
  try {
    const res = await userApi.detail(props.userId);
    detailData.value = res;
  } catch (error) {
    console.error('加载用户详情失败:', error);
    toast.error('加载用户详情失败');
    handleClose();
  } finally {
    detailLoading.value = false;
  }
}

function getGenderLabel(gender?: number) {
  const genderMap: Record<number, string> = {
    0: '未知',
    1: '男',
    2: '女',
  };
  return genderMap[gender ?? 0] || '未知';
}

function getStatusLabel(status?: number) {
  const statusMap: Record<number, string> = {
    0: '禁用',
    1: '启用',
    2: '待审核',
    3: '审核拒绝',
  };
  return statusMap[status ?? 1] || '未知';
}

function getStatusType(
  status?: number,
): 'error' | 'info' | 'success' | 'warning' {
  const statusTypeMap: Record<
    number,
    'error' | 'info' | 'success' | 'warning'
  > = {
    0: 'error',
    1: 'success',
    2: 'warning',
    3: 'error',
  };
  return statusTypeMap[status ?? 1] || 'info';
}

function handleClose() {
  drawerApi.close();
}

function handleEdit() {
  if (detailData.value) {
    emit('edit', detailData.value);
    handleClose();
  }
}
</script>

<template>
  <Drawer>
    <div
      v-if="detailLoading"
      class="flex items-center justify-center py-20"
    >
      <IconifyIcon
        icon="lucide:loader-2"
        class="size-10 animate-spin"
      />
    </div>
    <NDescriptions
      v-else-if="detailData"
      :column="2"
      label-placement="left"
    >
      <NDescriptionsItem label="用户名">
        {{ detailData.username || '-' }}
      </NDescriptionsItem>
      <NDescriptionsItem label="显示名称">
        {{ detailData.displayName || '-' }}
      </NDescriptionsItem>
      <NDescriptionsItem label="性别">
        {{ getGenderLabel(detailData.gender) }}
      </NDescriptionsItem>
      <NDescriptionsItem label="邮箱">
        {{ detailData.email || '-' }}
      </NDescriptionsItem>
      <NDescriptionsItem label="手机号">
        {{ detailData.phone || '-' }}
      </NDescriptionsItem>
      <NDescriptionsItem label="部门">
        {{ detailData.deptName || '-' }}
      </NDescriptionsItem>
      <NDescriptionsItem label="角色">
        {{ detailData.roleNames || '-' }}
      </NDescriptionsItem>
      <NDescriptionsItem label="状态">
        <Badge
          :variant="
            badgeVariantForTag(getStatusType(detailData.status)) ||
              'secondary'
          "
        >
          {{ getStatusLabel(detailData.status) }}
        </Badge>
      </NDescriptionsItem>
      <NDescriptionsItem
        label="描述"
        :span="2"
      >
        {{ detailData.description || '-' }}
      </NDescriptionsItem>
      <NDescriptionsItem label="创建时间">
        {{ detailData.createTime || '-' }}
      </NDescriptionsItem>
      <NDescriptionsItem label="更新时间">
        {{ detailData.updateTime || '-' }}
      </NDescriptionsItem>
    </NDescriptions>

    <template #footer>
      <div class="flex w-full justify-end gap-2">
        <Button
          type="button"
          variant="outline"
          @click="handleClose"
        >
          关闭
        </Button>
        <Button
          type="button"
          @click="handleEdit"
        >
          <IconifyIcon
            icon="lucide:pencil"
            class="mr-1 size-4"
          />
          编辑
        </Button>
      </div>
    </template>
  </Drawer>
</template>
