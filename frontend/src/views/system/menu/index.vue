<script setup lang="ts">
import type { FormInst, FormRules } from 'naive-ui';

import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { Menu } from '#/api/system/menu';

import { computed, nextTick, reactive, ref } from 'vue';

import { Page } from '@vben/common-ui';
import { IconifyIcon } from '@vben/icons';

import {
  NForm,
  NFormItem,
  NInput,
  NInputNumber,
  NModal,
  NRadio,
  NRadioGroup,
} from 'naive-ui';

import FormTreeSelect from '#/adapter/component/FormTreeSelect.vue';
import { useVbenVxeGrid } from '#/adapter/vxe-table';
import { menuApi } from '#/api/system/menu';
import IconSelect from '#/components/icon-select.vue';
import { useUserStore } from '#/store/user';
import { Badge } from '#/ui/badge';
import { Button } from '#/ui/button';
import { Switch } from '#/ui/switch';
import { toast } from '#/ui-patterns/toast';
import {
  ConfirmAction,
  type ConfirmActionExpose,
} from '#/ui-patterns/confirm-action';

const confirmAction = ref<ConfirmActionExpose | null>(null);
const userStore = useUserStore();

const statusOptions = [
  { label: '启用', value: 1 },
  { label: '禁用', value: 0 },
];

const typeMap: Record<
  number,
  { text: string; variant: 'default' | 'success' | 'warning' }
> = {
  1: { text: '目录', variant: 'default' },
  2: { text: '菜单', variant: 'success' },
  3: { text: '按钮', variant: 'warning' },
};

type MenuRow = Menu & { visible?: number };

const tableData = ref<MenuRow[]>([]);

function presentTree(nodes: Menu[]): MenuRow[] {
  return nodes.map((node) => ({
    ...node,
    children: node.children?.length ? presentTree(node.children) : undefined,
  }));
}

interface MenuOption {
  children?: MenuOption[];
  key: number | string;
  label: string;
}

const menuOptions = computed(() => {
  const options: MenuOption[] = [{ key: 0, label: '顶级菜单' }];

  function convert(menus: Menu[]): MenuOption[] {
    return menus
      .filter((menu) => menu.type !== 3)
      .map((menu) => ({
        key: menu.id,
        label: menu.name,
        children: menu.children ? convert(menu.children) : undefined,
      }));
  }

  options.push(...convert(tableData.value));
  return options;
});

const [Grid, gridApi] = useVbenVxeGrid({
  formOptions: {
    schema: [
      {
        component: 'Input',
        fieldName: 'name',
        label: '菜单名称',
        componentProps: { placeholder: '请输入菜单名称' },
      },
      {
        component: 'Select',
        fieldName: 'status',
        label: '状态',
        componentProps: {
          options: statusOptions,
          placeholder: '请选择状态',
          clearable: true,
        },
      },
    ],
    showCollapseButton: false,
    wrapperClass: 'grid-cols-1 md:grid-cols-2 lg:grid-cols-3',
  },
  gridOptions: {
    columns: [
      {
        field: 'name',
        title: '菜单名称',
        treeNode: true,
        align: 'left',
        minWidth: 220,
        slots: { default: 'name' },
      },
      {
        field: 'type',
        title: '类型',
        width: 90,
        slots: { default: 'type' },
      },
      { field: 'path', title: '路由地址', minWidth: 140 },
      { field: 'component', title: '组件路径', minWidth: 140 },
      { field: 'permission', title: '权限标识', minWidth: 160 },
      { field: 'sort', title: '排序', width: 80 },
      {
        field: 'visible',
        title: '可见',
        width: 90,
        slots: { default: 'visible' },
      },
      {
        field: 'status',
        title: '状态',
        width: 90,
        slots: { default: 'status' },
      },
      {
        field: 'action',
        title: '操作',
        width: 180,
        fixed: 'right',
        slots: { default: 'action' },
      },
    ],
    height: 'auto',
    keepSource: true,
    pagerConfig: { enabled: false },
    rowConfig: { keyField: 'id' },
    treeConfig: {
      childrenField: 'children',
      expandAll: true,
      rowField: 'id',
    },
    toolbarConfig: {
      custom: true,
      refresh: true,
      zoom: true,
    },
    proxyConfig: {
      ajax: {
        query: async (_page, formValues) => {
          const name =
            typeof formValues?.name === 'string' ? formValues.name.trim() : '';
          const status =
            typeof formValues?.status === 'number' ? formValues.status : undefined;
          const res = await menuApi.tree({
            title: name || undefined,
            status,
          });
          tableData.value = presentTree(res ?? []);
          return { records: tableData.value, total: tableData.value.length };
        },
        querySuccess: async () => {
          await nextTick();
          gridApi.grid?.setAllTreeExpand?.(true);
        },
      },
    },
  } as VxeTableGridOptions<MenuRow>,
});

const modalVisible = ref(false);
const modalTitle = ref('新增菜单');
const formRef = ref<FormInst | null>(null);
const submitLoading = ref(false);

const formData = reactive({
  id: undefined as string | undefined,
  parentId: '0' as number | string,
  name: '',
  type: 1,
  path: '',
  component: '',
  permission: '',
  icon: '',
  sort: 0,
  visible: 1,
  status: 1,
  isFrame: 0,
});

const rules: FormRules = {
  name: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  type: [
    {
      required: true,
      type: 'number',
      message: '请选择菜单类型',
      trigger: 'change',
    },
  ],
};

function handleAdd(parentId?: string) {
  modalTitle.value = '新增菜单';
  Object.assign(formData, {
    id: undefined,
    parentId: parentId || '0',
    name: '',
    type: 1,
    path: '',
    component: '',
    permission: '',
    icon: '',
    sort: 0,
    visible: 1,
    status: 1,
    isFrame: 0,
  });
  modalVisible.value = true;
}

function handleEdit(row: MenuRow) {
  modalTitle.value = '编辑菜单';
  Object.assign(formData, { ...row });
  modalVisible.value = true;
}

async function handleSubmit() {
  try {
    await formRef.value?.validate();
    submitLoading.value = true;

    if (formData.id) {
      await menuApi.update({ ...formData }, formData.id);
      toast.success('更新成功');
    } else {
      await menuApi.create({ ...formData });
      toast.success('创建成功');
    }

    modalVisible.value = false;
    await gridApi.query();
  } catch {
    // 错误已在拦截器处理
  } finally {
    submitLoading.value = false;
  }
}

async function handleDelete(row: MenuRow) {
  const ok = await confirmAction.value?.ask({
    title: '提示',
    description: `确定要删除菜单"${row.name}"吗？`,
    tone: 'destructive',
  });
  if (!ok) return;
  try {
    await menuApi.delete(row.id);
    toast.success('删除成功');
    await gridApi.query();
  } catch {
    // 错误已在拦截器处理
  }
}

function typeText(row: Menu) {
  return typeMap[row.type] ?? { text: '-', variant: 'default' as const };
}
</script>

<template>
  <Page auto-content-height>
    <ConfirmAction ref="confirmAction" />
    <Grid>
      <template #toolbar-tools>
        <Button
          v-if="userStore.hasPermission('system:menu:add')"
          type="button"
          @click="handleAdd()"
        >
          <IconifyIcon
            icon="lucide:plus"
            class="mr-1 size-4"
          />
          新增菜单
        </Button>
      </template>
      <template #name="{ row }">
        <span class="inline-flex items-center gap-1.5">
          <IconifyIcon
            v-if="row.icon"
            :icon="row.icon"
          />
          {{ row.name }}
        </span>
      </template>
      <template #type="{ row }">
        <Badge :variant="typeText(row).variant">
          {{ typeText(row).text }}
        </Badge>
      </template>
      <template #visible="{ row }">
        <span v-if="row.type === 3">-</span>
        <Badge
          v-else
          :variant="row.visible === 1 ? 'success' : 'secondary'"
        >
          {{ row.visible === 1 ? '是' : '否' }}
        </Badge>
      </template>
      <template #status="{ row }">
        <Badge :variant="row.status === 1 ? 'success' : 'destructive'">
          {{ row.status === 1 ? '启用' : '禁用' }}
        </Badge>
      </template>
      <template #action="{ row }">
        <div class="flex items-center justify-center gap-2">
          <Button
            v-if="row.type !== 3 && userStore.hasPermission('system:menu:create')"
            type="button"
            variant="link"
            size="sm"
            class="h-auto px-1"
            @click="handleAdd(row.id)"
          >
            新增
          </Button>
          <Button
            v-if="userStore.hasPermission('system:menu:edit')"
            type="button"
            variant="link"
            size="sm"
            class="h-auto px-1"
            @click="handleEdit(row)"
          >
            编辑
          </Button>
          <Button
            v-if="userStore.hasPermission('system:menu:delete')"
            type="button"
            variant="link"
            size="sm"
            class="text-destructive h-auto px-1"
            @click="handleDelete(row)"
          >
            删除
          </Button>
        </div>
      </template>
    </Grid>

    <NModal
      v-model:show="modalVisible"
      :title="modalTitle"
      preset="card"
      style="width: 650px"
      :mask-closable="false"
    >
      <NForm
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-placement="left"
        label-width="80"
        class="modal-form"
      >
        <NFormItem
          label="上级菜单"
          path="parentId"
        >
          <FormTreeSelect
            v-model:value="formData.parentId"
            :options="menuOptions"
            placeholder="请选择上级菜单"
            default-expand-all
            clearable
          />
        </NFormItem>
        <NFormItem
          label="菜单类型"
          path="type"
        >
          <NRadioGroup v-model:value="formData.type">
            <NRadio :value="1">
              目录
            </NRadio>
            <NRadio :value="2">
              菜单
            </NRadio>
            <NRadio :value="3">
              按钮
            </NRadio>
          </NRadioGroup>
        </NFormItem>
        <NFormItem
          label="菜单名称"
          path="name"
        >
          <NInput
            v-model:value="formData.name"
            placeholder="请输入菜单名称"
          />
        </NFormItem>
        <NFormItem
          v-if="formData.type !== 3"
          label="是否外链"
          path="isFrame"
        >
          <div class="flex items-center gap-2">
            <Switch
              v-model="formData.isFrame"
              :checked-value="1"
              :unchecked-value="0"
            />
            <span class="text-sm">{{
              formData.isFrame === 1 ? '是' : '否'
            }}</span>
            <span class="text-xs text-muted-foreground">
              外链点击后将在新窗口打开
            </span>
          </div>
        </NFormItem>
        <NFormItem
          v-if="formData.type !== 3 && !formData.isFrame"
          label="路由地址"
          path="path"
        >
          <NInput
            v-model:value="formData.path"
            placeholder="请输入路由地址"
          />
        </NFormItem>
        <NFormItem
          v-if="formData.type !== 3 && formData.isFrame"
          label="外链地址"
          path="component"
        >
          <NInput
            v-model:value="formData.component"
            placeholder="请输入外链地址，如：https://example.com"
          />
        </NFormItem>
        <NFormItem
          v-if="formData.type === 2 && !formData.isFrame"
          label="组件路径"
          path="component"
        >
          <NInput
            v-model:value="formData.component"
            placeholder="请输入组件路径"
          />
        </NFormItem>
        <NFormItem
          v-if="formData.type === 3"
          label="权限标识"
          path="permission"
        >
          <NInput
            v-model:value="formData.permission"
            placeholder="请输入权限标识，如：system:user:create"
          />
        </NFormItem>
        <NFormItem
          v-if="formData.type !== 3"
          label="图标"
          path="icon"
        >
          <IconSelect v-model="formData.icon" />
        </NFormItem>
        <NFormItem
          label="排序"
          path="sort"
        >
          <NInputNumber
            v-model:value="formData.sort"
            :min="0"
            style="width: 100%"
          />
        </NFormItem>
        <NFormItem
          v-if="formData.type !== 3"
          label="是否可见"
          path="visible"
        >
          <div class="flex items-center gap-2">
            <Switch
              v-model="formData.visible"
              :checked-value="1"
              :unchecked-value="0"
            />
            <span class="text-sm">
              {{ formData.visible === 1 ? '显示' : '隐藏' }}
            </span>
          </div>
        </NFormItem>
        <NFormItem
          label="状态"
          path="status"
        >
          <div class="flex items-center gap-2">
            <Switch
              v-model="formData.status"
              :checked-value="1"
              :unchecked-value="0"
            />
            <span class="text-sm">
              {{ formData.status === 1 ? '启用' : '禁用' }}
            </span>
          </div>
        </NFormItem>
      </NForm>
      <template #footer>
        <div class="flex justify-end gap-2">
          <Button
            type="button"
            variant="outline"
            @click="modalVisible = false"
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
    </NModal>
  </Page>
</template>
