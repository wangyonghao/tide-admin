<script lang="ts" setup>
import FormTextInput from './FormTextInput.vue';

defineOptions({
  name: 'FormTextarea',
  inheritAttrs: false,
});

defineProps<{
  value?: null | number | string;
}>();

const emit = defineEmits<{
  'update:value': [value: null | string];
}>();
</script>

<template>
  <FormTextInput
    textarea
    v-bind="$attrs"
    :value="value"
    @update:value="emit('update:value', $event)"
  />
</template>
