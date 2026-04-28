import http from '#/api/http';

/* ==================== API 定义 ==================== */
export const deptApi = {
  /** 查询部门列表 */
  list: (query: DeptQuery) => {
    return http.get<DeptResp[]>(`/system/dept/tree`, { params: query });
  },

  /** 查询部门详情 */
  get: (id: string) => {
    return http.get<DeptResp>(`/system/dept/${id}`);
  },

  /** 新增部门 */
  create: (data: any) => {
    return http.post<boolean>(`/system/dept`, data);
  },

  /** 修改部门 */
  update: (data: any, id: string) => {
    return http.put(`/system/dept/${id}`, data);
  },

  /** 删除部门 */
  delete: (id: string) => {
    return http.delete(`/system/dept`, { data: { ids: [id] } });
  },

  /** 导出部门 */
  export: (query: DeptQuery) => {
    return http.download(`/system/dept/export`, { params: query });
  },

  /** 查询部门字典树 */
  tree: (query: { description: string | unknown }) => {
    return http.get<DeptResp[]>(`/system/dept/dict/tree`, { params: query });
  },
};

/* ==================== Schema 定义 ==================== */
/** 部门类型 */
export interface DeptResp {
  id: string;
  parentId: string;
  name: string;
  sort: number;
  status: 1 | 2;
  isBuiltin: boolean;
  description: string;
  createUserString: string;
  createTime: string;
  updateUserString: string;
  updateTime: string;
  children: DeptResp[];
}
export interface DeptQuery {
  description?: string;
  status?: number;
}
