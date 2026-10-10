<script setup lang="ts">
import type { ToasterProps } from 'vue-sonner';

import { cn } from '@vben-core/shared/utils';

import { Toaster as Sonner } from 'vue-sonner';

import 'vue-sonner/style.css';

const props = defineProps<ToasterProps>();
</script>

<template>
  <Sonner
    v-bind="props"
    :class="cn('toaster group', props.class)"
    :style="{
      '--normal-bg': 'var(--color-popover)',
      '--normal-text': 'var(--color-popover-foreground)',
      '--normal-border': 'var(--color-border)',
      '--border-radius': 'var(--radius)',
      ...props.style,
    }"
  />
</template>
