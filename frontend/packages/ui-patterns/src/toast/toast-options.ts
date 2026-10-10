/**
 * duration 用毫秒。0 不自动关闭，和原先 Naive message 的 duration: 0 一样。
 */
export interface ToastOptions {
  description?: string;
  duration?: number;
}

export function toastDuration(duration: number | undefined): number | undefined {
  if (duration == null) return undefined;
  if (duration <= 0) return Number.POSITIVE_INFINITY;
  return duration;
}

export function toastPayload(options?: ToastOptions | number):
  | {
      description?: string;
      duration?: number;
    }
  | undefined {
  if (options == null) return undefined;
  if (typeof options === 'number') {
    return { duration: toastDuration(options) };
  }
  const payload: { description?: string; duration?: number } = {};
  if (options.description != null) payload.description = options.description;
  const duration = toastDuration(options.duration);
  if (duration != null) payload.duration = duration;
  return Object.keys(payload).length > 0 ? payload : undefined;
}

export function toastText(content: unknown): string {
  if (content == null) return '';
  return String(content);
}

export interface ToastCaller {
  (
    message: string,
    data?: {
      description?: string;
      duration?: number;
    },
  ): string | number;
}

export interface ToastApi {
  error: (content: unknown, options?: ToastOptions | number) => string | number;
  info: (content: unknown, options?: ToastOptions | number) => string | number;
  loading: (
    content: unknown,
    options?: ToastOptions | number,
  ) => string | number;
  success: (
    content: unknown,
    options?: ToastOptions | number,
  ) => string | number;
  warning: (
    content: unknown,
    options?: ToastOptions | number,
  ) => string | number;
}

export function createToast(api: {
  error: ToastCaller;
  info: ToastCaller;
  loading: ToastCaller;
  success: ToastCaller;
  warning: ToastCaller;
}): ToastApi {
  function call(
    method: ToastCaller,
    content: unknown,
    options?: ToastOptions | number,
  ) {
    return method(toastText(content), toastPayload(options));
  }
  return {
    error: (content, options) => call(api.error, content, options),
    info: (content, options) => call(api.info, content, options),
    loading: (content, options) => call(api.loading, content, options),
    success: (content, options) => call(api.success, content, options),
    warning: (content, options) => call(api.warning, content, options),
  };
}
