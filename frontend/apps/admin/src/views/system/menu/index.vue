<script setup lang="ts">
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { Menu } from '#/api/system/menu';

import { computed, nextTick, ref } from 'vue';

import { Page, useVbenModal } from '@vben/common-ui';
import { IconifyIcon } from '@vben/icons';

import { useVbenVxeGrid } from '#/adapter/vxe-table';
import { menuApi } from '#/api/system/menu';
import { useUserStore } from '#/store/user';
import { Badge } from '#/ui/badge';
import { Button } from '#/ui/button';
import { toast } from '#/ui-patterns/toast';
import {
  ConfirmAction,
  type ConfirmActionExpose,
} from '#/ui-patterns/confirm-action';

import MenuForm from './modules/form.vue';

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
  const options: MenuOption[] = [{ key: '0', label: '顶级菜单' }];

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

const [MenuModal, menuModalApi] = useVbenModal({
  connectedComponent: MenuForm,
  destroyOnClose: true,
});

function handleAdd(parentId?: string) {
  menuModalApi
    .setData({
      parentId: parentId || '0',
      menuOptions: menuOptions.value,
    })
    .open();
}

function handleEdit(row: MenuRow) {
  menuModalApi
    .setData({
      record: row,
      menuOptions: menuOptions.value,
    })
    .open();
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

    <MenuModal @success="gridApi.query()" />
  </Page>
</template>
