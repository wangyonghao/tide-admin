import type { BaseEntity, PageQuery, PageResult } from '#/types/api';
import type { Option } from '#/types/global';

import http from '#/api/http';

/** 租户套餐 */
export interface TenantPackageResp extends BaseEntity {
  id: string;
  name: string;
  sort: number;
  menuCheckStrictly: string;
  description: string;
  status: number;
  menuIds: [];
  disabled: boolean;
}

export interface TenantPackageQuery {
  name?: string | undefined;
  status?: string;
  description?: string;
  sort: Array<string> | string;
}

export interface TenantPackagePageQuery extends PageQuery, TenantPackageQuery {}

/** 查询租户套餐列表 */
export function listTenantPackage(query: TenantPackagePageQuery) {
  return http.get<PageResult<TenantPackageResp[]>>('/tenant/package', {
    params: query,
  });
}

/** 查询租户套餐详情 */
export function getTenantPackage(id: string) {
  return http.get<TenantPackageResp>(`/tenant/package/${id}`);
}

/** 新增租户套餐 */
export function addTenantPackage(data: any) {
  return http.post('/tenant/package', data);
}

/** 修改租户套餐 */
export function updateTenantPackage(data: any, id: string) {
  return http.put(`/tenant/package/${id}`, data);
}

/** 删除租户套餐 */
export function deleteTenantPackage(id: string) {
  return http.delete(`/tenant/package/${id}`);
}

/** 导出租户套餐 */
export function exportPackage(query: TenantPackagePageQuery) {
  return http.download(`/tenant/package/export`, {
    params: query,
  });
}

/** 查询所有套餐 */
export function listAllTenantPackage() {
  return http.get<TenantPackageResp[]>(`/tenant/package/list`);
}

/** 查询套餐菜单 */
export function listTenantPackageMenu() {
  return http.get<TenantPackageResp[]>(`/tenant/package/menu/tree`, {
    params: { isSimple: false },
  });
}

/** 查询租户套餐字典 */
export function listTenantPackageDict(query?: {
  description: string;
  status: number;
}) {
  return http.get<Option[]>(`/tenant/package/dict`, {
    params: query,
  });
}
