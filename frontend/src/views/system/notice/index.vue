<script setup lang="ts">
import type { DataTableColumns } from 'naive-ui';
import type { NoticeResp, NoticeDetailResp } from '#/api/system/notice';

import { h, onMounted, ref } from 'vue';

import { IconifyIcon } from '@vben/icons';
import { $t } from '@vben/locales';

import { SearchOutline } from '@vicons/ionicons5';
import {
  NDataTable,
  NDatePicker,
  NDrawer,
  NDrawerContent,
  NIcon,
  NInput,
  NTag,
  useMessage,
} from 'naive-ui';

import FormSelect from '#/adapter/component/FormSelect.vue';
import { noticeApi } from '#/api/system/notice';
import { useDict } from '#/hooks';
import { Button } from '#/ui/button';
import {
  ConfirmAction,
  type ConfirmActionExpose,
} from '#/ui-patterns/confirm-action';
import { ToolbarActions } from '#/ui-patterns/toolbar-actions';

import NoticeForm from './components/notice-form.vue';
import NoticeView from './components/notice-view.vue';

const message = useMessage();
const confirmAction = ref<ConfirmActionExpose | null>(null);

// ==================== 字典数据 ====================
const {
  notice_type,
  notice_scope_enum,
  notice_method_enum,
  notice_status_enum,
} = useDict(
  'notice_type',
  'notice_scope_enum',
  'notice_method_enum',
  'notice_status_enum',
);

// ==================== 搜索表单 ====================
const searchForm = ref({
  title: '',
  type: null as string | null,
  publishTime: null as [number, number] | null,
  status: null as string | null,
});

// ==================== 表格数据 ====================
const tableData = ref<NoticeResp[]>([]);
const tableLoading = ref(false);
const tablePagination = ref({
  page: 1,
  pageSize: 10,
  itemCount: 0,
  showSizePicker: true,
  pageSizes: [10, 20, 50, 100],
  onChange: (page: number) => {
    tablePagination.value.page = page;
    loadTableData();
  },
  onUpdatePageSize: (pageSize: number) => {
    tablePagination.value.pageSize = pageSize;
    tablePagination.value.page = 1;
    loadTableData();
  },
});

// ==================== 抽屉状态 ====================
const showFormDrawer = ref(false);
const showViewDrawer = ref(false);
const currentNoticeId = ref<string>();
const currentNoticeDetail = ref<NoticeDetailResp>();

// ==================== 表格列定义 ====================
const tableColumns: DataTableColumns<NoticeResp> = [
  {
    title: '序号',
    key: 'index',
    width: 60,
    render: (_row, index) =>
      (tablePagination.value.page - 1) * tablePagination.value.pageSize +
      index +
      1,
  },
  {
    title: $t('system.notice.title'),
    key: 'title',
    minWidth: 200,
  },
  {
    title: $t('system.notice.createUser'),
    key: 'createUserString',
    minWidth: 120,
  },
  {
    title: $t('system.notice.type'),
    key: 'type',
    minWidth: 100,
    render(row) {
      const typeItem = notice_type?.value?.find(
        (item) => String(item.value) === row.type,
      );
      if (!typeItem) return row.type;
      return h(
        NTag,
        { type: (typeItem as any).tagType || 'default', size: 'small' },
        { default: () => typeItem.label },
      );
    },
  },
  {
    title: $t('system.notice.noticeScope'),
    key: 'noticeScope',
    minWidth: 120,
    render(row) {
      const scopeItem = notice_scope_enum?.value?.find(
        (item) => String(item.value) === row.noticeScope,
      );
      if (!scopeItem) return row.noticeScope;
      return h(
        NTag,
        { type: (scopeItem as any).tagType || 'default', size: 'small' },
        { default: () => scopeItem.label },
      );
    },
  },
  {
    title: $t('system.notice.noticeMethods'),
    key: 'noticeMethods',
    minWidth: 150,
    render(row) {
      const methods = row.noticeMethods?.split(',') || [];
      return h(
        'div',
        { class: 'flex flex-wrap items-center gap-1' },
        methods.map((method) => {
          const methodItem = notice_method_enum?.value?.find(
            (item) => String(item.value) === method,
          );
          if (!methodItem) return null;
          return h(
            NTag,
            {
              type: (methodItem as any).tagType || 'default',
              size: 'small',
            },
            { default: () => methodItem.label },
          );
        }),
      );
    },
  },
  {
    title: $t('system.notice.isTiming'),
    key: 'isTiming',
    minWidth: 100,
    render(row) {
      return h(
        NTag,
        {
          type: row.isTiming === 'true' ? 'success' : 'default',
          size: 'small',
        },
        { default: () => (row.isTiming === 'true' ? '是' : '否') },
      );
    },
  },
  {
    title: $t('system.notice.isTop'),
    key: 'isTop',
    minWidth: 100,
    render(row) {
      return h(
        NTag,
        {
          type: row.isTop === 'true' ? 'warning' : 'default',
          size: 'small',
        },
        { default: () => (row.isTop === 'true' ? '是' : '否') },
      );
    },
  },
  {
    title: $t('system.notice.status'),
    key: 'status',
    minWidth: 100,
    render(row) {
      const statusItem = notice_status_enum?.value?.find(
        (item) => Number(item.value) === row.status,
      );
      if (!statusItem) return row.status;
      return h(
        NTag,
        { type: (statusItem as any).tagType || 'default', size: 'small' },
        { default: () => statusItem.label },
      );
    },
  },
  {
    title: $t('system.notice.publishTime'),
    key: 'publishTime',
    minWidth: 160,
  },
  {
    title: $t('common.operation'),
    key: 'action',
    width: 180,
    fixed: 'right',
    render(row) {
      return h('div', { class: 'flex items-center gap-2' }, [
        h(
          Button,
          {
            type: 'button',
            variant: 'link',
            size: 'sm',
            class: 'h-auto px-1',
            onClick: () => handlePreview(row),
          },
          {
            default: () => [
              h(IconifyIcon, { icon: 'lucide:eye', class: 'mr-1 size-3.5' }),
              '预览',
            ],
          },
        ),
        h(
          Button,
          {
            type: 'button',
            variant: 'link',
            size: 'sm',
            class: 'h-auto px-1',
            onClick: () => handleEdit(row),
          },
          {
            default: () => [
              h(IconifyIcon, { icon: 'lucide:pencil', class: 'mr-1 size-3.5' }),
              '编辑',
            ],
          },
        ),
        h(
          Button,
          {
            type: 'button',
            variant: 'link',
            size: 'sm',
            class: 'text-destructive h-auto px-1',
            onClick: () => handleDelete(row),
          },
          {
            default: () => [
              h(IconifyIcon, {
                icon: 'lucide:trash-2',
                class: 'mr-1 size-3.5',
              }),
              '删除',
            ],
          },
        ),
      ]);
    },
  },
];

// ==================== 加载数据 ====================
async function loadTableData() {
  tableLoading.value = true;
  try {
    let publishTime: string | undefined;

    if (searchForm.value.publishTime) {
      const start = new Date(searchForm.value.publishTime[0])
        .toISOString()
        .slice(0, 19)
        .replace('T', ' ');
      const end = new Date(searchForm.value.publishTime[1])
        .toISOString()
        .slice(0, 19)
        .replace('T', ' ');
      publishTime = `${start},${end}`;
    }

    const res = await noticeApi.list({
      page: tablePagination.value.page,
      pageSize: tablePagination.value.pageSize,
      title: searchForm.value.title || undefined,
      type: searchForm.value.type || undefined,
      publishTime,
      status: searchForm.value.status || undefined,
    });

    tableData.value = res.records;
    tablePagination.value.itemCount = res.total;
  } catch (error) {
    console.error('加载公告列表失败:', error);
    message.error('加载数据失败');
  } finally {
    tableLoading.value = false;
  }
}

// ==================== 搜索 ====================
function handleSearch() {
  tablePagination.value.page = 1;
  loadTableData();
}

// ==================== 重置 ====================
function handleReset() {
  searchForm.value = {
    title: '',
    type: null,
    publishTime: null,
    status: null,
  };
  handleSearch();
}

// ==================== 新增 ====================
function handleAdd() {
  currentNoticeId.value = undefined;
  showFormDrawer.value = true;
}

// ==================== 预览 ====================
async function handlePreview(record: NoticeResp) {
  try {
    const detail = await noticeApi.detail(record.id);
    currentNoticeDetail.value = detail;
    showViewDrawer.value = true;
  } catch (error) {
    console.error('加载公告详情失败:', error);
    message.error('加载数据失败');
  }
}

// ==================== 编辑 ====================
function handleEdit(record: NoticeResp) {
  currentNoticeId.value = record.id;
  showFormDrawer.value = true;
}

// ==================== 删除 ====================
async function handleDelete(row: NoticeResp) {
  const ok = await confirmAction.value?.ask({
    title: '删除确认',
    description: `确定删除公告"${row.title}"吗？`,
    tone: 'destructive',
  });
  if (!ok) return;
  try {
    await noticeApi.delete(row.id);
    message.success($t('pages.common.deleteSuccess'));
    await loadTableData();
  } catch (error) {
    console.error('删除公告失败:', error);
    message.error('删除失败');
  }
}

// ==================== 导出 ====================
function handleExport() {
  let publishTime: string | undefined;

  if (searchForm.value.publishTime) {
    const start = new Date(searchForm.value.publishTime[0])
      .toISOString()
      .slice(0, 19)
      .replace('T', ' ');
    const end = new Date(searchForm.value.publishTime[1])
      .toISOString()
      .slice(0, 19)
      .replace('T', ' ');
    publishTime = `${start},${end}`;
  }

  noticeApi.export({
    title: searchForm.value.title || undefined,
    type: searchForm.value.type || undefined,
    publishTime,
    status: searchForm.value.status || undefined,
  });
}

// ==================== 表单提交成功 ====================
function handleFormSuccess() {
  showFormDrawer.value = false;
  loadTableData();
}

// ==================== 初始化 ====================
onMounted(() => {
  loadTableData();
});
</script>

<template>
  <div class="h-full bg-background p-4">
    <ConfirmAction ref="confirmAction" />
    <!-- 搜索和操作栏 -->
    <div class="mb-4">
      <!-- 搜索表单 - 响应式网格布局 -->
      <div
        class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-3 mb-3"
      >
        <NInput
          v-model:value="searchForm.title"
          :placeholder="$t('system.notice.title')"
          clearable
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <NIcon><SearchOutline /></NIcon>
          </template>
        </NInput>
        <FormSelect
          v-model:value="searchForm.type"
          :options="notice_type"
          :placeholder="$t('system.notice.type')"
          clearable
        />
        <FormSelect
          v-model:value="searchForm.status"
          :options="notice_status_enum"
          :placeholder="$t('system.notice.status')"
          clearable
        />
        <div class="sm:col-span-2 lg:col-span-1 xl:col-span-1">
          <NDatePicker
            v-model:value="searchForm.publishTime"
            type="datetimerange"
            clearable
            class="w-full"
            format="yyyy-MM-dd HH:mm:ss"
          />
        </div>
      </div>

      <!-- 操作按钮 -->
      <ToolbarActions>
        <Button type="button" @click="handleSearch">
          <IconifyIcon icon="lucide:search" class="mr-1 size-4" />
          {{ $t('pages.common.search') }}
        </Button>
        <Button type="button" variant="outline" @click="handleReset">
          <IconifyIcon icon="lucide:rotate-ccw" class="mr-1 size-4" />
          {{ $t('pages.common.reset') }}
        </Button>
        <Button
          type="button"
          class="bg-success text-success-foreground hover:bg-success/90"
          @click="handleAdd"
        >
          <IconifyIcon icon="lucide:plus" class="mr-1 size-4" />
          {{ $t('pages.common.add') }}
        </Button>
        <Button type="button" variant="destructive" @click="handleExport">
          <IconifyIcon icon="lucide:download" class="mr-1 size-4" />
          {{ $t('pages.common.export') }}
        </Button>
      </ToolbarActions>
    </div>

    <!-- 数据表格 -->
    <NDataTable
      :columns="tableColumns"
      :data="tableData"
      :loading="tableLoading"
      :row-key="(row) => row.id"
      :pagination="tablePagination"
      scroll-x="1600px"
    />

    <!-- 新增/编辑抽屉 -->
    <NDrawer v-model:show="showFormDrawer" :width="1000" placement="right">
      <NDrawerContent
        :title="currentNoticeId ? $t('common.edit') : $t('common.create')"
        closable
      >
        <NoticeForm
          :notice-id="currentNoticeId"
          @success="handleFormSuccess"
          @cancel="showFormDrawer = false"
        />
      </NDrawerContent>
    </NDrawer>

    <!-- 查看抽屉 -->
    <NDrawer v-model:show="showViewDrawer" :width="900" placement="right">
      <NDrawerContent :title="$t('common.detail')" closable>
        <NoticeView v-if="currentNoticeDetail" :notice="currentNoticeDetail" />
      </NDrawerContent>
    </NDrawer>
  </div>
</template>

<style lang="scss" scoped></style>
