<script setup lang="ts">
import { computed } from 'vue';

import { IconifyIcon } from '@vben/icons';

const props = defineProps<{ icon?: string }>();

const name = computed(() => {
  const value = props.icon?.trim() ?? '';
  if (!value) return '';
  if (value.includes(':') || value.startsWith('http') || value.startsWith('/')) {
    return value;
  }
  return `lucide:${value}`;
});
</script>

<template>
  <IconifyIcon
    v-if="name"
    :icon="name"
    class="size-4 shrink-0"
    aria-hidden="true"
  />
</template>
