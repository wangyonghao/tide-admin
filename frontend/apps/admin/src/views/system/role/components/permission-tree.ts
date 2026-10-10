/**
 * 角色权限矩阵的勾选与保存。
 * menuIds 只包含已选菜单 id，以及 checked 的权限 id。
 * menuCheckStrictly 为真时，勾选菜单会连带下级菜单和权限；勾选权限会带上它所在的菜单。
 * 取消权限不会取消菜单。关闭关联时，菜单和权限各自独立。
 * 这个标志本身不参与重算，保存时原样带回。
 */

export interface Permission {
  id: string;
  label: string;
  checked: boolean;
}

export interface MenuNode {
  id: string;
  label: string;
  title?: string;
  name?: string;
  children?: MenuNode[];
  permissions?: Permission[];
  type?: number;
  icon?: string;
  checked?: boolean;
}

export interface RawMenu {
  id: unknown;
  title?: string;
  name?: string;
  type?: number;
  icon?: string;
  children?: RawMenu[];
}

export interface VisibleMenuRow {
  node: MenuNode;
  depth: number;
  hasChildren: boolean;
}

export function processMenuTree(menus: RawMenu[]): MenuNode[] {
  return menus.map((menu) => {
    const node: MenuNode = {
      id: String(menu.id),
      label: menu.title || menu.name || '',
      title: menu.title,
      name: menu.name,
      type: menu.type,
      icon: menu.icon,
      permissions: [],
      children: [],
      checked: false,
    };

    if (menu.type === 2 && menu.children && menu.children.length > 0) {
      const hasButton = menu.children.some((child) => child.type === 3);

      if (hasButton) {
        node.permissions = menu.children.map((child) => ({
          id: String(child.id),
          label: child.title || child.name || '',
          checked: false,
        }));
      } else {
        node.children = processMenuTree(menu.children);
      }
    } else if (menu.children && menu.children.length > 0) {
      node.children = processMenuTree(menu.children);
    }

    return node;
  });
}

export function getAllCheckedIds(
  nodes: MenuNode[],
  selectedMenuIds: Set<string>,
): string[] {
  const ids: string[] = [];

  function collectIds(list: MenuNode[]) {
    list.forEach((node) => {
      if (selectedMenuIds.has(node.id)) {
        ids.push(node.id);
      }

      node.permissions?.forEach((perm) => {
        if (perm.checked) {
          ids.push(perm.id);
        }
      });

      if (node.children && node.children.length > 0) {
        collectIds(node.children);
      }
    });
  }

  collectIds(nodes);
  return ids;
}

export function permissionSavePayload(
  nodes: MenuNode[],
  selectedMenuIds: Set<string>,
  menuCheckStrictly: boolean,
) {
  return {
    menuIds: getAllCheckedIds(nodes, selectedMenuIds),
    menuCheckStrictly,
  };
}

export function setCheckedByKeys(
  nodes: MenuNode[],
  keys: string[],
): Set<string> {
  const keySet = new Set(keys.map(String));
  const selectedMenuIds = new Set<string>();

  function updateNodes(list: MenuNode[]) {
    list.forEach((node) => {
      if (keySet.has(node.id)) {
        selectedMenuIds.add(node.id);
        node.checked = true;
      } else {
        node.checked = false;
      }

      node.permissions?.forEach((perm) => {
        perm.checked = keySet.has(perm.id);
      });

      if (node.children && node.children.length > 0) {
        updateNodes(node.children);
      }
    });
  }

  updateNodes(nodes);
  return selectedMenuIds;
}

export function toggleMenuCheck(
  node: MenuNode,
  checked: boolean,
  selectedMenuIds: Set<string>,
  menuCheckStrictly: boolean,
) {
  node.checked = checked;
  if (checked) {
    selectedMenuIds.add(node.id);
  } else {
    selectedMenuIds.delete(node.id);
  }

  if (menuCheckStrictly) {
    toggleChildrenCheck(node, checked, selectedMenuIds);
    node.permissions?.forEach((perm) => {
      perm.checked = checked;
    });
  }
}

export function toggleChildrenCheck(
  node: MenuNode,
  checked: boolean,
  selectedMenuIds: Set<string>,
) {
  if (node.children && node.children.length > 0) {
    node.children.forEach((child) => {
      child.checked = checked;
      if (checked) {
        selectedMenuIds.add(child.id);
      } else {
        selectedMenuIds.delete(child.id);
      }

      child.permissions?.forEach((perm) => {
        perm.checked = checked;
      });

      toggleChildrenCheck(child, checked, selectedMenuIds);
    });
  }
}

export function togglePermissionCheck(
  node: MenuNode,
  permission: Permission,
  checked: boolean,
  selectedMenuIds: Set<string>,
  menuCheckStrictly: boolean,
) {
  permission.checked = checked;

  if (menuCheckStrictly && checked) {
    node.checked = true;
    selectedMenuIds.add(node.id);
  }
}

export function countChecked(
  nodes: MenuNode[],
  selectedMenuIds: Set<string>,
): number {
  let count = selectedMenuIds.size;

  function countPermissions(list: MenuNode[]) {
    list.forEach((node) => {
      if (node.permissions) {
        count += node.permissions.filter((perm) => perm.checked).length;
      }
      if (node.children && node.children.length > 0) {
        countPermissions(node.children);
      }
    });
  }

  countPermissions(nodes);
  return count;
}

export function collectNodeKeys(nodes: MenuNode[]): string[] {
  const keys: string[] = [];

  function collect(list: MenuNode[]) {
    list.forEach((node) => {
      keys.push(node.id);
      if (node.children && node.children.length > 0) {
        collect(node.children);
      }
    });
  }

  collect(nodes);
  return keys;
}

function everyMenuChecked(list: MenuNode[]): boolean {
  return list.every((node) => {
    const nodeChecked = node.checked || false;
    const childrenChecked =
      node.children && node.children.length > 0
        ? everyMenuChecked(node.children)
        : true;
    return nodeChecked && childrenChecked;
  });
}

export function isAllMenusChecked(nodes: MenuNode[]): boolean {
  if (nodes.length === 0) return false;
  return everyMenuChecked(nodes);
}

function someMenuChecked(list: MenuNode[]): boolean {
  return list.some((node) => {
    if (node.checked) return true;
    if (node.children && node.children.length > 0) {
      return someMenuChecked(node.children);
    }
    return false;
  });
}

export function isSomeMenusChecked(nodes: MenuNode[]): boolean {
  if (nodes.length === 0) return false;
  if (isAllMenusChecked(nodes)) return false;
  return someMenuChecked(nodes);
}

export function toggleAllCheck(
  nodes: MenuNode[],
  checked: boolean,
  selectedMenuIds: Set<string>,
) {
  function updateAll(list: MenuNode[]) {
    list.forEach((node) => {
      node.checked = checked;
      if (checked) {
        selectedMenuIds.add(node.id);
      } else {
        selectedMenuIds.delete(node.id);
      }

      node.permissions?.forEach((perm) => {
        perm.checked = checked;
      });

      if (node.children && node.children.length > 0) {
        updateAll(node.children);
      }
    });
  }

  updateAll(nodes);
}

export function visibleMenuRows(
  nodes: MenuNode[],
  expandedIds: ReadonlySet<string>,
  depth = 0,
): VisibleMenuRow[] {
  const rows: VisibleMenuRow[] = [];
  nodes.forEach((node) => {
    const children = node.children ?? [];
    const hasChildren = children.length > 0;
    rows.push({ node, depth, hasChildren });
    if (hasChildren && expandedIds.has(node.id)) {
      rows.push(...visibleMenuRows(children, expandedIds, depth + 1));
    }
  });
  return rows;
}
