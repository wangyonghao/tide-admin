export interface MenuFormValues {
  component?: string;
  frameUrl?: string;
  icon?: string;
  id?: string;
  isFrame?: number;
  name?: string;
  parentId?: null | number | string;
  path?: string;
  permission?: string;
  routeComponent?: string;
  sort?: null | number;
  status?: number;
  type?: number;
  visible?: number;
}

/** 顶级菜单的键是字符串 `0`，和接口里的 parentId 对齐。数字 0 也收成同一键。 */
export function normalizeMenuParentId(value: unknown): string {
  if (value == null || value === '' || value === 0 || value === '0') return '0';
  return String(value);
}

export function menuShowsPath(values: { isFrame?: number; type?: number }) {
  return values.type !== 3 && !values.isFrame;
}

export function menuShowsFrameAddress(values: {
  isFrame?: number;
  type?: number;
}) {
  return values.type !== 3 && !!values.isFrame;
}

export function menuShowsRouteComponent(values: {
  isFrame?: number;
  type?: number;
}) {
  return values.type === 2 && !values.isFrame;
}

export function resolveMenuComponent(values: MenuFormValues): string {
  if (menuShowsFrameAddress(values)) return String(values.frameUrl ?? '');
  if (menuShowsRouteComponent(values)) return String(values.routeComponent ?? '');
  return '';
}

export function menuPayload(values: MenuFormValues) {
  const type = values.type ?? 1;
  const isButton = type === 3;
  return {
    component: isButton ? '' : resolveMenuComponent({ ...values, type }),
    icon: isButton ? '' : (values.icon ?? ''),
    id: values.id,
    isFrame: isButton ? 0 : (values.isFrame ?? 0),
    name: values.name ?? '',
    parentId: normalizeMenuParentId(values.parentId),
    path: isButton || values.isFrame ? '' : (values.path ?? ''),
    permission: isButton ? (values.permission ?? '') : '',
    sort: typeof values.sort === 'number' ? values.sort : 0,
    status: values.status ?? 1,
    type,
    visible: isButton ? 1 : (values.visible ?? 1),
  };
}
