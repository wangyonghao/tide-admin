<script lang="ts" setup>
import { computed, defineAsyncComponent, ref, useAttrs } from 'vue';

import { IconifyIcon } from '@vben/icons';

import { Button } from '#/ui/button';
import { Input } from '#/ui/input';
import { Textarea } from '#/ui/textarea';

import { commitFormText, displayFormText } from './empty-value';
import {
  textCountLabel,
  textMaxLength,
  textRows,
  textShowsCount,
  textUsesPair,
  textUsesPassword,
  textUsesTextarea,
} from './text-mode';

defineOptions({
  name: 'FormTextInput',
  inheritAttrs: false,
});

const props = defineProps<{
  textarea?: boolean;
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
const areaRef = ref<{ $el?: { focus?: () => void } } | null>(null);
const naiveRef = ref<{ focus?: () => void } | null>(null);
const revealed = ref(false);

const OMIT = new Set([
  'autosize',
  'clearable',
  'maxLength',
  'maxlength',
  'modelValue',
  'onUpdate:modelValue',
  'onUpdate:value',
  'pair',
  'rows',
  'showCount',
  'showPasswordOn',
  'showWordLimit',
  'textarea',
  'type',
  'value',
]);

const fieldAttrs = computed(() => ({
  ...attrs,
  textarea: props.textarea,
}));

const pairOnly = computed(() => textUsesPair(fieldAttrs.value));
const asTextarea = computed(
  () => !pairOnly.value && textUsesTextarea(fieldAttrs.value),
);
const asPassword = computed(
  () =>
    !pairOnly.value && !asTextarea.value && textUsesPassword(fieldAttrs.value),
);
const maxLength = computed(() => textMaxLength(fieldAttrs.value));
const showCount = computed(() => textShowsCount(fieldAttrs.value));
const rows = computed(() => textRows(fieldAttrs.value));
const shown = computed(() => displayFormText(props.value));
const countLabel = computed(() => textCountLabel(shown.value, maxLength.value));

const plainAttrs = computed(() => {
  const next: Record<string, unknown> = {};
  for (const [key, val] of Object.entries(attrs)) {
    if (OMIT.has(key)) continue;
    next[key] = val;
  }
  return next;
});

const naiveAttrs = computed(() => {
  const next: Record<string, unknown> = {};
  for (const [key, val] of Object.entries(attrs)) {
    if (
      key === 'modelValue' ||
      key === 'onUpdate:modelValue' ||
      key === 'textarea' ||
      key === 'value' ||
      key === 'onUpdate:value'
    ) {
      continue;
    }
    next[key] = val;
  }
  return next;
});

const naiveValue = computed(() =>
  props.value == null ? null : String(props.value),
);

const inputType = computed(() => {
  if (asPassword.value) return revealed.value ? 'text' : 'password';
  const type = attrs.type;
  if (typeof type === 'string' && type !== 'textarea') return type;
  return 'text';
});

function onText(value: number | string) {
  emit('update:value', commitFormText(value));
}

function onNaive(value: null | string) {
  emit('update:value', value);
}

defineExpose({
  focus: () => {
    if (pairOnly.value) {
      naiveRef.value?.focus?.();
      return;
    }
    const target = asTextarea.value ? areaRef.value : inputRef.value;
    target?.$el?.focus?.();
  },
});
</script>

<template>
  <NInput
    v-if="pairOnly"
    ref="naiveRef"
    v-bind="naiveAttrs"
    :value="naiveValue"
    @update:value="onNaive"
  />
  <div v-else class="w-full">
    <div :class="asPassword ? 'relative' : undefined">
      <Textarea
        v-if="asTextarea"
        ref="areaRef"
        v-bind="plainAttrs"
        :rows="rows"
        :maxlength="maxLength"
        :model-value="shown"
        @update:model-value="onText"
      />
      <Input
        v-else
        ref="inputRef"
        v-bind="plainAttrs"
        :type="inputType"
        :maxlength="maxLength"
        :class="asPassword ? 'pr-10' : undefined"
        :model-value="shown"
        @update:model-value="onText"
      />
      <Button
        v-if="asPassword"
        type="button"
        variant="ghost"
        size="icon"
        class="absolute top-1/2 right-1 size-8 -translate-y-1/2"
        tabindex="-1"
        @click="revealed = !revealed"
      >
        <IconifyIcon
          :icon="revealed ? 'lucide:eye-off' : 'lucide:eye'"
          class="size-4"
        />
      </Button>
    </div>
    <div v-if="showCount" class="text-muted-foreground mt-1 text-right text-xs">
      {{ countLabel }}
    </div>
  </div>
</template>
