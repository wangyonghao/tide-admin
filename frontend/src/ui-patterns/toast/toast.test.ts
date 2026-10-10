import { describe, expect, it, vi } from 'vitest';

import {
  createToast,
  toastDuration,
  toastPayload,
  toastText,
} from './toast-options';

describe('toast helper', () => {
  it('maps a zero duration to a toast that stays up', () => {
    expect(toastDuration(0)).toBe(Number.POSITIVE_INFINITY);
    expect(toastDuration(-1)).toBe(Number.POSITIVE_INFINITY);
    expect(toastDuration(5000)).toBe(5000);
    expect(toastDuration(undefined)).toBeUndefined();
  });

  it('accepts a duration number or an options object', () => {
    expect(toastPayload(5000)).toEqual({ duration: 5000 });
    expect(toastPayload({ duration: 0, description: '保持打开' })).toEqual({
      description: '保持打开',
      duration: Number.POSITIVE_INFINITY,
    });
    expect(toastPayload()).toBeUndefined();
    expect(toastPayload({})).toBeUndefined();
  });

  it('stringifies the message and forwards success, error, and warning', () => {
    const success = vi.fn(() => 'ok');
    const error = vi.fn(() => 'bad');
    const warning = vi.fn(() => 'warn');
    const api = createToast({
      error,
      info: vi.fn(),
      loading: vi.fn(),
      success,
      warning,
    });

    expect(toastText(null)).toBe('');
    expect(api.success('保存成功')).toBe('ok');
    expect(success).toHaveBeenCalledWith('保存成功', undefined);
    expect(api.error(404, { duration: 5000 })).toBe('bad');
    expect(error).toHaveBeenCalledWith('404', { duration: 5000 });
    expect(api.warning('请选择数据', 0)).toBe('warn');
    expect(warning).toHaveBeenCalledWith('请选择数据', {
      duration: Number.POSITIVE_INFINITY,
    });
  });
});
