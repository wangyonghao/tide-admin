<script setup lang="ts">
import type { DataTableColumns } from 'naive-ui';

import type { RoleDetailResp } from '#/api/system/role';

import { computed, h, ref, watch } from 'vue';

import { IconifyIcon } from '@vben/icons';
import { $t } from '@vben/locales';

import { NButton, NCheckbox, NDataTable, useMessage } from 'naive-ui';

import { roleApi } from '#/api/system/role';

interface Permission {
  id: number | string;
  label: string;
  checked: boolean;
}

interface MenuNode {
  id: number | string;
  label: string;
  title?: string;
  name?: string;
  children?: MenuNode[];
  permissions?: Permission[];
  type?: number;
  icon?: string;
  checked?: boolean;
}

interface Props {
  roleId?: number | string;
  roleDetail?: null | Partial<RoleDetailResp>;
  menuTree?: any[];
  selectKeys?: any[];
}

const props = withDefaults(defineProps<Props>(), {
  roleId: undefined,
  roleDetail: null,
  menuTree: () => [],
  selectKeys: () => [],
});

const emits = defineEmits(['refresh']);

const message = useMessage();

// 本地状态
const localRoleId = ref<number | string>();
const localRoleDetail = ref<RoleDetailResp>();
const localMenuTree = ref<MenuNode[]>([]);
const selectedMenuIds = ref<Set<number | string>>(new Set());
const expandedRowKeys = ref<Array<number | string>>([]);
const saving = ref(false);

// 处理菜单树数据，提取权限
function processMenuTree(menus: any[]): MenuNode[] {
  return menus.map((menu) => {
    const node: MenuNode = {
      id: menu.id,
      label: menu.title || menu.name || '',
      title: menu.title,
      name: menu.name,
      type: menu.type,
      icon: menu.icon,
      permissions: [],
      children: [],
      checked: false,
    };

    // 如果是菜单类型(type=2)且有子节点
    if (menu.type === 2 && menu.children && menu.children.length > 0) {
      // 检查是否有按钮类型的子节点
      const hasButton = menu.children.some((child: any) => child.type === 3);

      if (hasButton) {
        // 如果包含按钮，将所有子节点都作为权限
        node.permissions = menu.children.map((child: any) => ({
          id: child.id,
          label: child.title || child.name || '',
          checked: false,
        }));
      } else {
        // 否则递归处理子节点
        node.children = processMenuTree(menu.children);
      }
    } else if (menu.children && menu.children.length > 0) {
      // 其他类型（目录等）递归处理子节点
      node.children = processMenuTree(menu.children);
    }

    return node;
  });
}

// 获取所有选中的ID（包括菜单和权限）
function getAllCheckedIds(): Array<number | string> {
  const ids: Array<number | string> = [];

  function collectIds(nodes: MenuNode[]) {
    nodes.forEach((node) => {
      if (selectedMenuIds.value.has(node.id)) {
        ids.push(node.id);
      }

      if (node.permissions) {
        node.permissions.forEach((perm) => {
          if (perm.checked) {
            ids.push(perm.id);
          }
        });
      }

      if (node.children && node.children.length > 0) {
        collectIds(node.children);
      }
    });
  }

  collectIds(localMenuTree.value);
  return ids;
}

// 根据keys设置选中状态
function setCheckedByKeys(keys: Array<number | string>) {
  selectedMenuIds.value = new Set();

  function updateNodes(nodes: MenuNode[]) {
    nodes.forEach((node) => {
      if (keys.includes(node.id)) {
        selectedMenuIds.value.add(node.id);
        node.checked = true;
      } else {
        node.checked = false;
      }

      if (node.permissions) {
        node.permissions.forEach((perm) => {
          perm.checked = keys.includes(perm.id);
        });
      }

      if (node.children && node.children.length > 0) {
        updateNodes(node.children);
      }
    });
  }

  updateNodes(localMenuTree.value);
}

// 切换菜单选中状态
function toggleMenuCheck(node: MenuNode, checked: boolean) {
  node.checked = checked;
  if (checked) {
    selectedMenuIds.value.add(node.id);
  } else {
    selectedMenuIds.value.delete(node.id);
  }

  // 如果是节点关联模式
  if (localRoleDetail.value?.menuCheckStrictly) {
    // 选中/取消选中所有子节点
    toggleChildrenCheck(node, checked);
    // 选中/取消选中所有权限
    if (node.permissions) {
      node.permissions.forEach((perm) => {
        perm.checked = checked;
      });
    }
  }
}

// 递归切换子节点选中状态
function toggleChildrenCheck(node: MenuNode, checked: boolean) {
  if (node.children && node.children.length > 0) {
    node.children.forEach((child) => {
      child.checked = checked;
      if (checked) {
        selectedMenuIds.value.add(child.id);
      } else {
        selectedMenuIds.value.delete(child.id);
      }

      // 递归处理子节点的权限
      if (child.permissions) {
        child.permissions.forEach((perm) => {
          perm.checked = checked;
        });
      }

      toggleChildrenCheck(child, checked);
    });
  }
}

// 切换权限选中状态
function togglePermissionCheck(
  node: MenuNode,
  permission: Permission,
  checked: boolean,
) {
  permission.checked = checked;

  // 如果是节点关联模式，选中权限时也选中菜单
  if (localRoleDetail.value?.menuCheckStrictly && checked) {
    node.checked = true;
    selectedMenuIds.value.add(node.id);
  }
}

// 计算选中数量
const checkedCount = computed(() => {
  let count = selectedMenuIds.value.size;

  function countPermissions(nodes: MenuNode[]) {
    nodes.forEach((node) => {
      if (node.permissions) {
        count += node.permissions.filter((p) => p.checked).length;
      }
      if (node.children && node.children.length > 0) {
        countPermissions(node.children);
      }
    });
  }

  countPermissions(localMenuTree.value);
  return count;
});

// 初始化数据
function initData() {
  if (props.roleId) {
    localRoleId.value = props.roleId;
  }
  if (props.roleDetail) {
    localRoleDetail.value = props.roleDetail as RoleDetailResp;
  }
  if (props.menuTree && props.menuTree.length > 0) {
    localMenuTree.value = processMenuTree(props.menuTree);
  }
  if (props.selectKeys && props.selectKeys.length > 0) {
    setCheckedByKeys(props.selectKeys);
  }
  // 默认展开所有节点
  expandedRowKeys.value = getAllNodeKeys(localMenuTree.value);
}

// 保存权限
async function handleSave() {
  if (!localRoleId.value || !localRoleDetail.value) return;

  saving.value = true;
  try {
    const menuIds = getAllCheckedIds();
    await roleApi.updatePermission(localRoleId.value.toString(), {
      menuIds,
      menuCheckStrictly: localRoleDetail.value.menuCheckStrictly,
    });
    message.success($t('system.role.saveSuccess'));
    emits('refresh');
  } catch (error) {
    console.error(error);
  } finally {
    saving.value = false;
  }
}

// 监听props变化
watch(
  () => [props.roleId, props.roleDetail, props.menuTree, props.selectKeys],
  () => {
    initData();
  },
  { immediate: true, deep: true },
);

// 获取所有节点的key
function getAllNodeKeys(nodes: MenuNode[]): Array<number | string> {
  const keys: Array<number | string> = [];

  function collect(nodes: MenuNode[]) {
    nodes.forEach((node) => {
      keys.push(node.id);
      if (node.children && node.children.length > 0) {
        collect(node.children);
      }
    });
  }

  collect(nodes);
  return keys;
}

// 展开/折叠全部
const expandAll = () => {
  expandedRowKeys.value = getAllNodeKeys(localMenuTree.value);
};

const collapseAll = () => {
  expandedRowKeys.value = [];
};

/**
 * 通过回调更新 无法通过v-model
 * @param value 菜单选择是否严格模式
 */
function handleMenuCheckStrictlyChange(value: boolean) {
  if (localRoleDetail.value) {
    localRoleDetail.value.menuCheckStrictly = value;
  }
}

// 全选/取消全选
const allChecked = computed(() => {
  if (localMenuTree.value.length === 0) return false;

  function checkAllNodes(nodes: MenuNode[]): boolean {
    return nodes.every((node) => {
      const nodeChecked = node.checked || false;
      const childrenChecked =
        node.children && node.children.length > 0
          ? checkAllNodes(node.children)
          : true;
      return nodeChecked && childrenChecked;
    });
  }

  return checkAllNodes(localMenuTree.value);
});

const someChecked = computed(() => {
  if (localMenuTree.value.length === 0) return false;
  if (allChecked.value) return false;

  function hasSomeChecked(nodes: MenuNode[]): boolean {
    return nodes.some((node) => {
      if (node.checked) return true;
      if (node.children && node.children.length > 0) {
        return hasSomeChecked(node.children);
      }
      return false;
    });
  }

  return hasSomeChecked(localMenuTree.value);
});

function toggleAllCheck(checked: boolean) {
  function updateAll(nodes: MenuNode[]) {
    nodes.forEach((node) => {
      node.checked = checked;
      if (checked) {
        selectedMenuIds.value.add(node.id);
      } else {
        selectedMenuIds.value.delete(node.id);
      }

      // 更新权限
      if (node.permissions) {
        node.permissions.forEach((perm) => {
          perm.checked = checked;
        });
      }

      // 递归更新子节点
      if (node.children && node.children.length > 0) {
        updateAll(node.children);
      }
    });
  }

  updateAll(localMenuTree.value);
}

// 表格列配置
const columns = computed<DataTableColumns<MenuNode>>(() => [
  {
    key: 'checkbox',
    width: 50,
    align: 'center',
    title: () => {
      return h(NCheckbox, {
        checked: allChecked.value,
        indeterminate: someChecked.value,
        onUpdateChecked: (checked: boolean) => {
          toggleAllCheck(checked);
        },
      });
    },
    render: (row) => {
      return h(NCheckbox, {
        checked: row.checked,
        onUpdateChecked: (checked: boolean) => {
          toggleMenuCheck(row, checked);
        },
      });
    },
  },
  {
    title: '菜单',
    key: 'label',
    width: 180,
    minWidth: 180,
    tree: true,
    resizable: true,
    ellipsis: { tooltip: true },
    render: (row) => {
      const children = [];

      if (row.icon) {
        children.push(
          h(IconifyIcon, { icon: row.icon, class: 'w-4 h-4 flex-shrink-0' }),
        );
      }
      children.push(h('span', { class: 'truncate' }, row.label));

      return h('div', { class: 'flex items-center gap-2 min-w-0' }, children);
    },
  },
  {
    title: '权限',
    key: 'permissions',
    minWidth: 300,
    render: (row) => {
      if (!row.permissions || row.permissions.length === 0) {
        return null;
      }

      return h(
        'div',
        { class: 'flex flex-wrap gap-x-4 gap-y-2' },
        row.permissions.map((perm) =>
          h(
            'label',
            {
              class:
                'flex cursor-pointer items-center gap-1.5 text-sm whitespace-nowrap',
              onClick: (e: Event) => {
                e.stopPropagation();
              },
            },
            [
              h(NCheckbox, {
                checked: perm.checked,
                onUpdateChecked: (checked: boolean) =>
                  togglePermissionCheck(row, perm, checked),
              }),
              h('span', {}, perm.label),
            ],
          ),
        ),
      );
    },
  },
]);

// 行key
function rowKey(row: MenuNode) {
  return row.id;
}
</script>

<template>
  <div class="h-full flex flex-col">
    <!-- 操作栏 -->
    <div
      class="flex items-center justify-between px-3 py-3 border-b bg-card rounded-t-lg"
    >
      <div class="flex items-center gap-4">
        <div class="rounded-md bg-blue-50 px-3 py-1 text-sm dark:bg-blue-950">
          已选中
          <span class="mx-1 font-semibold text-primary">{{
            checkedCount
          }}</span>
          项
        </div>
      </div>

      <div class="flex items-center gap-2">
        <div class="flex items-center gap-2 text-sm text-muted-foreground">
          <span>节点关联:</span>
          <label class="flex items-center gap-1.5 cursor-pointer">
            <input
              type="checkbox"
              :checked="localRoleDetail?.menuCheckStrictly"
              @change="
                handleMenuCheckStrictlyChange(
                  !localRoleDetail?.menuCheckStrictly,
                )
              "
              class="w-4 h-4 rounded border-gray-300"
            />
            <span>{{
              localRoleDetail?.menuCheckStrictly ? '已启用' : '已禁用'
            }}</span>
          </label>
        </div>
        <NButton size="small" @click="collapseAll" title="折叠全部">
          <template #icon>
            <IconifyIcon icon="lucide:chevrons-up" />
          </template>
          折叠全部
        </NButton>
        <NButton size="small" @click="expandAll" title="展开全部">
          <template #icon>
            <IconifyIcon icon="lucide:chevrons-down" />
          </template>
          展开全部
        </NButton>
        <NButton
          type="primary"
          size="small"
          :loading="saving"
          @click="handleSave"
        >
          <template #icon>
            <IconifyIcon icon="lucide:save" />
          </template>
          保存
        </NButton>
      </div>
    </div>

    <!-- 权限树形表格 -->
    <div class="flex-1 overflow-hidden">
      <NDataTable
        v-model:expanded-row-keys="expandedRowKeys"
        :columns="columns"
        :data="localMenuTree"
        :row-key="rowKey"
        :pagination="false"
        :bordered="false"
        max-height="calc(100vh - 280px)"
        :scroll-x="800"
        size="small"
        class="permission-tree-table"
      />
    </div>
  </div>
</template>

<style scoped>
:deep(.permission-tree-table .n-data-table-td) {
  padding: 8px 12px;
}

:deep(.permission-tree-table .n-data-table-th) {
  font-weight: 600;
}

/* 确保树形结构的缩进正确显示 */
:deep(.permission-tree-table .n-data-table-td--tree-col) {
  padding-left: 12px !important;
}

/* 树形展开按钮样式 */
:deep(.permission-tree-table .n-data-table-expand-trigger) {
  margin-right: 8px;
}
</style>
