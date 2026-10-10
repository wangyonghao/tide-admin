<script lang="ts" setup>
import { Page } from '@vben/common-ui';

import { Button } from '#/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '#/ui/card';
import { toast } from '#/ui-patterns/toast';

function error() {
  toast.error('Once upon a time you dressed so fine');
}

function warning() {
  toast.warning('How many roads must a man walk down');
}
function success() {
  toast.success(
    'Cause you walked hand in hand With another man in my place',
  );
}
function loading() {
  toast.loading(
    'If I were you, I will realize that I love you more than any other guy',
  );
}

function notify(type: 'error' | 'info' | 'success' | 'warning') {
  const show = toast[type];
  show('说点啥呢', { description: '想不出来', duration: 2500 });
}
</script>

<template>
  <Page
    description="按钮和提示已改成原子控件与 Sonner。这里不再依赖 Naive 的 Message / Notification。"
    title="组件使用演示"
  >
    <Card class="mb-5">
      <CardHeader>
        <CardTitle>按钮</CardTitle>
      </CardHeader>
      <CardContent>
        <div class="flex flex-wrap gap-2">
          <Button type="button" variant="outline">Default</Button>
          <Button type="button" variant="secondary">Tertiary</Button>
          <Button type="button">Primary</Button>
          <Button type="button" variant="secondary">Info</Button>
          <Button type="button" variant="outline">Success</Button>
          <Button type="button" variant="outline">Warning</Button>
          <Button type="button" variant="destructive">Error</Button>
        </div>
      </CardContent>
    </Card>

    <Card class="mb-5">
      <CardHeader>
        <CardTitle>Message</CardTitle>
      </CardHeader>
      <CardContent>
        <div class="flex flex-wrap gap-2">
          <Button type="button" variant="destructive" @click="error">
            错误
          </Button>
          <Button type="button" variant="outline" @click="warning">警告</Button>
          <Button type="button" variant="outline" @click="success">成功</Button>
          <Button type="button" @click="loading">加载中</Button>
        </div>
      </CardContent>
    </Card>

    <Card class="mb-5">
      <CardHeader>
        <CardTitle>Notification</CardTitle>
      </CardHeader>
      <CardContent>
        <div class="flex flex-wrap gap-2">
          <Button type="button" variant="destructive" @click="notify('error')">
            错误
          </Button>
          <Button type="button" variant="outline" @click="notify('warning')">
            警告
          </Button>
          <Button type="button" variant="outline" @click="notify('success')">
            成功
          </Button>
          <Button type="button" @click="notify('info')">加载中</Button>
        </div>
      </CardContent>
    </Card>
  </Page>
</template>
