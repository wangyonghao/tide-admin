<script lang="ts" setup>
import { usePreferences } from '@vben/preferences';

import { Toaster } from '#/ui/sonner';

defineOptions({ name: 'App' });

const { theme } = usePreferences();
</script>

<template>
  <div class="h-full">
    <RouterView />
    <Toaster
      :theme="theme"
      position="top-center"
    />
  </div>
</template>
