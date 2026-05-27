import http from '#/api/http';

/* ==================== API 定义 ==================== */
export const deptApi = {
  /** 查询部门列表 */
  tree: (query: DeptQuery) => {
    return http.get<DeptResult[]>(`/system/dept/tree`, { params: query });
  },
  /** 查询部门详情 */
  get: (id: string) => {
    return http.get<DeptResult>(`/system/dept/${id}`);
  },
  /** 新增部门 */
  create: (data: any) => {
    return http.post<boolean>(`/system/dept`, data);
  },
  /** 修改部门 */
  update: (data: any, id: string) => {
    return http.patch(`/system/dept/${id}`, data);
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
  option: (query: { keyword: string | unknown }) => {
    return http.get<DeptResult[]>(`/system/dept/dict/tree`, { params: query });
  },
};

/* ==================== Schema 定义 ==================== */
/** 部门类型 */
export interface DeptResult {
  id: string;
  parentId: string;
  name: string;
  code: string;
  type: number;
  sort: number;
  status: 1 | 2;
  isBuiltin: boolean;
  description: string;
  createUserString: string;
  createTime: string;
  updateUserString: string;
  updateTime: string;
  children: DeptResult[];
}
export interface DeptQuery {
  keyword?: string;
  status?: number;
}
