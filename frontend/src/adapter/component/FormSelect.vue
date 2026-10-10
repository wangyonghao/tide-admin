<script lang="ts" setup>
import type { NormalizedSelectOption } from './select-value';

import { computed, ref, useAttrs } from 'vue';

import { IconifyIcon } from '@vben/icons';

import { Checkbox } from '#/ui/checkbox';
import { Input } from '#/ui/input';
import { Popover, PopoverContent, PopoverTrigger } from '#/ui/popover';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '#/ui/select';

import {
  commitSelectValue,
  filterSelectOptions,
  isOptionSelected,
  isSelectEmpty,
  normalizeSelectOptions,
  selectDisplayKey,
  selectMultipleKeys,
  selectSummary,
  toggleSelectValues,
} from './select-value';

defineOptions({
  name: 'FormSelect',
  inheritAttrs: false,
});

const props = defineProps<{
  clearable?: boolean;
  disabled?: boolean;
  filterable?: boolean;
  multiple?: boolean;
  options?: unknown;
  placeholder?: string;
  value?: unknown;
}>();

const emit = defineEmits<{
  'update:value': [
    value:
      | null
      | boolean
      | number
      | string
      | Array<boolean | number | string>,
  ];
}>();

const attrs = useAttrs();
const query = ref('');
const open = ref(false);

const triggerLook =
  'border-input bg-background ring-offset-background focus-visible:ring-ring flex h-10 w-full items-center justify-between rounded-md border px-3 py-2 text-sm shadow-xs focus-visible:ring-1 focus-visible:outline-hidden disabled:cursor-not-allowed disabled:opacity-50';

const options = computed(() =>
  normalizeSelectOptions(props.options ?? attrs.options),
);
const filtered = computed(() =>
  props.filterable
    ? filterSelectOptions(options.value, query.value)
    : options.value,
);
const empty = computed(() => isSelectEmpty(props.value));
const displayKey = computed(
  () => selectDisplayKey(props.value, options.value) ?? null,
);
const multipleKeys = computed(() =>
  selectMultipleKeys(props.value, options.value),
);
const summary = computed(() => selectSummary(props.value, options.value));
const usePanel = computed(() => props.filterable === true);
const triggerText = computed(() => {
  if (props.multiple) return summary.value || props.placeholder || '';
  const match = options.value.find((option) => option.value === props.value);
  return match?.label || props.placeholder || '';
});
const triggerMuted = computed(() => {
  if (props.multiple) return summary.value.length === 0;
  return !options.value.some((option) => option.value === props.value);
});

function onOpen(next: boolean) {
  open.value = next;
  if (!next) query.value = '';
}

function clear() {
  emit('update:value', null);
}

function onSingle(key: unknown) {
  if (props.multiple) {
    const keys = Array.isArray(key) ? key : [];
    const next = keys
      .map((item) => commitSelectValue(item, options.value))
      .filter((item) => item != null);
    emit('update:value', next.length === 0 ? null : next);
    return;
  }
  emit('update:value', commitSelectValue(key, options.value));
}

function onToggle(
  option: NormalizedSelectOption,
  checked: boolean | 'indeterminate',
) {
  if (option.disabled) return;
  emit(
    'update:value',
    toggleSelectValues(props.value, option, checked === true),
  );
}

function choose(option: NormalizedSelectOption) {
  if (option.disabled || props.disabled) return;
  emit('update:value', option.value);
  open.value = false;
}
</script>

<template>
  <!-- 可搜索不走 Select 内容区：套件下拉的可视高度跟触发器绑在一起，搜索框放不下。 -->
  <Popover
    v-if="usePanel"
    :open="open"
    @update:open="onOpen"
  >
    <PopoverTrigger as-child>
      <button
        type="button"
        :disabled="disabled"
        :class="[triggerLook, attrs.class]"
      >
        <span
          class="line-clamp-1 flex-auto text-left"
          :class="triggerMuted ? 'text-muted-foreground' : ''"
        >
          {{ triggerText }}
        </span>
        <span
          v-if="clearable && !empty"
          class="mr-1 inline-flex size-4 shrink-0 cursor-pointer items-center opacity-50 hover:opacity-100"
          @pointerdown.stop
          @click.stop.prevent="clear"
        >
          <IconifyIcon
            icon="lucide:x"
            class="size-4"
          />
        </span>
        <IconifyIcon
          icon="lucide:chevron-down"
          class="size-4 shrink-0 opacity-50"
        />
      </button>
    </PopoverTrigger>
    <PopoverContent
      align="start"
      class="max-h-60 w-[var(--reka-popover-trigger-width)] min-w-32 overflow-y-auto p-1"
    >
      <div
        v-if="filterable"
        class="p-1"
        @keydown.stop
        @pointerdown.stop
      >
        <Input
          :model-value="query"
          class="h-8"
          placeholder="搜索"
          @update:model-value="query = String($event ?? '')"
        />
      </div>
      <template v-if="multiple">
        <label
          v-for="option in filtered"
          :key="option.key"
          class="flex cursor-pointer items-center gap-2 rounded-sm px-2 py-1.5 text-sm hover:bg-accent"
          :class="option.disabled ? 'pointer-events-none opacity-50' : ''"
        >
          <Checkbox
            :model-value="isOptionSelected(value, option)"
            :disabled="option.disabled || disabled"
            @update:model-value="(checked) => onToggle(option, checked)"
          />
          <span>{{ option.label }}</span>
        </label>
      </template>
      <template v-else>
        <button
          v-for="option in filtered"
          :key="option.key"
          type="button"
          class="flex w-full items-center rounded-sm px-2 py-1.5 text-left text-sm hover:bg-accent disabled:opacity-50"
          :class="value === option.value ? 'bg-accent' : ''"
          :disabled="option.disabled || disabled"
          @click="choose(option)"
        >
          {{ option.label }}
        </button>
      </template>
    </PopoverContent>
  </Popover>

  <Select
    v-else
    :multiple="multiple"
    :open="open"
    :model-value="multiple ? multipleKeys : displayKey"
    :disabled="disabled"
    @update:open="onOpen"
    @update:model-value="onSingle"
  >
    <SelectTrigger
      :class="attrs.class"
      :disabled="disabled"
    >
      <!-- 自己画标签。选项 value 在 Select 里是带类型前缀的 key，不能直接当文案。 -->
      <SelectValue
        v-if="triggerMuted"
        :placeholder="placeholder"
      />
      <SelectValue
        v-else
        :placeholder="placeholder"
      >
        {{ triggerText }}
      </SelectValue>
      <span
        v-if="clearable && !empty"
        class="mr-1 inline-flex size-4 shrink-0 cursor-pointer items-center opacity-50 hover:opacity-100"
        @pointerdown.stop
        @click.stop.prevent="clear"
      >
        <IconifyIcon
          icon="lucide:x"
          class="size-4"
        />
      </span>
    </SelectTrigger>
    <SelectContent>
      <SelectItem
        v-for="option in options"
        :key="option.key"
        :value="option.key"
        :disabled="option.disabled"
      >
        {{ option.label }}
      </SelectItem>
    </SelectContent>
  </Select>
</template>
