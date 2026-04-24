import type { PageQuery, PageRes } from '#/types/api';

import { requestClient as http } from '#/api/request';

/** 任务类型 */
export interface JobResp {
  /**
   * ID
   */
  id: number | string;

  /**
   * 任务组
   */
  groupName: string;
  /**
   * 任务名称
   */
  jobName: string;
  /**
   * 描述
   */
  description?: string;
  /**
   * 触发类型
   */
  triggerType: number;
  /**
   * 间隔时长
   */
  triggerInterval: string;
  /**
   * 执行器类型
   */
  executorType: number;
  /**
   * 任务类型
   */
  taskType: number;
  /**
   * 执行器名称
   */
  executorInfo: string;
  /**
   * 任务参数
   */
  argsStr?: string;

  /**
   * 参数类型
   */
  argsType?: string;
  /**
   * 路由策略
   */
  routeKey: number;
  /**
   * 阻塞策略
   */
  blockStrategy: number;
  /**
   * 超时时间（单位：秒）
   */
  executorTimeout: number;
  /**
   * 最大重试次数
   */
  maxRetryTimes: number;
  /**
   * 重试间隔（单位：秒）
   */
  retryInterval: number;
  /**
   * 并行数
   */
  parallelNum: number;
  /**
   * 任务状态
   */
  jobStatus: number;
  /**
   * 下次触发时间
   */
  nextTriggerAt?: Date;
  /**
   * 创建时间
   */
  createDt?: Date;
  /**
   * 修改时间
   */
  updateDt?: Date;
}
export interface JobQuery {
  /**
   * 任务组
   */
  groupName: string;
  /**
   * 任务名称
   */
  jobName?: string;
  /**
   * 任务状态
   */
  jobStatus?: number;
}
export interface JobPageQuery extends JobQuery, PageQuery {}

/** 查询任务组列表 */
export function listGroup() {
  return http.get(`/schedule/job/group`);
}

/** 查询任务列表 */
export function listJob(query: JobPageQuery) {
  return http.get<PageRes<JobResp[]>>(`/schedule/job`, {
    params: query,
  });
}

/** 新增任务 */
export function addJob(data: any) {
  return http.post(`/schedule/job`, data);
}

/** 修改任务 */
export function updateJob(data: any, id: number | string) {
  return http.put(`/schedule/job/${id}`, data);
}

/** 修改任务状态 */
export function updateJobStatus(data: any, id: number | string) {
  return http.patch(`/schedule/job/${id}/status`, data);
}

/** 删除任务 */
export function deleteJob(id: number | string) {
  return http.delete(`/schedule/job/${id}`);
}

/** 执行任务 */
export function triggerJob(id: number | string) {
  return http.post(`/schedule/job/trigger/${id}`);
}
