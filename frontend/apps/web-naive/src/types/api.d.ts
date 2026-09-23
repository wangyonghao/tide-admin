/** 业务主键。雪花 ID 超出 JS 安全整数，前后端一律按字符串传递。 */
export type ID = string;
export type IDS = string[];

export interface BaseEntity {
  /** 创建人 */
  createUser?: string;
  /** 创建时间 */
  createTime?: string;
  /** 更新人 */
  updateUser?: string;
  /** 更新时间 */
  updateTime?: string;
  /** 创建人 */
  createUserString?: string;
  /** 更新人名称 */
  updateUserString?: string;
  /** 租户ID */
  tenantId: string;
}

/** 接口返回数据格式 */
interface ApiResult<T> {
  code: number;
  data: T;
  msg: string;
  success: boolean;
  timestamp: string;
}

/** 分页响应数据格式 */
interface PageResult<T> {
  /** 列表数据 */
  records: T;
  /** 总记录数 */
  total: number;
  /** 页码 */
  page: number;
  /** 每页条数 */
  pageSize: number;
  /** 总页数 */
  pages: number;
}

/** 分页请求数据格式 */
interface PageQuery {
  /** 页码 */
  page: number;
  /** 每页条数 */
  pageSize: number;
}
