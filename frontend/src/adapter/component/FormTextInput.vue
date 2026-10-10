<script lang="ts" setup>
import { computed, defineAsyncComponent, ref, useAttrs } from 'vue';

import { Input } from '#/ui/input';

import { commitFormText, displayFormText } from './empty-value';

defineOptions({
  name: 'FormTextInput',
  inheritAttrs: false,
});

const props = defineProps<{
  value?: null | number | string;
}>();

const emit = defineEmits<{
  'update:value': [value: null | string];
}>();

const NInput = defineAsyncComponent(() =>
  import('naive-ui/es/input').then((res) => res.NInput),
);

const attrs = useAttrs();
const inputRef = ref<{ $el?: { focus?: () => void } } | null>(null);
const naiveRef = ref<{ focus?: () => void } | null>(null);

const OMIT = new Set([
  'autosize',
  'clearable',
  'maxLength',
  'maxlength',
  'modelValue',
  'onUpdate:modelValue',
  'onUpdate:value',
  'pair',
  'showCount',
  'showPasswordOn',
  'showWordLimit',
  'type',
  'value',
]);

/** 文本域、字数统计、密码显隐仍是 Naive 的能力，这一波不改语义。 */
const naiveOnly = computed(() => {
  const type = attrs.type;
  return (
    type === 'textarea' ||
    attrs.autosize != null ||
    attrs.pair != null ||
    attrs.showWordLimit != null ||
    attrs.showPasswordOn != null
  );
});

const plainAttrs = computed(() => {
  const next: Record<string, unknown> = {};
  for (const [key, val] of Object.entries(attrs)) {
    if (OMIT.has(key)) continue;
    next[key] = val;
  }
  const type = attrs.type;
  if (typeof type === 'string' && type !== 'textarea') {
    next.type = type;
  }
  const raw = attrs.maxlength ?? attrs.maxLength;
  if (typeof raw === 'number' || typeof raw === 'string') {
    next.maxlength = raw;
  }
  return next;
});

const naiveValue = computed(() =>
  props.value == null ? null : String(props.value),
);

const naiveAttrs = computed(() => {
  const next: Record<string, unknown> = {};
  for (const [key, val] of Object.entries(attrs)) {
    if (
      key === 'modelValue' ||
      key === 'onUpdate:modelValue' ||
      key === 'value' ||
      key === 'onUpdate:value'
    ) {
      continue;
    }
    next[key] = val;
  }
  return next;
});

function onText(value: number | string) {
  emit('update:value', commitFormText(value));
}

function onNaive(value: null | string) {
  emit('update:value', value);
}

defineExpose({
  focus: () => {
    if (naiveOnly.value) {
      naiveRef.value?.focus?.();
      return;
    }
    inputRef.value?.$el?.focus?.();
  },
});
</script>

<template>
  <NInput
    v-if="naiveOnly"
    ref="naiveRef"
    v-bind="naiveAttrs"
    :value="naiveValue"
    @update:value="onNaive"
  />
  <Input
    v-else
    ref="inputRef"
    v-bind="plainAttrs"
    :model-value="displayFormText(value)"
    @update:model-value="onText"
  />
</template>
