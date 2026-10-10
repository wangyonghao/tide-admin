import { describe, expect, it } from 'vitest';

import {
  collectNodeKeys,
  getAllCheckedIds,
  isAllMenusChecked,
  permissionSavePayload,
  processMenuTree,
  setCheckedByKeys,
  toggleAllCheck,
  toggleMenuCheck,
  togglePermissionCheck,
  visibleMenuRows,
  type MenuNode,
  type RawMenu,
} from './permission-tree';

const rawMenus: RawMenu[] = [
  {
    id: 1,
    title: '系统',
    type: 1,
    children: [
      {
        id: 2,
        title: '用户',
        type: 2,
        children: [
          { id: 21, title: '查询', type: 3 },
          { id: 22, title: '新增', type: 3 },
          { id: 23, title: '混在按钮里的菜单', type: 2 },
        ],
      },
      {
        id: 3,
        title: '日志',
        type: 2,
        children: [{ id: 31, title: '登录日志', type: 2 }],
      },
    ],
  },
];

function tree() {
  return processMenuTree(rawMenus);
}

function nodeById(nodes: MenuNode[], id: string): MenuNode {
  for (const node of nodes) {
    if (node.id === id) return node;
    const child = node.children?.length
      ? nodeById(node.children, id)
      : undefined;
    if (child) return child;
  }
  throw new Error(`missing ${id}`);
}

describe('role permission save payload', () => {
  it('turns button children into permissions and keeps real submenus', () => {
    const nodes = tree();
    const user = nodeById(nodes, '2');
    const log = nodeById(nodes, '3');

    expect(user.children).toEqual([]);
    expect(user.permissions?.map((perm) => perm.id)).toEqual([
      '21',
      '22',
      '23',
    ]);
    expect(log.permissions).toEqual([]);
    expect(log.children?.map((child) => child.id)).toEqual(['31']);
  });

  it('keeps a menu and its permissions independent when association is off', () => {
    const nodes = tree();
    const selected = new Set<string>();
    const user = nodeById(nodes, '2');
    const query = user.permissions?.[0];
    if (!query) throw new Error('missing query');

    toggleMenuCheck(user, true, selected, false);
    expect(getAllCheckedIds(nodes, selected)).toEqual(['2']);

    toggleMenuCheck(user, false, selected, false);
    togglePermissionCheck(user, query, true, selected, false);
    expect(permissionSavePayload(nodes, selected, false)).toEqual({
      menuIds: ['21'],
      menuCheckStrictly: false,
    });
  });

  it('does not clear an already checked permission when association is off', () => {
    const nodes = tree();
    const selected = setCheckedByKeys(nodes, ['2', '21']);
    toggleMenuCheck(nodeById(nodes, '2'), false, selected, false);

    expect(permissionSavePayload(nodes, selected, false).menuIds).toEqual([
      '21',
    ]);
  });

  it('checks descendant menus and permissions when association is on', () => {
    const nodes = tree();
    const selected = new Set<string>();
    toggleMenuCheck(nodeById(nodes, '1'), true, selected, true);

    expect(permissionSavePayload(nodes, selected, true)).toEqual({
      menuIds: ['1', '2', '21', '22', '23', '3', '31'],
      menuCheckStrictly: true,
    });
  });

  it('checks only the permission menu, not its ancestors', () => {
    const nodes = tree();
    const selected = new Set<string>();
    const user = nodeById(nodes, '2');
    const query = user.permissions?.[0];
    if (!query) throw new Error('missing query');

    togglePermissionCheck(user, query, true, selected, true);
    expect(getAllCheckedIds(nodes, selected)).toEqual(['2', '21']);

    togglePermissionCheck(user, query, false, selected, true);
    expect(getAllCheckedIds(nodes, selected)).toEqual(['2']);
  });

  it('does not pull the parent in when a child menu is checked', () => {
    const nodes = tree();
    const selected = new Set<string>();
    toggleMenuCheck(nodeById(nodes, '31'), true, selected, true);
    expect(getAllCheckedIds(nodes, selected)).toEqual(['31']);
  });

  it('applies saved keys without cascading, even if association is on', () => {
    const nodes = tree();
    const selected = setCheckedByKeys(nodes, ['31', '21']);
    expect(permissionSavePayload(nodes, selected, true)).toEqual({
      menuIds: ['21', '31'],
      menuCheckStrictly: true,
    });
  });

  it('selects every menu and permission from the header, then clears them', () => {
    const nodes = tree();
    const selected = new Set<string>();
    toggleAllCheck(nodes, true, selected);
    expect(getAllCheckedIds(nodes, selected)).toEqual([
      '1',
      '2',
      '21',
      '22',
      '23',
      '3',
      '31',
    ]);

    toggleAllCheck(nodes, false, selected);
    expect(getAllCheckedIds(nodes, selected)).toEqual([]);
  });

  it('treats the header as fully checked when every menu is on, even if a permission is off', () => {
    const nodes = tree();
    setCheckedByKeys(nodes, ['1', '2', '3', '31', '22']);
    expect(isAllMenusChecked(nodes)).toBe(true);
  });

  it('hides collapsed rows without dropping them from the save payload', () => {
    const nodes = tree();
    const selected = setCheckedByKeys(nodes, ['1', '2', '21', '3', '31']);
    const collapsed = visibleMenuRows(nodes, new Set());
    expect(collapsed.map((row) => row.node.id)).toEqual(['1']);
    expect(getAllCheckedIds(nodes, selected)).toEqual([
      '1',
      '2',
      '21',
      '3',
      '31',
    ]);

    const expanded = visibleMenuRows(nodes, new Set(collectNodeKeys(nodes)));
    expect(expanded.map((row) => row.node.id)).toEqual(['1', '2', '3', '31']);
  });
});
