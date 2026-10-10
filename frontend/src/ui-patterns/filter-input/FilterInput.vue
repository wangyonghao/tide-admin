<script lang="ts" setup>
import { computed, useAttrs } from 'vue';

import { Input } from '#/ui/input';

defineOptions({ name: 'FilterInput', inheritAttrs: false });

const model = defineModel<string>({ default: '' });
const attrs = useAttrs();
const inputAttrs = computed(() => {
  const { class: _class, ...rest } = attrs;
  return rest;
});

function clear() {
  model.value = '';
}
</script>

<template>
  <div class="relative w-full" :class="attrs.class">
    <Input v-bind="inputAttrs" v-model="model" class="pr-8" />
    <button
      v-if="model"
      type="button"
      class="text-muted-foreground absolute top-1/2 right-2 -translate-y-1/2 text-sm leading-none"
      @click="clear"
    >
      ×
    </button>
  </div>
</template>
