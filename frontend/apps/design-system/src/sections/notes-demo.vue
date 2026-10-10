<script lang="ts" setup>
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@tide/ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@tide/ui/tabs';

defineOptions({ name: 'NotesDemo' });
</script>

<template>
  <Card>
    <CardHeader>
      <CardTitle>范围</CardTitle>
      <CardDescription>
        这个应用只演示原子和页面模式。产品页面仍在后台
        <code>frontend/apps/admin</code>。
      </CardDescription>
    </CardHeader>
    <CardContent>
      <Tabs default-value="usage">
        <TabsList>
          <TabsTrigger value="usage">
            用法
          </TabsTrigger>
          <TabsTrigger value="avoid">
            反例
          </TabsTrigger>
        </TabsList>
        <TabsContent
          value="usage"
          class="text-sm leading-6"
        >
          <p>按路径导入，不从包根一次性取出全部组件。</p>
          <pre
            class="bg-muted mt-3 overflow-x-auto rounded-md p-3 text-xs"
          ><code>import { Button } from '@tide/ui/button';
import { toast } from '@tide/ui-patterns/toast';
import { ConfirmAction } from '@tide/ui-patterns/confirm-action';</code></pre>
        </TabsContent>
        <TabsContent
          value="avoid"
          class="text-sm leading-6"
        >
          <ul class="list-disc space-y-1 pl-5">
            <li>不要引用后台页面、接口或 <code>#/</code> 业务别名。</li>
            <li>不要再引入 <code>naive-ui</code>，也不要包一层同名组件。</li>
            <li>不要把列表、权限或表单流程写进这个应用。</li>
          </ul>
        </TabsContent>
      </Tabs>
    </CardContent>
  </Card>
</template>
