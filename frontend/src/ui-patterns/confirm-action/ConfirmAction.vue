<script lang="ts" setup>
import { ref } from 'vue';

import {
  AlertDialog,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogTitle,
} from '#/ui/alert-dialog';
import { Button } from '#/ui/button';

export interface ConfirmActionOptions {
  cancelText?: string;
  confirmText?: string;
  description?: string;
  title: string;
  tone?: 'default' | 'destructive';
}

export interface ConfirmActionExpose {
  ask: (options: ConfirmActionOptions) => Promise<boolean>;
}

defineOptions({ name: 'ConfirmAction' });

const open = ref(false);
const title = ref('');
const description = ref('');
const confirmText = ref('确定');
const cancelText = ref('取消');
const tone = ref<'default' | 'destructive'>('default');

let pending: ((ok: boolean) => void) | null = null;

function settle(ok: boolean) {
  const resolve = pending;
  pending = null;
  open.value = false;
  resolve?.(ok);
}

function ask(options: ConfirmActionOptions) {
  if (pending) {
    pending(false);
    pending = null;
  }
  title.value = options.title;
  description.value = options.description ?? '';
  confirmText.value = options.confirmText ?? '确定';
  cancelText.value = options.cancelText ?? '取消';
  tone.value = options.tone ?? 'default';
  open.value = true;
  return new Promise<boolean>((resolve) => {
    pending = resolve;
  });
}

function onOpenChange(value: boolean) {
  if (value) {
    open.value = true;
    return;
  }
  settle(false);
}

defineExpose<ConfirmActionExpose>({ ask });
</script>

<template>
  <AlertDialog :open="open" @update:open="onOpenChange">
    <AlertDialogContent :open="open" @close="settle(false)">
      <AlertDialogTitle>{{ title }}</AlertDialogTitle>
      <AlertDialogDescription v-if="description" class="mt-2">
        {{ description }}
      </AlertDialogDescription>
      <div class="mt-4 flex justify-end gap-2">
        <Button type="button" variant="outline" @click="settle(false)">
          {{ cancelText }}
        </Button>
        <Button type="button" :variant="tone" @click="settle(true)">
          {{ confirmText }}
        </Button>
      </div>
    </AlertDialogContent>
  </AlertDialog>
</template>
