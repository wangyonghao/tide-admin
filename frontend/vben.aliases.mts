import { fileURLToPath } from 'node:url';

/** Absolute path under frontend/ */
export function frontendPath(...parts: string[]) {
  return fileURLToPath(new URL(`./${parts.join('/')}`, import.meta.url));
}

/**
 * `#/ui` and `#/ui-patterns` stay as app import paths for this release.
 * The source of truth is `@tide/ui` and `@tide/ui-patterns`.
 * `#/ui-patterns` must be matched before `#/ui`.
 */
export function createTideUiAliases() {
  return [
    {
      find: /^#\/ui-patterns(?=\/|$)/,
      replacement: frontendPath('packages/ui-patterns/src'),
    },
    {
      find: /^#\/ui(?=\/|$)/,
      replacement: frontendPath('packages/ui/src'),
    },
  ];
}

/** Absolute path under frontend/src/vben/<pkg>/... */
export function vbenSrc(...parts: string[]) {
  return frontendPath('src/vben', ...parts);
}

/**
 * Maps former @vben/* workspace packages to src/vben / configs.
 * More-specific subpaths must come before the package root entry.
 */
export function createVbenAliases() {
  return {
    '@vben/tailwind-config/theme': frontendPath('src/styles/theme.css'),
    '@vben/tailwind-config': frontendPath('src/styles/theme.css'),

    '@vben/styles/naive': vbenSrc('styles/naive/index.css'),
    '@vben/styles/global': vbenSrc('styles/global/index.scss'),
    '@vben/styles/antd': vbenSrc('styles/antd/index.css'),
    '@vben/styles/antdv-next': vbenSrc('styles/antdv-next/index.css'),
    '@vben/styles/ele': vbenSrc('styles/ele/index.css'),
    '@vben/styles': vbenSrc('styles/index.ts'),

    '@vben/plugins/echarts': vbenSrc('plugins/echarts/index.ts'),
    '@vben/plugins/tiptap': vbenSrc('plugins/tiptap/index.ts'),
    '@vben/plugins/vxe-table': vbenSrc('plugins/vxe-table/index.ts'),
    '@vben/plugins/motion': vbenSrc('plugins/motion/index.ts'),
    '@vben/plugins': vbenSrc('plugins/index.ts'),

    '@vben/common-ui/es/tippy': vbenSrc('common-ui/components/tippy/index.ts'),
    '@vben/common-ui/es/loading': vbenSrc(
      'common-ui/components/loading/index.ts',
    ),
    '@vben/common-ui': vbenSrc('common-ui/index.ts'),

    '@vben/types/global': vbenSrc('types/global.d.ts'),
    '@vben/types': vbenSrc('types/index.ts'),

    '@vben/constants': vbenSrc('constants/index.ts'),
    '@vben/utils': vbenSrc('utils/index.ts'),
    '@vben/icons': vbenSrc('icons/index.ts'),
    '@vben/stores': vbenSrc('stores/index.ts'),
    '@vben/preferences': vbenSrc('preferences/index.ts'),
    '@vben/locales': vbenSrc('locales/index.ts'),
    '@vben/request': vbenSrc('request/index.ts'),
    '@vben/hooks': vbenSrc('hooks/index.ts'),
    '@vben/layouts': vbenSrc('layouts/index.ts'),
  } as const;
}
