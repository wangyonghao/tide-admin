<script setup lang="ts">
import { $t } from '@vben/locales';

import { useMenuState } from '../composables/useMenuState';
import AppDetail from './AppDetail.vue';
import MenuDetail from './MenuDetail.vue';

defineProps<{ coarse: boolean }>();

const state = useMenuState();
</script>

<template>
  <MenuDetail
    v-if="state.selectedNode.value"
    :coarse="coarse"
  />
  <AppDetail v-else-if="state.selectedApp.value" />
  <div
    v-else
    class="px-4 py-6 text-sm text-muted-foreground"
  >
    {{ $t('appMenu.emptyDetail') }}
  </div>
</template>
