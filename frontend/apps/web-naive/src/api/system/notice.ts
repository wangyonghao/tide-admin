import type { BaseEntity, PageQuery, PageResult } from '#/types/api';

import http from '#/api/http';

export interface NoticeResp extends BaseEntity {
  /** ID */
  id: string;
  /** 标题 */
  title: string;
  /** 内容 */
  content: string;
  /** 分类 */
  type: string;
  /** 通知范围（1：所有人；2：指定用户） */
  noticeScope: string;
  /** 通知用户 */
  noticeUsers: string;
  /** 通知方式（1：系统消息；2：登录弹窗） */
  noticeMethods: string;
  /** 是否定时 */
  isTiming: string;
  /** 发布时间 */
  publishTime: string;
  /** 是否置顶 */
  isTop: string;
  /** 状态（1：草稿；2：待发布；3：已发布） */
  status: number;
  /** 创建人 */
  createUser: string;
  /** 创建时间 */
  createTime: string;
}

export interface NoticeDetailResp extends BaseEntity {
  id: string;
  title: string;
  content: string;
  type: string;
  noticeScope: string;
  noticeUsers: string;
  noticeMethods: string;
  isTiming: string;
  publishTime: string;
  isTop: string;
  status: number;
  createUser: string;
  createTime: string;
  updateUser: string;
  updateTime: string;
  tenantId: string;
  createUserString: string;
  updateUserString: string;
}

export interface NoticeQuery {
  title: string | undefined;
  type: string | undefined;
  publishTime: string | undefined;
  isTop: string | undefined;
  status: string | undefined;
  sort: Array<string> | string;
}

export interface NoticePageQuery extends NoticeQuery, PageQuery {}

/** 查询公告列表 */
export function listNotice(query: NoticePageQuery) {
  return http.get<PageResult<NoticeResp[]>>('/system/notice', {
    params: query,
  });
}

/** 查询公告详情 */
export function getNotice(id: string) {
  return http.get<NoticeDetailResp>(`/system/notice/${id}`);
}

/** 新增公告 */
export function addNotice(data: any) {
  return http.post('/system/notice', data);
}

/** 修改公告 */
export function updateNotice(data: any, id: string) {
  return http.put(`/system/notice/${id}`, data);
}

/** 删除公告 */
export function deleteNotice(id: string) {
  return http.delete('/system/notice', { data: { ids: [id] } });
}

/** 导出公告 */
export function exportNotice(query: NoticeQuery) {
  return http.download(`/system/notice/export`, {
    params: query,
  });
}
