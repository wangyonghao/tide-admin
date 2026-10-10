import type { MenuRecord } from '../types';

export function childrenOf(
  nodes: MenuRecord[],
  appId: string,
  parentId: null | string,
  kinds: MenuRecord['type'][] = ['folder', 'function'],
) {
  return nodes
    .filter(
      (node) =>
        node.appId === appId &&
        node.parentId === parentId &&
        kinds.includes(node.type),
    )
    .sort((a, b) => a.sort - b.sort || a.name.localeCompare(b.name));
}

export function isDescendant(
  nodes: MenuRecord[],
  ancestorId: string,
  id: string,
) {
  let current = nodes.find((node) => node.id === id);
  const guard = new Set<string>();
  while (current?.parentId) {
    if (guard.has(current.id)) return false;
    guard.add(current.id);
    if (current.parentId === ancestorId) return true;
    current = nodes.find((node) => node.id === current?.parentId);
  }
  return false;
}

export type DropIssue = 'cross-app' | 'descendant' | 'into-button' | 'into-function' | 'self';

/** 不能拖到按钮或功能下，也不能拖进自己的子孙。目录可以接收其他模块的节点。 */
export function dropIssue(
  nodes: MenuRecord[],
  dragId: string,
  nextParentId: null | string,
): DropIssue | null {
  const drag = nodes.find((node) => node.id === dragId);
  if (!drag || drag.type === 'button') return 'self';
  if (nextParentId === dragId) return 'self';
  if (nextParentId && isDescendant(nodes, dragId, nextParentId)) {
    return 'descendant';
  }
  if (!nextParentId) return null;
  const parent = nodes.find((node) => node.id === nextParentId);
  if (!parent) return 'self';
  if (parent.type === 'button') return 'into-button';
  if (parent.type === 'function') return 'into-function';
  return null;
}

export function matchesQuery(node: MenuRecord, query: string) {
  const q = query.trim().toLowerCase();
  if (!q) return true;
  return [node.name, node.path, node.permission]
    .join(' ')
    .toLowerCase()
    .includes(q);
}

/** 命中节点及其祖先保留，便于在过滤结果里仍能看到层级。 */
export function filterTree(
  nodes: MenuRecord[],
  appId: string,
  query: string,
) {
  const treeNodes = nodes.filter(
    (node) => node.appId === appId && node.type !== 'button',
  );
  if (!query.trim()) return treeNodes;
  const keep = new Set<string>();
  for (const node of treeNodes) {
    if (!matchesQuery(node, query)) continue;
    keep.add(node.id);
    let parentId = node.parentId;
    while (parentId) {
      keep.add(parentId);
      parentId = treeNodes.find((item) => item.id === parentId)?.parentId ?? null;
    }
  }
  return treeNodes.filter((node) => keep.has(node.id));
}

export function ancestorIds(nodes: MenuRecord[], id: string) {
  const ids: string[] = [];
  let current = nodes.find((node) => node.id === id);
  while (current?.parentId) {
    ids.push(current.parentId);
    current = nodes.find((node) => node.id === current?.parentId);
  }
  return ids;
}
