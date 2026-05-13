import http from '#/api/http';

/** 根据域名查询租户 ID */
export function getTenantIdByDomain(domain: string) {
  return http.get<string>(`/tenant/common/id`, { params: { domain } });
}
