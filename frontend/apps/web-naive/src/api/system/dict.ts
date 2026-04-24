import type { PageQuery } from '#/types/api';

import { requestClient as http } from '#/api/request';

/** 字典类型 */
export interface DictResp {
  id: string;
  name: string;
  code: string;
  isBuiltin: boolean;
  description: string;
  createUserString: string;
  createTime: string;
  updateUserString: string;
  updateTime: string;
}
export interface DictQuery {
  description?: string;
  sort: Array<string>;
}
export interface DictItemResp {
  id: string;
  label: string;
  value: string;
  color: string;
  sort: number;
  description: string;
  status: 1 | 2;
  dictId: string;
  createUserString: string;
  createTime: string;
  updateUserString: string;
  updateTime: string;
  cssClass: string;
  listClass: string;
}
export interface DictItemQuery {
  description?: string;
  status?: number;
  sort: Array<string>;
  dictId: string;
}
export interface DictItemPageQuery extends DictItemQuery, PageQuery {}

/** 查询字典列表 */
export function listDict(query?: DictQuery) {
  return http.get<DictResp[]>(`/system/dict/list`, { params: query });
}

/** 查询字典详情 */
export function getDict(id: number | string) {
  return http.get<DictResp>(`/system/dict/${id}`);
}

/** 新增字典 */
export function addDict(data: any) {
  return http.post(`/system/dict`, data);
}

/** 修改字典 */
export function updateDict(data: any, id: string) {
  return http.put(`/system/dict/${id}`, data);
}

/** 删除字典 */
export function deleteDict(id: string) {
  return http.delete(`/system/dict`, { data: { ids: [id] } });
}

/** 清除字典缓存 */
export function clearDictCache(code: string) {
  return http.delete(`/system/dict/cache/${code}`);
}

/** 查询字典项列表 */
export function listDictItem(query: DictItemPageQuery) {
  return http.get<DictItemResp[]>(`/system/dict/item`, {
    params: query,
  });
}

/** 查询字典项详情 */
export function getDictItem(id: string) {
  return http.get<DictItemResp>(`/system/dict/item/${id}`);
}

/** 新增字典项 */
export function addDictItem(data: any) {
  return http.post(`/system/dict/item`, data);
}

/** 修改字典项 */
export function updateDictItem(data: any, id: string) {
  return http.put(`/system/dict/item/${id}`, data);
}

/** 删除字典项 */
export function deleteDictItem(id: string) {
  return http.delete(`/system/dict/item`, { data: { ids: [id] } });
}
