import type { MenuRecord } from '../types';

/** 功能权限前缀 + 动作编码，如 system:user + create → system:user:create */
export function joinPerm(prefix: string, action: string) {
  const base = prefix.trim().replace(/:$/, '');
  const act = action.trim().replace(/^:/, '');
  if (!base) return act;
  if (!act) return base;
  return `${base}:${act}`;
}

export function actionFromPerm(permission: string, prefix: string) {
  if (prefix && (permission === prefix || permission.startsWith(`${prefix}:`))) {
    return permission.slice(prefix.length).replace(/^:/, '');
  }
  return permission;
}

/** 模块内权限标识唯一。空字符串不参与校验。 */
export function permissionTaken(
  nodes: MenuRecord[],
  appId: string,
  permission: string,
  exceptId?: string,
) {
  const code = permission.trim();
  if (!code) return false;
  return nodes.some(
    (node) =>
      node.appId === appId &&
      node.id !== exceptId &&
      node.permission.trim() === code,
  );
}

export function pathTaken(
  nodes: MenuRecord[],
  appId: string,
  path: string,
  exceptId?: string,
) {
  const value = path.trim();
  if (!value) return false;
  return nodes.some(
    (node) =>
      node.appId === appId &&
      node.type !== 'button' &&
      node.id !== exceptId &&
      node.path.trim() === value,
  );
}

/** 把 from 前缀换成 to，只动完全相等或以 from: 开头的标识。 */
export function replacePrefix(permission: string, from: string, to: string) {
  if (!from) return permission;
  if (permission === from) return to;
  if (permission.startsWith(`${from}:`)) {
    return `${to}${permission.slice(from.length)}`;
  }
  return permission;
}
