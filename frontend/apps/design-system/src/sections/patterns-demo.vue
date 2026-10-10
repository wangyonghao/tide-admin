<script lang="ts" setup>
import { ref } from 'vue';

import { Button } from '@tide/ui/button';
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@tide/ui/card';
import { Separator } from '@tide/ui/separator';
import {
  ConfirmAction,
  type ConfirmActionExpose,
} from '@tide/ui-patterns/confirm-action';
import { FilterInput } from '@tide/ui-patterns/filter-input';
import { toast } from '@tide/ui-patterns/toast';
import { ToolbarActions } from '@tide/ui-patterns/toolbar-actions';

defineOptions({ name: 'PatternsDemo' });

const keyword = ref('');
const confirmRef = ref<ConfirmActionExpose | null>(null);
const confirmResult = ref('尚未操作');

function showToast(kind: 'error' | 'info' | 'success' | 'warning') {
  const text = {
    error: '保存失败',
    info: '已加入队列',
    success: '已保存',
    warning: '名称重复',
  }[kind];
  toast[kind](text, { description: '这条提示来自 @tide/ui-patterns/toast。' });
}

async function askConfirm() {
  const ok = await confirmRef.value?.ask({
    cancelText: '取消',
    confirmText: '删除',
    description: '只更新本页状态，不会请求后台。',
    title: '删除这条演示？',
    tone: 'destructive',
  });
  confirmResult.value = ok ? '已确认' : '已取消';
}
</script>

<template>
  <Card>
    <CardHeader>
      <CardTitle>页面模式</CardTitle>
      <CardDescription>
        来自 @tide/ui-patterns。确认和 toast 都停在这一页。
      </CardDescription>
    </CardHeader>
    <CardContent class="flex flex-col gap-6">
      <section class="flex max-w-sm flex-col gap-2">
        <h2 class="text-sm font-medium">FilterInput</h2>
        <FilterInput v-model="keyword" placeholder="筛选关键字" />
        <p class="text-muted-foreground text-xs">
          当前值：{{ keyword === '' ? '（空字符串）' : keyword }}
        </p>
      </section>

      <Separator />

      <section class="flex flex-col gap-3">
        <h2 class="text-sm font-medium">ToolbarActions</h2>
        <ToolbarActions>
          <Button type="button" @click="showToast('success')">成功</Button>
          <Button type="button" variant="secondary" @click="showToast('info')">
            信息
          </Button>
          <Button type="button" variant="outline" @click="showToast('warning')">
            警告
          </Button>
          <Button
            type="button"
            variant="destructive"
            @click="showToast('error')"
          >
            失败
          </Button>
        </ToolbarActions>
      </section>

      <Separator />

      <section class="flex flex-col gap-3">
        <h2 class="text-sm font-medium">ConfirmAction</h2>
        <ToolbarActions>
          <Button type="button" variant="destructive" @click="askConfirm">
            请求确认
          </Button>
        </ToolbarActions>
        <p class="text-muted-foreground text-xs">结果：{{ confirmResult }}</p>
        <ConfirmAction ref="confirmRef" />
      </section>
    </CardContent>
  </Card>
</template>
