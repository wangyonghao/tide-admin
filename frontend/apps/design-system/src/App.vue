<script lang="ts" setup>
import { ref } from 'vue';

import { Button } from '@tide/ui/button';
import { Toaster } from '@tide/ui/sonner';

import AtomsDemo from './sections/atoms-demo.vue';
import NotesDemo from './sections/notes-demo.vue';
import PatternsDemo from './sections/patterns-demo.vue';

defineOptions({ name: 'DesignSystemApp' });

const dark = ref(false);

function toggleTheme() {
  dark.value = !dark.value;
  document.documentElement.classList.toggle('dark', dark.value);
}
</script>

<template>
  <div class="bg-background text-foreground min-h-full">
    <header class="border-border bg-card border-b">
      <div
        class="mx-auto flex max-w-5xl items-center justify-between gap-4 px-6 py-4"
      >
        <div>
          <p class="text-muted-foreground text-xs tracking-wide uppercase">
            @tide/ui · @tide/ui-patterns
          </p>
          <h1 class="text-xl font-semibold">Tide Design System</h1>
        </div>
        <Button type="button" variant="outline" @click="toggleTheme">
          {{ dark ? '浅色' : '深色' }}
        </Button>
      </div>
    </header>
    <main class="mx-auto flex max-w-5xl flex-col gap-8 px-6 py-8">
      <NotesDemo />
      <AtomsDemo />
      <PatternsDemo />
    </main>
    <Toaster :theme="dark ? 'dark' : 'light'" position="top-center" />
  </div>
</template>
