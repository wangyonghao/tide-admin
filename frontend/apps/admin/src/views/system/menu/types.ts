/** 节点类型：folder 目录、function 功能、button 按钮。模块是树根，单独存放。 */
export type MenuKind = 'button' | 'folder' | 'function';

export type MenuStatus = 'disabled' | 'enabled';

export interface AppRecord {
  code: string;
  entry: string;
  icon: string;
  id: string;
  name: string;
  sort: number;
  status: MenuStatus;
}

export interface MenuRecord {
  apis: string[];
  appId: string;
  cache: boolean;
  component: string;
  external: boolean;
  icon: string;
  id: string;
  name: string;
  parentId: null | string;
  path: string;
  permission: string;
  remark: string;
  sort: number;
  status: MenuStatus;
  type: MenuKind;
  visible: boolean;
}

export interface RoleGrant {
  id: string;
  menuIds: string[];
  name: string;
}

export interface MenuImpact {
  buttons: number;
  children: number;
  roles: number;
}

export interface BasicFormValues {
  cache: boolean;
  component: string;
  external: boolean;
  icon: string;
  name: string;
  path: string;
  permission: string;
  remark: string;
  sort: number;
  status: MenuStatus;
  type: 'folder' | 'function';
  visible: boolean;
}
