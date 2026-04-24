import { requestClient as http } from '#/api/request';

/** 仪表盘公告类型 */
export interface DashboardNoticeResp {
  id: number;
  title: string;
  type: number;
  isTop: boolean;
}

/** 仪表盘访问趋势类型 */
export interface DashboardAccessTrendResp {
  date: string;
  pvCount: number;
  ipCount: number;
}

/** 仪表盘通用图表类型 */
export interface DashboardChartCommonResp {
  name: string;
  value: number;
}

/** 仪表盘通用总览类型 */
export interface DashboardOverviewCommonResp {
  total: number;
  today: number;
  growth: number;
  dataList: DashboardChartCommonResp[];
}

const BASE_URL = '/dashboard';

/** 查询公告列表 */
export function listDashboardNotice() {
  return http.get<DashboardNoticeResp[]>(`${BASE_URL}/notice`);
}

/** 查询 PV 总览 */
export function getDashboardOverviewPv() {
  return http.get<DashboardOverviewCommonResp>(
    `${BASE_URL}/analysis/overview/pv`,
  );
}

/** 查询 IP 总览 */
export function getDashboardOverviewIp() {
  return http.get<DashboardOverviewCommonResp>(
    `${BASE_URL}/analysis/overview/ip`,
  );
}

/** 查询地域分析 */
export function getAnalysisGeo() {
  return http.get<DashboardChartCommonResp[]>(`${BASE_URL}/analysis/geo`);
}

/** 查询访问趋势 */
export function getDashboardAccessTrend(days: number) {
  return http.get<DashboardAccessTrendResp[]>(
    `${BASE_URL}/access/trend/${days}`,
  );
}

/** 查询访问时段分析 */
export function getAnalysisTimeslot() {
  return http.get<DashboardChartCommonResp[]>(`${BASE_URL}/analysis/timeslot`);
}

/** 查询模块分析 */
export function getAnalysisModule() {
  return http.get<DashboardChartCommonResp[]>(`${BASE_URL}/analysis/module`);
}

/** 查询终端分析 */
export function getAnalysisOs() {
  return http.get<DashboardChartCommonResp[]>(`${BASE_URL}/analysis/os`);
}

/** 查询浏览器分析 */
export function getAnalysisBrowser() {
  return http.get<DashboardChartCommonResp[]>(`${BASE_URL}/analysis/browser`);
}
