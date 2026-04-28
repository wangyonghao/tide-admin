import { useUserStore } from '#/store/user';
import axios, { type AxiosInstance, type AxiosRequestConfig, type AxiosResponse, type InternalAxiosRequestConfig } from 'axios';
import { preferences } from '@vben/preferences';
import { message } from '#/adapter/naive';
import { $t } from '#/locales';

interface HttpConfig {
  baseURL?: string;
  getToken?: () => string;
  getLocale?: () => string;
  successCode?: number;
  unwrapResponse?: boolean;
  onError?: (msg: string) => void;
}

interface SSEOptions {
  params?: Record<string, any>;
  onOpen?: () => void;
  onMessage?: (data: any, event: MessageEvent) => void;
  onError?: (error: Event) => void;
}

interface SSEConnection {
  close: () => void;
  readyState: number;
}

/**
 * 全局 HTTP 客户端单例
 * 直接 import http from '#/util/http'; 即可使用，支持运行时动态配置
 */
class HttpService {
  private baseURL: string;
  private getToken: () => string;
  private getLocale: () => string;
  private successCode: number;
  private unwrapResponse: boolean;
  private onError: (msg: string) => void;
  private instance: AxiosInstance;

  constructor() {
    this.baseURL = '';
    this.getToken = () => '';
    this.getLocale = () => 'zh-CN';
    this.successCode = 200;
    this.unwrapResponse = false;
    this.onError = console.error;
    this.instance = axios.create({ timeout: 30000 });
    this._initInterceptors();
  }

  /** 初始化配置（建议在 main.js / app.js 调用一次） */
  init(config: HttpConfig = {}) {
    this.baseURL = config.baseURL ?? this.baseURL;
    this.getToken = config.getToken ?? this.getToken;
    this.getLocale = config.getLocale ?? this.getLocale;
    this.successCode = config.successCode ?? this.successCode;
    this.unwrapResponse = config.unwrapResponse ?? this.unwrapResponse;
    this.onError = config.onError ?? this.onError;

    // 重建实例以应用新配置（自动清除旧拦截器）
    this.instance = axios.create({ baseURL: this.baseURL, timeout: 30000 });
    this._initInterceptors();
    return this;
  }

  private _initInterceptors() {
    // 请求拦截：自动注入 Token & 语言
    this.instance.interceptors.request.use(
      (config: InternalAxiosRequestConfig) => {
        const token = this.getToken();
        const locale = this.getLocale();
        if (token) config.headers.Authorization = `Bearer ${token}`;
        if (locale) config.headers['Accept-Language'] = locale;
        return config;
      },
      (error: any) => Promise.reject(error)
    );

    // 响应拦截：统一错误处理 & 响应解构
    this.instance.interceptors.response.use(
      (response: AxiosResponse) => {
        // 跳过下载请求，直接返回完整响应
        if (response.config.responseType === 'blob') return response;

        const resData = response.data;
        // 解构统一响应 {code, msg, data}
        if (this.unwrapResponse && typeof resData === 'object' && 'code' in resData) {
          const { code, message, data } = resData;
          if (code === this.successCode) return data;
          const errMsg = message || '请求业务异常';
          this._showError(errMsg);
          return Promise.reject(new Error(errMsg));
        }
        return resData;
      },
      (error: any) => {
        if (axios.isCancel(error)) {
            return Promise.reject(error);
        }
        let message = '';

        // 处理网络连接错误和请求超时错误
        const err: string = error?.toString?.() ?? '';
        if (err?.includes('Network Error')) {
          message = $t('ui.fallback.http.networkError');
        } else if (error?.message?.includes?.('timeout')) {
          message = $t('ui.fallback.http.requestTimeout');
        }
        if (message) {
          this.onError(message);
          return Promise.reject(error);
        }

        // 处理常见 HTTP 错误
        const status = error.response?.status;
        const statusMap: Record<number, string> = {
          400: $t('ui.fallback.http.badRequest'),
          401: $t('ui.fallback.http.unauthorized'),
          403: $t('ui.fallback.http.forbidden'),
          404: $t('ui.fallback.http.notFound'),
          408: $t('ui.fallback.http.requestTimeout'),
        };
        message = statusMap[status] || message;
        const backendMsg = error.response?.data?.msg || error.response?.data?.message;
        if (backendMsg) message = backendMsg;

        this._showError(message || $t('ui.fallback.http.internalServerError'));
        return Promise.reject(error);
      }
    );
  }

  private _showError(msg: string) {
    console.error(`[HTTP Error]: ${msg}`);
    if (typeof this.onError === 'function') this.onError(msg);
  }

  // 标准 HTTP 方法
  get<T = any>(url: string, config?: AxiosRequestConfig): Promise<T> { 
    return this.instance.get(url, config); 
  }
  post<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> { 
    return this.instance.post(url, data, config); 
  }
  put<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> { 
    return this.instance.put(url, data, config); 
  }
  patch<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> { 
    return this.instance.patch(url, data, config); 
  }
  delete<T = any>(url: string, config?: AxiosRequestConfig): Promise<T> { 
    return this.instance.delete(url, config); 
  }

  // 上传
  upload<T = any>(url: string, formData: FormData, config: AxiosRequestConfig = {}): Promise<T> {
    return this.post(url, formData, { headers: { 'Content-Type': 'multipart/form-data' }, ...config });
  }

  // 下载（严格 size>0 才触发）
  async download(url: string, config: AxiosRequestConfig = {}, fallbackName = 'download'): Promise<Blob> {
    const response = await this.instance.get(url, { responseType: 'blob', ...config });
    const blob = response.data;

    if (blob.size === 0) {
      try {
        const text = await blob.text();
        const errData = JSON.parse(text);
        this._showError(errData.msg || errData.message || '文件生成失败');
      } catch {
        this._showError('下载失败：文件数据为空');
      }
      throw new Error('Empty blob');
    }

    let fileName = fallbackName;
    const disposition = response.headers['content-disposition'];
    if (disposition) {
      const match = disposition.match(/filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/);
      if (match?.[1]) fileName = decodeURIComponent(match[1].replace(/['"]/g, ''));
    }

    const link = document.createElement('a');
    link.href = window.URL.createObjectURL(blob);
    link.download = fileName;
    document.body.appendChild(link);
    link.click();
    setTimeout(() => {
      document.body.removeChild(link);
      window.URL.revokeObjectURL(link.href);
    }, 100);
    return blob;
  }

  // 📡 SSE 封装
  sse(url: string, options: SSEOptions = {}): SSEConnection {
    const token = this.getToken();
    const locale = this.getLocale();
    const separator = url.includes('?') ? '&' : '?';
    const params = new URLSearchParams(options.params || {});
    if (token) params.append('Authorization', `Bearer ${token}`);
    if (locale) params.append('Accept-Language', locale);

    const sseUrl = `${url}${separator}${params.toString()}`;
    const eventSource = new EventSource(sseUrl, { withCredentials: true });

    eventSource.onopen = () => options.onOpen?.();
    eventSource.onmessage = (event) => {
      try { options.onMessage?.(JSON.parse(event.data), event); }
      catch { options.onMessage?.(event.data, event); }
    };
    eventSource.onerror = (err) => {
      this._showError('SSE 连接异常');
      options.onError?.(err);
      eventSource.close();
    };
    return { close: () => eventSource.close(), readyState: eventSource.readyState };
  }
}

// 导出全局单例对象
const http = new HttpService();
http.init({
  baseURL: import.meta.env.VITE_API_BASE || '/api',
  getToken: ():string => {
    const userStore = useUserStore();
    return userStore.token ?? '';
  },              // 动态获取最新 Token
  getLocale: () => preferences.app.locale,      // 动态获取语言
  onError: (msg) => message.error(msg,{duration:5000 })
});

export default http;
