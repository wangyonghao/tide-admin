import type { PageQuery, PageResult } from '#/types/api';

import http from '#/api/http';

/** 任务日志类型 */
export interface JobLogResp {
  /**
   * ID
   */
  id: number;
  /**
   * 任务组
   */
  groupName: string;
  /**
   * 任务名称
   */
  jobName: string;
  /**
   * 任务 ID
   */
  jobId: number;
  /**
   * 任务状态 1待处理 2运行中  3成功 4已失败 5已停止 6已取消
   */
  taskBatchStatus: number;
  /**
   * 操作原因
   */
  operationReason: number;
  /**
   * 执行器类型
   */
  executorType: number;
  /**
   * 执行器名称
   */
  executorInfo: string;
  /**
   * 执行时间
   */
  executionAt: string;
  /**
   * 创建时间
   */
  createDt: string;
}
export interface JobLogQuery {
  /**
   * 任务 ID
   */
  jobId?: number;
  /**
   * 任务组
   */
  groupName?: string;
  /**
   * 任务名称
   */
  jobName?: string;
  /**
   * 任务批次状态
   */
  taskBatchStatus?: number;
  /**
   * 创建时间
   */
  datetimeRange?: Array<string>;
}
export interface JobLogPageQuery extends JobLogQuery, PageQuery {}

/** 查询任务日志列表 */
export function listJobLog(query: JobLogPageQuery) {
  return http.get<PageResult<JobLogResp[]>>(`/schedule/log`, {
    params: query,
  });
}

/** 查询任务日志详情 */
export function getJobLogDetail(id: number) {
  return http.get<boolean>(`/schedule/log/${id}`);
}

/** 停止任务 */
export function stopJob(id: number) {
  return http.post(`/schedule/log/stop/${id}`);
}

/** 重试任务 */
export function retryJob(id: number) {
  return http.post(`/schedule/log/retry/${id}`);
}
