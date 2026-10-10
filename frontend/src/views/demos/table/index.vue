<script setup lang="ts">
import { Page } from '@vben/common-ui';

const columns = [
  { key: 'no', title: 'No' },
  { key: 'title', title: 'Title' },
  { key: 'length', title: 'Length' },
];
const data = [
  { length: '4:18', no: 3, title: 'Wonderwall' },
  { length: '4:48', no: 4, title: "Don't Look Back in Anger" },
  { length: '7:27', no: 12, title: 'Champagne Supernova' },
];
</script>

<template>
  <Page
    description="静态示例。产品列表用 Vxe，这里不再使用 Naive 表格。"
    title="表格示例"
  >
    <div class="border-border overflow-hidden rounded-md border">
      <table class="w-full text-sm">
        <thead class="bg-muted/50">
          <tr>
            <th
              v-for="column in columns"
              :key="column.key"
              class="px-3 py-2 text-left font-medium"
            >
              {{ column.title }}
            </th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="row in data"
            :key="row.no"
            class="border-border border-t"
          >
            <td class="px-3 py-2">
              {{ row.no }}
            </td>
            <td class="px-3 py-2">
              {{ row.title }}
            </td>
            <td class="px-3 py-2">
              {{ row.length }}
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </Page>
</template>
