import type { PageQuery } from '#/types/api';
import type { Option } from '#/types/global';

import  http  from '#/api/http';

// ========== API 定义 ==========
/** 查询代码生成列表 */
export function listGenConfig(query: GenConfigPageQuery) {
  return http.get<GenConfigResp[]>(`/code/generator/config`, { params: query });
}
/** 查询生成配置信息 */
export function getGenConfig(tableName: string) {
  return http.get<GenConfigResp>(`/code/generator/config/${tableName}`);
}
/** 查询字段配置列表 */
export function listFieldConfig(tableName: string, requireSync: boolean) {
  return http.get<FieldConfigResp[]>(
    `/code/generator/field/${tableName}?requireSync=${requireSync}`,
  );
}
/** 保存配置信息 */
export function saveGenConfig(tableName: string, req: GeneratorConfigResp) {
  return http.post(`/code/generator/config/${tableName}`, req);
}
/** 生成预览 */
export function genPreview(tableNames: Array<string>) {
  return http.get<GeneratePreviewResp[]>(
    `/code/generator/preview/${tableNames}`,
  );
}
/** 生成代码 */
export function downloadCode(tableNames: Array<string>) {
  return http.post(`/code/generator/${tableNames}/download`, {
    responseType: 'blob',
    responseReturn: 'raw',
  });
}
/** 生成代码 */
export function generateCode(tableNames: Array<string>) {
  return http.post(`/code/generator/${tableNames}`);
}
/** 查询字典列表 */
export function listFieldConfigDict() {
  return http.get<Option[]>(`/code/generator/dict`);
}

// ========== 类型定义 ==========
/** 工具代码生成类型 */
export interface GenConfigResp {
  tableName: string;
  comment: string;
  moduleName: string;
  packageName: string;
  businessName: string;
  author: string;
  tablePrefix: string;
  isOverride: boolean;
  createTime?: string;
  updateTime?: string;
  classNamePrefix?: string;
  listType: number;
  entityName: string;
  dialogType: number;
  treeId: string;
  treePid: string;
  treeLabel: string;
  frontPath: string;
}
export interface GenConfigQuery {
  tableName?: string;
}
export interface GenConfigPageQuery extends GenConfigQuery, PageQuery {}

export interface FieldConfigResp {
  tableName: string;
  columnName: string;
  columnType: string;
  fieldName: string;
  fieldType: string;
  fieldSort: number;
  comment: string;
  isRequired: boolean;
  showInList: boolean;
  showInForm: boolean;
  showInQuery: boolean;
  formType: string;
  queryType: string;
  dictCode: string;
  createTime?: string;
}
export interface GeneratorConfigResp {
  genConfig: GenConfigResp;
  fieldConfigs: FieldConfigResp[];
}
export interface GeneratePreviewResp {
  path: string;
  fileName: string;
  content: string;
}
