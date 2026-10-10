import { describe, expect, it } from 'vitest';

import {
  menuPayload,
  menuShowsFrameAddress,
  menuShowsPath,
  menuShowsRouteComponent,
  normalizeMenuParentId,
  resolveMenuComponent,
} from './form-values';

describe('menu form values', () => {
  it('treats 0 and empty as the top menu', () => {
    expect(normalizeMenuParentId(0)).toBe('0');
    expect(normalizeMenuParentId('0')).toBe('0');
    expect(normalizeMenuParentId(null)).toBe('0');
    expect(normalizeMenuParentId('12')).toBe('12');
  });

  it('shows path, frame address, and route component on different types', () => {
    expect(menuShowsPath({ type: 1, isFrame: 0 })).toBe(true);
    expect(menuShowsPath({ type: 2, isFrame: 1 })).toBe(false);
    expect(menuShowsPath({ type: 3, isFrame: 0 })).toBe(false);
    expect(menuShowsFrameAddress({ type: 1, isFrame: 1 })).toBe(true);
    expect(menuShowsFrameAddress({ type: 3, isFrame: 1 })).toBe(false);
    expect(menuShowsRouteComponent({ type: 2, isFrame: 0 })).toBe(true);
    expect(menuShowsRouteComponent({ type: 1, isFrame: 0 })).toBe(false);
  });

  it('submits the visible component field', () => {
    expect(
      resolveMenuComponent({
        type: 2,
        isFrame: 0,
        frameUrl: 'https://example.com',
        routeComponent: 'system/user/index',
      }),
    ).toBe('system/user/index');
    expect(
      resolveMenuComponent({
        type: 1,
        isFrame: 1,
        frameUrl: 'https://example.com',
        routeComponent: 'system/user/index',
      }),
    ).toBe('https://example.com');
    expect(
      menuPayload({
        type: 3,
        name: '新增',
        permission: 'system:user:create',
        path: '/ignored',
        frameUrl: 'https://example.com',
        parentId: 0,
      }),
    ).toEqual({
      component: '',
      icon: '',
      id: undefined,
      isFrame: 0,
      name: '新增',
      parentId: '0',
      path: '',
      permission: 'system:user:create',
      sort: 0,
      status: 1,
      type: 3,
      visible: 1,
    });
  });
});
