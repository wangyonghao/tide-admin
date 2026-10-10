<script setup lang="ts">
import type { PrimitiveProps } from 'reka-ui';

import type { ButtonVariants, ButtonVariantSize } from './types';

import { computed } from 'vue';

import { LoaderCircle } from '@vben-core/icons';
import { cn } from '@vben-core/shared/utils';

import { Primitive } from 'reka-ui';

import { buttonVariants } from './button';

interface Props extends PrimitiveProps {
  class?: any;
  disabled?: boolean;
  loading?: boolean;
  size?: ButtonVariantSize;
  variant?: ButtonVariants;
}

const props = withDefaults(defineProps<Props>(), {
  as: 'button',
  class: '',
  disabled: false,
  loading: false,
});

const isDisabled = computed(() => props.disabled || props.loading);
</script>

<template>
  <Primitive
    :as="as"
    :as-child="asChild"
    :disabled="isDisabled"
    :aria-busy="loading || undefined"
    :class="cn(buttonVariants({ variant, size }), props.class)"
  >
    <LoaderCircle v-if="loading" class="mr-2 size-4 shrink-0 animate-spin" />
    <slot></slot>
  </Primitive>
</template>
