import { fileURLToPath } from 'node:url';

import tailwindcss from '@tailwindcss/vite';
import vue from '@vitejs/plugin-vue';
import { defineConfig } from 'vite';

const themeCss = fileURLToPath(
  new URL('../admin/src/styles/theme.css', import.meta.url),
);

export default defineConfig({
  plugins: [vue(), tailwindcss()],
  resolve: {
    alias: {
      '@vben/tailwind-config/theme': themeCss,
      '@vben/tailwind-config': themeCss,
    },
    dedupe: ['vue'],
  },
  server: {
    host: true,
    port: 6174,
    // Theme stylesheet is owned by the admin app; workspace packages sit above.
    fs: {
      allow: ['../..'],
    },
  },
});
