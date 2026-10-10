<script lang="ts" setup>
import { ref } from 'vue';

import { Badge } from '@tide/ui/badge';
import { Button } from '@tide/ui/button';
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@tide/ui/card';
import { Checkbox } from '@tide/ui/checkbox';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogTitle,
  DialogTrigger,
} from '@tide/ui/dialog';
import { Input } from '@tide/ui/input';
import { Label } from '@tide/ui/label';
import { RadioGroup, RadioGroupItem } from '@tide/ui/radio-group';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@tide/ui/select';
import { Separator } from '@tide/ui/separator';
import { Skeleton } from '@tide/ui/skeleton';
import { Switch, type SwitchScalar } from '@tide/ui/switch';
import { Textarea } from '@tide/ui/textarea';

defineOptions({ name: 'AtomsDemo' });

const name = ref('');
const note = ref('');
const agreed = ref(false);
const enabled = ref(true);
const bit = ref<SwitchScalar>(1);
const tone = ref('default');
const role = ref('editor');
const dialogOpen = ref(false);

const buttonVariants = [
  'default',
  'secondary',
  'outline',
  'destructive',
  'ghost',
  'link',
] as const;
</script>

<template>
  <Card>
    <CardHeader>
      <CardTitle>原子</CardTitle>
      <CardDescription>来自 @tide/ui。颜色走同一套 HSL token。</CardDescription>
    </CardHeader>
    <CardContent class="flex flex-col gap-6">
      <section class="flex flex-col gap-3">
        <h2 class="text-sm font-medium">Button</h2>
        <div class="flex flex-wrap items-center gap-2">
          <Button
            v-for="variant in buttonVariants"
            :key="variant"
            type="button"
            :variant="variant"
          >
            {{ variant }}
          </Button>
          <Button type="button" size="sm">小号</Button>
          <Button type="button" loading>提交中</Button>
        </div>
      </section>

      <Separator />

      <section class="grid gap-4 sm:grid-cols-2">
        <div class="flex flex-col gap-2">
          <Label for="ds-name">Input</Label>
          <Input id="ds-name" v-model="name" placeholder="名称" />
          <p class="text-muted-foreground text-xs">当前值：{{ name || '（空）' }}</p>
        </div>
        <div class="flex flex-col gap-2">
          <Label for="ds-note">Textarea</Label>
          <Textarea id="ds-note" v-model="note" placeholder="备注" />
        </div>
      </section>

      <section class="flex flex-col gap-2">
        <Label>Select</Label>
        <Select v-model="role">
          <SelectTrigger class="w-56">
            <SelectValue placeholder="选择角色" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="viewer">查看</SelectItem>
            <SelectItem value="editor">编辑</SelectItem>
            <SelectItem value="admin">管理</SelectItem>
          </SelectContent>
        </Select>
        <p class="text-muted-foreground text-xs">当前值：{{ role }}</p>
      </section>

      <Separator />

      <section class="flex flex-col gap-3">
        <h2 class="text-sm font-medium">Checkbox / Switch / Radio</h2>
        <label class="flex items-center gap-2 text-sm">
          <Checkbox v-model="agreed" />
          已阅读说明（{{ agreed ? '是' : '否' }}）
        </label>
        <label class="flex items-center gap-2 text-sm">
          <Switch v-model="enabled" />
          布尔开关（{{ enabled ? '开' : '关' }}）
        </label>
        <label class="flex items-center gap-2 text-sm">
          <Switch v-model="bit" :checked-value="1" :unchecked-value="0" />
          1 / 0 开关（{{ bit }}）
        </label>
        <RadioGroup v-model="tone" class="flex flex-wrap gap-4">
          <label class="flex items-center gap-2 text-sm">
            <RadioGroupItem value="default" />
            默认
          </label>
          <label class="flex items-center gap-2 text-sm">
            <RadioGroupItem value="quiet" />
            安静
          </label>
        </RadioGroup>
      </section>

      <Separator />

      <section class="flex flex-col gap-3">
        <h2 class="text-sm font-medium">Badge / Skeleton / Dialog</h2>
        <div class="flex flex-wrap gap-2">
          <Badge>default</Badge>
          <Badge variant="secondary">secondary</Badge>
          <Badge variant="outline">outline</Badge>
          <Badge variant="success">success</Badge>
          <Badge variant="warning">warning</Badge>
          <Badge variant="destructive">destructive</Badge>
        </div>
        <div class="flex flex-col gap-2">
          <Skeleton class="h-4 w-48" />
          <Skeleton class="h-4 w-32" />
        </div>
        <Dialog v-model:open="dialogOpen">
          <DialogTrigger as-child>
            <Button type="button" variant="outline">打开对话框</Button>
          </DialogTrigger>
          <DialogContent :open="dialogOpen" class="max-w-md">
            <DialogTitle>原子对话框</DialogTitle>
            <DialogDescription class="mt-2">
              这里没有业务表单，只确认 Dialog 能打开和关闭。
            </DialogDescription>
          </DialogContent>
        </Dialog>
      </section>
    </CardContent>
  </Card>
</template>
