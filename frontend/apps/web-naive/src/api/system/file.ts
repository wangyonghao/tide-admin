import http from '#/api/http';

/* ==================== API 定义 ==================== */
export const fileApi = {
  /**
   * 分页查询文件列表
   */
  list: (params: FileQuery) => {
    return http.get<{ list: FileResult[]; total: number }>('/system/file', { params });
  },
  /**
   * 查询文件列表（不分页）
   */
  listAll: (params: Omit<FileQuery, 'page' | 'size'>) => {
    return http.get<FileResult[]>('/system/file/list', { params });
  },
  /**
   * 查询文件详情
   */
  detail: (id: number) => {
    return http.get<FileResult>(`/system/file/${id}`);
  },
  /**
   * 上传文件
   */
  upload: (file: File, path?: string) => {
    const formData = new FormData();
    formData.append('file', file);
    
    const request = { path: path || '/' };
    formData.append('request', new Blob([JSON.stringify(request)], { type: 'application/json' }));
    
    return http.post<FileResult>('/system/file', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
  },
  /**
   * 下载文件
   */
  download: (fileId: number, fileName: string) => {
    return http.download(`/system/file/download/${fileId}`, {}, fileName);
  },
  /**
   * 预览文件
   */
  preview: (fileId: number) => {
    return `/system/file/preview/${fileId}`;
  },
  /**
   * 删除文件
   */
  delete: (id: number) => {
    return http.delete(`/system/file/${id}`);
  },
  /**
   * 批量删除文件
   */
  batchDelete: (ids: number[]) => {
    return http.delete('/system/file', { data: { ids } });
  },
};

/* ==================== Schema 定义 ==================== */

/**
 * 文件类型枚举
 */
export enum FileType {
  /** 图片 */
  IMAGE = 1,
  /** 视频 */
  VIDEO = 2,
  /** 音频 */
  AUDIO = 3,
  /** 文档 */
  DOCUMENT = 4,
  /** 其他 */
  OTHER = 5,
}

/**
 * 文件查询条件
 */
export interface FileQuery {
  /** 名称 */
  originalName?: string;
  /** 上级目录 */
  parentPath?: string;
  /** 类型 */
  type?: FileType;
  /** 关联业务类型 */
  bizType?: string;
  /** 关联业务Id */
  bizId?: string;
  /** 页码 */
  page?: number;
  /** 每页数量 */
  size?: number;
  /** 排序字段 */
  sort?: string;
  /** 排序方向 */
  order?: string;
}

/**
 * 文件响应参数
 */
export interface FileResult {
  /** ID */
  id: number;
  /** 创建人 */
  createUserString?: string;
  /** 创建时间 */
  createTime: string;
  /** 修改人 */
  updateUserString?: string;
  /** 修改时间 */
  updateTime?: string;
  /** 名称 */
  fileName: string;
  /** 原始名称 */
  originalName: string;
  /** 大小（字节） */
  fileSize: number;
  /** URL */
  url: string;
  /** 路径 */
  ossPath: string;
  /** 扩展名 */
  fileExtension: string;
  /** 内容类型 */
  contentType: string;
  /** 类型 */
  type: FileType;
  /** SHA256 值 */
  sha256?: string;
  /** 元数据 */
  metadata?: string;
  /** 缩略图名称 */
  thumbnailName?: string;
  /** 缩略图大小（字节) */
  thumbnailSize?: number;
  /** 缩略图元数据 */
  thumbnailMetadata?: string;
  /** 缩略图 URL */
  thumbnailUrl?: string;
  /** 存储 ID */
  storageId?: number;
  /** 存储名称 */
  storageName?: string;
}

/**
 * 文件上传请求
 */
export interface FileUploadRequest {
  /** 文件 */
  file: File;
  /** 路径 */
  path?: string;
}

