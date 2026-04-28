import type { PageQuery, PageResult } from '#/types/api';

import http from '#/api/http';

/** 短信日志类型 */
export interface SmsLogResp {
  id: string;
  configId: string;
  phone: string;
  params: string;
  status: number;
  resMsg: string;
  createUser: string;
  createTime: string;
  updateUser: string;
  updateTime: string;
  createUserString: string;
  updateUserString: string;
}
export interface SmsLogQuery {
  configId: string | undefined;
  phone: string | undefined;
  status: number | undefined;
  sort: Array<string>;
}
export interface SmsLogPageQuery extends PageQuery, SmsLogQuery {}

/** 查询短信日志列表 */
export function listSmsLog(query: SmsLogPageQuery) {
  return http.get<PageResult<SmsLogResp[]>>(`/system/sms/log`, { params: query });
}

/** 查询短信日志详情 */
export function getSmsLog(id: string) {
  return http.get<SmsLogResp>(`/system/sms/log/${id}`);
}

/** 删除短信日志 */
export function deleteSmsLog(id: string) {
  return http.delete(`/system/sms/log`, { data: { ids: [id] } });
}

/** 导出短信日志 */
export function exportSmsLog(query: SmsLogQuery) {
  return http.download(`/system/sms/log/export`, { params: query });
}
