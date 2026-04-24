import type { NoticePageQuery, NoticeResp } from './notice';

import type { PageQuery, PageRes } from '#/types/api';

import { requestClient as http } from '#/api/request';

/** 系统消息类型 */
export interface MessageResp {
  id: string;
  title: string;
  content: string;
  type: number;
  path: string;
  isRead: boolean;
  readTime?: string;
  createUserString?: string;
  createTime: string;
}

export interface MessageQuery {
  title?: string;
  type?: number;
  isRead?: boolean;
  sort: Array<string>;
}

export interface MessagePageQuery extends MessageQuery, PageQuery {}

/** 查询未读消息数量 */
export function getUnreadMessageCount() {
  return http.get(`/user/message/unread`);
}

/** 查询消息列表 */
export function listMessage(query: MessagePageQuery) {
  return http.get<PageRes<MessageResp[]>>(`/user/message`, {
    params: query,
  });
}

/** 获取用户消息详情 */
export function getUserMessage(id: number | string) {
  return http.get<MessageResp>(`/user/message/${id}`);
}

/** 删除消息 */
export function deleteMessage(ids: Array<string>) {
  return http.delete(`/user/message`, { data: { ids } });
}

/** 标记已读 */
export function readMessage(ids: Array<string>) {
  return http.patch(`/user/message/read`, { data: { ids } });
}

/** 全部已读 */
export function readAllMessage() {
  return http.patch(`/user/message/readAll`);
}

/** 查询未读公告数量 */
export function getUnreadNoticeCount() {
  return http.get(`/user/message/notice/unread`);
}

/** 查询未读公告 ID 列表 */
export function getUnreadNoticeIds(method: string) {
  return http.get<number[]>(`/user/message/notice/unread/${method}`);
}

/** 分页查询用户公告 */
export function listUserNotice(query: NoticePageQuery) {
  return http.get<PageRes<NoticeResp[]>>(`/user/message/notice`, {
    params: query,
  });
}

/** 获取用户公告详情 */
export function getUserNotice(id: string) {
  return http.get<NoticeResp>(`/user/message/notice/${id}`);
}
