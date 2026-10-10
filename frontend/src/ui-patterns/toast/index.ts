import { toast as sonner } from '#/ui/sonner';

import { createToast } from './toast-options';

export type { ToastApi, ToastOptions } from './toast-options';
export {
  createToast,
  toastDuration,
  toastPayload,
  toastText,
} from './toast-options';

export const toast = createToast({
  error: (message, data) => sonner.error(message, data),
  info: (message, data) => sonner.info(message, data),
  loading: (message, data) => sonner.loading(message, data),
  success: (message, data) => sonner.success(message, data),
  warning: (message, data) => sonner.warning(message, data),
});
