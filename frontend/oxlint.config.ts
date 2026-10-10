import { defineConfig } from 'oxlint';

export default defineConfig({
  categories: {
    correctness: 'error',
    suspicious: 'warn',
  },
  env: {
    browser: true,
    es2021: true,
    node: true,
  },
  ignorePatterns: [
    '**/dist/**',
    '**/node_modules/**',
    'apps/docs/**',
    '**/*.json',
    '**/*.md',
  ],
  plugins: ['typescript', 'unicorn', 'vue'],
  rules: {
    'eslint/no-unused-vars': 'warn',
    'typescript/no-explicit-any': 'off',
    'vue/prefer-import-from-vue': 'error',
  },
});
