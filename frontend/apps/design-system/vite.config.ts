import tailwindcss from '@tailwindcss/vite';
import vue from '@vitejs/plugin-vue';
import { defineConfig } from 'vite';

export default defineConfig({
  plugins: [vue(), tailwindcss()],
  resolve: {
    dedupe: ['vue'],
  },
  server: {
    host: true,
    port: 6174,
    // Token stylesheet and workspace packages sit above this app.
    fs: {
      allow: ['../..'],
    },
  },
});
