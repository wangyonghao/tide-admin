import { Window } from 'happy-dom';

const added: string[] = [];

/**
 * 文档示例会经过 `@vben/common-ui` 和 ant-design-vue，模块加载时读取浏览器全局。
 * 预渲染前把 happy-dom 上 Node 还没有的全局补上，buildEnd 里卸掉，
 * 避免 PWA 写 service worker 时拿到 http location。
 */
export function installDocsSsrDom() {
  if (typeof globalThis.window !== 'undefined') {
    return;
  }
  const dom = new Window({ url: 'http://localhost:6173/' });
  const target = globalThis as unknown as Record<string, unknown>;
  const source = dom as unknown as Record<string, unknown>;
  const skip = new Set([
    'constructor',
    'console',
    'global',
    'globalThis',
    'process',
    'prototype',
  ]);

  let proto: object | null = dom;
  while (proto && proto !== Object.prototype) {
    for (const key of Object.getOwnPropertyNames(proto)) {
      if (skip.has(key) || target[key] != null) continue;
      try {
        const value = source[key];
        if (value === undefined) continue;
        target[key] = value;
        added.push(key);
      } catch {
        // 个别访问器离开文档就会抛。
      }
    }
    proto = Object.getPrototypeOf(proto);
  }

  // document 存在时 VitePress 会走浏览器分支，这两个标识符只在页面 HTML 里注入。
  if (target.__VP_HASH_MAP__ == null) {
    target.__VP_HASH_MAP__ = {};
    added.push('__VP_HASH_MAP__');
  }
  if (target.__ASSETS_DIR__ == null) {
    target.__ASSETS_DIR__ = 'assets';
    added.push('__ASSETS_DIR__');
  }
}

export function uninstallDocsSsrDom() {
  const target = globalThis as unknown as Record<string, unknown>;
  for (const key of added) {
    delete target[key];
  }
  added.length = 0;
}
