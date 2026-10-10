<script setup lang="ts">
import { useTemplateRef } from 'vue';

import { takeSelectedFiles } from './files';

const props = withDefaults(
  defineProps<{
    accept?: string;
    disabled?: boolean;
    max?: number;
    multiple?: boolean;
  }>(),
  {
    multiple: false,
  },
);

const emit = defineEmits<{
  select: [files: File[]];
}>();

const inputRef = useTemplateRef<HTMLInputElement>('inputRef');

function openPicker() {
  if (props.disabled) return;
  inputRef.value?.click();
}

function onChange(event: Event) {
  const input = event.target;
  if (!(input instanceof HTMLInputElement)) return;
  const files = takeSelectedFiles(input.files, {
    max: props.max,
    multiple: props.multiple,
  });
  if (files.length > 0) {
    emit('select', files);
  }
  input.value = '';
}
</script>

<template>
  <div class="inline-flex" @click="openPicker">
    <input
      ref="inputRef"
      type="file"
      class="sr-only"
      tabindex="-1"
      :accept="accept"
      :multiple="multiple"
      :disabled="disabled"
      @click.stop
      @change="onChange"
    />
    <slot />
  </div>
</template>
