import type { PageQuery, PageResult } from '#/types/api';

import http from '#/api/http';

/* ==================== API 定义 ==================== */
export const dictApi = {
  /** 分页查询字典列表 */
  page: (query: DictPageQuery) => {
    return http.get<PageResult<DictResult>>('/system/dict/page', { params: query });
  },
  /** 新增字典 */
  create: (data: DictRequest) => {
    return http.post('/system/dict', data);
  },
  /** 修改字典 */
  update: (id: number, data: DictRequest) => {
    return http.put(`/system/dict/${id}`, data);
  },
  /** 批量删除字典 */
  delete: (ids: number[]) => {
    return http.delete('/system/dict', { data: ids });
  },
  /** 清除字典缓存 */
  clearCache: (dictType: string) => {
    return http.delete(`/system/dict/cache/${dictType}`);
  },
};

/* ==================== Schema 定义 ==================== */

/**
 * 字典响应参数
 */
export interface DictResult {
  /** ID */
  id: number;
  /** 字典类型 */
  dictType: string;
  /** 字典值 */
  value: string;
  /** 字典标签 */
  label: string;
  /** 扩展信息 */
  ext?: Record<string, any>;
  /** 排序 */
  sort: number;
  /** 是否启用 */
  enabled: boolean;
  /** 描述 */
  description?: string;
}

/**
 * 字典查询条件
 */
export interface DictQuery {
  /** 关键词（搜索字典类型、标签、值、描述） */
  description?: string;
  /** 字典类型 */
  dictType?: string;
}

/**
 * 字典创建或修改请求参数
 */
export interface DictRequest {
  /** 字典类型 */
  dictType: string;
  /** 字典值 */
  value: string;
  /** 字典标签 */
  label: string;
  /** 扩展信息 */
  extra?: Record<string, any>;
  /** 排序 */
  sort?: number;
  /** 是否启用 */
  enabled?: boolean;
  /** 描述 */
  description?: string;
}

/**
 * 字典分页查询参数
 */
export interface DictPageQuery extends DictQuery, PageQuery {}
