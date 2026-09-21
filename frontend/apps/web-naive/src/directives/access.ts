import type { App, DirectiveBinding } from 'vue';

import { useAccessStore } from '@vben/stores';

import { useUserStore } from '#/store/user';

function normalize(value: string | string[] | undefined): string[] {
  if (!value) return [];
  return Array.isArray(value) ? value.filter(Boolean) : [value];
}

function hasAuth(binding: DirectiveBinding<string | string[]>) {
  const codes = normalize(binding.value);
  if (codes.length === 0) return true;

  const type = binding.arg ?? 'code';
  if (type === 'role') {
    const userStore = useUserStore();
    return codes.some((role) => userStore.hasRole(role));
  }

  const accessStore = useAccessStore();
  const accessCodes = accessStore.accessCodes ?? [];
  if (accessCodes.includes('*:*:*')) return true;
  return codes.some((code) => accessCodes.includes(code));
}

function applyAccess(el: HTMLElement, binding: DirectiveBinding<string | string[]>) {
  if (!hasAuth(binding)) {
    el.remove();
  }
}

/**
 * 注册 v-access / v-access:code / v-access:role
 */
export function registerAccessDirective(app: App) {
  app.directive('access', {
    mounted: applyAccess,
  });
}
