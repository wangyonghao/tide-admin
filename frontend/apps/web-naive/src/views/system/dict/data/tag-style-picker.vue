<script setup lang="ts">
import { onMounted, ref } from 'vue';

import { NRadioButton, NRadioGroup, NSelect } from 'naive-ui';

import { tagSelectOptions } from '#/components/dict';

/**
 * 需要禁止透传
 * 不禁止会有奇怪的bug 会绑定到selectType上
 * TODO: 未知原因 有待研究
 */
defineOptions({ inheritAttrs: false });

defineEmits<{ deselect: [] }>();

const radioGroupOptions = [
  { label: '默认颜色', value: 'default' },
  // { label: '自定义颜色', value: 'custom' },
] as const;

type SelectType = (typeof radioGroupOptions)[number]['value'];

const selectType = defineModel<SelectType>('selectType', {
  default: 'default',
});

/**
 * color必须为hex颜色或者undefined
 */
const color = defineModel<string | undefined>('value', {
  default: undefined,
});

function handleSelectTypeChange(value: SelectType) {
  // 必须给默认hex颜色 不能为空字符串
  color.value = value === 'custom' ? '#1677ff' : undefined;
}

// const { isDark } = usePreferences();
// const theme = computed(() => {
//   return isDark.value ? 'black' : 'white';
// });

const tagSelectOption = ref<[]>([]);
onMounted(() => {
  tagSelectOption.value = tagSelectOptions();
});
</script>

<template>
  <div class="flex flex-1 items-center gap-[6px]">
    <NRadioGroup
      v-model:value="selectType"
      @update:value="handleSelectTypeChange"
    >
      <NRadioButton
        v-for="option in radioGroupOptions"
        :key="option.value"
        :value="option.value"
      >
        {{ option.label }}
      </NRadioButton>
    </NRadioGroup>
    <NSelect
      v-if="selectType === 'default'"
      v-model:value="color"
      :options="tagSelectOption"
      clearable
      class="flex-1"
      placeholder="请选择标签样式"
      @clear="$emit('deselect')"
    >
      <template #default="{ option }">
        <component :is="option.label" />
      </template>
    </NSelect>
  </div>
</template>
