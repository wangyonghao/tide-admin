<script setup lang="ts">
import type { RoleDetailResp } from '#/api/system/role';

import { computed, ref, watch } from 'vue';

import { IconifyIcon } from '@vben/icons';
import { $t } from '@vben/locales';

import { roleApi } from '#/api/system/role';
import { Button } from '#/ui/button';
import { Checkbox } from '#/ui/checkbox';
import { toast } from '#/ui/sonner';

import {
  collectNodeKeys,
  countChecked,
  isAllMenusChecked,
  isSomeMenusChecked,
  permissionSavePayload,
  processMenuTree,
  setCheckedByKeys,
  toggleAllCheck,
  toggleMenuCheck,
  togglePermissionCheck,
  visibleMenuRows,
  type MenuNode,
  type Permission,
  type RawMenu,
} from './permission-tree';

interface Props {
  roleId?: string;
  roleDetail?: null | Partial<RoleDetailResp>;
  menuTree?: RawMenu[];
  selectKeys?: string[];
}

const props = withDefaults(defineProps<Props>(), {
  roleId: undefined,
  roleDetail: null,
  menuTree: () => [],
  selectKeys: () => [],
});

const emits = defineEmits(['refresh']);

const localRoleId = ref<string>();
const localRoleDetail = ref<RoleDetailResp>();
const localMenuTree = ref<MenuNode[]>([]);
const selectedMenuIds = ref<Set<string>>(new Set());
const expandedRowKeys = ref<string[]>([]);
const saving = ref(false);

function commitSelection(next: Set<string>) {
  selectedMenuIds.value = new Set(next);
}

function initData() {
  if (props.roleId != null && props.roleId !== '') {
    localRoleId.value = props.roleId;
  }
  if (props.roleDetail) {
    localRoleDetail.value = props.roleDetail as RoleDetailResp;
  }
  if (props.menuTree && props.menuTree.length > 0) {
    localMenuTree.value = processMenuTree(props.menuTree);
  }
  if (props.selectKeys && props.selectKeys.length > 0) {
    commitSelection(
      setCheckedByKeys(localMenuTree.value, props.selectKeys.map(String)),
    );
  }
  expandedRowKeys.value = collectNodeKeys(localMenuTree.value);
}

async function handleSave() {
  if (!localRoleId.value || !localRoleDetail.value) return;

  saving.value = true;
  try {
    await roleApi.updatePermission(
      localRoleId.value.toString(),
      permissionSavePayload(
        localMenuTree.value,
        selectedMenuIds.value,
        localRoleDetail.value.menuCheckStrictly,
      ),
    );
    toast.success($t('system.role.saveSuccess'));
    emits('refresh');
  } catch (error) {
    console.error(error);
  } finally {
    saving.value = false;
  }
}

watch(
  () => [props.roleId, props.roleDetail, props.menuTree, props.selectKeys],
  () => {
    initData();
  },
  { immediate: true, deep: true },
);

const expandAll = () => {
  expandedRowKeys.value = collectNodeKeys(localMenuTree.value);
};

const collapseAll = () => {
  expandedRowKeys.value = [];
};

function handleMenuCheckStrictlyChange(value: boolean) {
  if (localRoleDetail.value) {
    localRoleDetail.value.menuCheckStrictly = value;
  }
}

const checkedCount = computed(() =>
  countChecked(localMenuTree.value, selectedMenuIds.value),
);

const allChecked = computed(() => isAllMenusChecked(localMenuTree.value));

const someChecked = computed(() => isSomeMenusChecked(localMenuTree.value));

const expandedIds = computed(() => new Set(expandedRowKeys.value));

const rows = computed(() =>
  visibleMenuRows(localMenuTree.value, expandedIds.value),
);

function onToggleAll(checked: boolean | 'indeterminate') {
  toggleAllCheck(localMenuTree.value, checked === true, selectedMenuIds.value);
  commitSelection(selectedMenuIds.value);
}

function onToggleMenu(node: MenuNode, checked: boolean | 'indeterminate') {
  toggleMenuCheck(
    node,
    checked === true,
    selectedMenuIds.value,
    Boolean(localRoleDetail.value?.menuCheckStrictly),
  );
  commitSelection(selectedMenuIds.value);
}

function onTogglePermission(
  node: MenuNode,
  permission: Permission,
  checked: boolean | 'indeterminate',
) {
  togglePermissionCheck(
    node,
    permission,
    checked === true,
    selectedMenuIds.value,
    Boolean(localRoleDetail.value?.menuCheckStrictly),
  );
  commitSelection(selectedMenuIds.value);
}

function toggleExpand(id: string) {
  expandedRowKeys.value = expandedRowKeys.value.includes(id)
    ? expandedRowKeys.value.filter((key) => key !== id)
    : [...expandedRowKeys.value, id];
}
</script>

<template>
  <div class="flex h-full flex-col">
    <div
      class="flex items-center justify-between rounded-t-lg border-b bg-card px-3 py-3"
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
          <label class="flex cursor-pointer items-center gap-1.5">
            <Checkbox
              :model-value="localRoleDetail?.menuCheckStrictly === true"
              @update:model-value="
                (checked) => handleMenuCheckStrictlyChange(checked === true)
              "
            />
            <span>{{
              localRoleDetail?.menuCheckStrictly ? '已启用' : '已禁用'
            }}</span>
          </label>
        </div>
        <Button
          type="button"
          size="sm"
          variant="outline"
          title="折叠全部"
          @click="collapseAll"
        >
          <IconifyIcon icon="lucide:chevrons-up" class="mr-1 size-4" />
          折叠全部
        </Button>
        <Button
          type="button"
          size="sm"
          variant="outline"
          title="展开全部"
          @click="expandAll"
        >
          <IconifyIcon icon="lucide:chevrons-down" class="mr-1 size-4" />
          展开全部
        </Button>
        <Button type="button" size="sm" :loading="saving" @click="handleSave">
          <IconifyIcon icon="lucide:save" class="mr-1 size-4" />
          保存
        </Button>
      </div>
    </div>

    <div class="flex-1 overflow-auto" style="max-height: calc(100vh - 280px)">
      <table class="w-full min-w-[800px] border-collapse text-sm">
        <thead class="sticky top-0 z-10 bg-card">
          <tr class="border-b border-border">
            <th class="w-12 px-3 py-2 text-center font-semibold">
              <Checkbox
                :model-value="
                  allChecked ? true : someChecked ? 'indeterminate' : false
                "
                :indeterminate="someChecked"
                @update:model-value="onToggleAll"
              />
            </th>
            <th class="w-44 px-3 py-2 text-left font-semibold">菜单</th>
            <th class="px-3 py-2 text-left font-semibold">权限</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="row in rows"
            :key="row.node.id"
            class="border-b border-border"
          >
            <td class="px-3 py-2 text-center">
              <Checkbox
                :model-value="row.node.checked === true"
                @update:model-value="
                  (checked) => onToggleMenu(row.node, checked)
                "
              />
            </td>
            <td class="px-3 py-2">
              <div
                class="flex min-w-0 items-center gap-2"
                :style="{ paddingLeft: `${row.depth * 16}px` }"
              >
                <button
                  v-if="row.hasChildren"
                  type="button"
                  class="text-muted-foreground inline-flex size-4 shrink-0 items-center justify-center"
                  :aria-expanded="expandedIds.has(row.node.id)"
                  @click="toggleExpand(row.node.id)"
                >
                  <IconifyIcon
                    :icon="
                      expandedIds.has(row.node.id)
                        ? 'lucide:chevron-down'
                        : 'lucide:chevron-right'
                    "
                    class="size-3.5"
                  />
                </button>
                <span v-else class="inline-block size-4 shrink-0"></span>
                <IconifyIcon
                  v-if="row.node.icon"
                  :icon="row.node.icon"
                  class="size-4 shrink-0"
                />
                <span class="truncate">{{ row.node.label }}</span>
              </div>
            </td>
            <td class="px-3 py-2">
              <div
                v-if="row.node.permissions?.length"
                class="flex flex-wrap gap-x-4 gap-y-2"
              >
                <label
                  v-for="perm in row.node.permissions"
                  :key="perm.id"
                  class="flex cursor-pointer items-center gap-1.5 whitespace-nowrap"
                >
                  <Checkbox
                    :model-value="perm.checked"
                    @update:model-value="
                      (checked) => onTogglePermission(row.node, perm, checked)
                    "
                  />
                  <span>{{ perm.label }}</span>
                </label>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
