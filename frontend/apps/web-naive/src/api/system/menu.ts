import { requestClient as http } from '#/api/request';

export const menuApi = {
  /** 查询菜单列表 */
  tree(query?: MenuQuery) {
    return http.get<Menu[]>(`/system/menu`, { params: query });
  },
  /** 查询菜单详情 */
  detail(id: string) {
    return http.get<Menu>(`/system/menu/${id}`);
  },
  /** 新增菜单 */
  create(data: any) {
    return http.post<boolean>(`/system/menu`, data);
  },
  /** 修改菜单 */
  update(data: any, id: string) {
    return http.put(`/system/menu/${id}`, data);
  },
  /** 删除菜单 */
  delete(id: string) {
    return http.delete(`/system/menu`, { data: { ids: [id] } });
  },
  /** 清除菜单缓存 */
  clearMenuCache() {
    return http.delete(`/system/menu/cache`);
  },
};

/** 菜单类型 */
export interface Menu {
  id: string;
  name: string;
  parentId: string;
  type: 1 | 2 | 3;
  path: string;
  component: string;
  redirect: string;
  icon: string;
  isExternal: boolean;
  isCache: boolean;
  isHidden: boolean;
  permission: string;
  sort: number;
  status: 1 | 2;
  children?: Menu[];
}

export interface MenuQuery {
  title?: string;
  status?: number;
}
