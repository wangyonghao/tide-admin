import type { MenuRecordRaw } from '@vben/types';

import type { Menu } from '#/api/system/menu';

function isButton(type: Menu['type'] | string | number | undefined) {
  return Number(type) === 3 || type === 'BUTTON';
}

function normalizePath(path?: string, fallback = '') {
  if (!path) return fallback;
  if (/^https?:\/\//.test(path) || path.startsWith('/')) return path;
  return `/${path}`;
}

/**
 * 把后端菜单树转成布局侧栏识别的 MenuRecordRaw。
 * 按钮（type=3）不进侧栏，避免空白名称/空 path。
 */
export function toAccessMenus(menus: Menu[] | undefined): MenuRecordRaw[] {
  if (!menus?.length) return [];
  return menus
    .filter((menu) => !isButton(menu.type) && !menu.isHidden)
    .map((menu) => {
      const children = toAccessMenus(menu.children);
      // 后端返回的是 name 字段，确保优先使用
      const menuName = menu.name || menu.title || '未命名菜单';
      return {
        name: menuName,
        path: normalizePath(menu.path, String(menu.id)),
        icon: menu.icon,
        order: menu.sort,
        show: true,
        children: children.length > 0 ? children : undefined,
      };
    });
}
