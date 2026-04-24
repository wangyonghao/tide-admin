<script setup lang="ts">
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { NoticeQuery, NoticeResp } from '#/api/system/notice';

import { useRouter } from 'vue-router';

import { Page } from '@vben/common-ui';
import { IconifyIcon } from '@vben/icons';
import { $t } from '@vben/locales';

import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  VbenButton,
} from '@vben-core/shadcn-ui';

import { useMessage } from 'naive-ui';

import { useVbenVxeGrid } from '#/adapter/vxe-table';
import { deleteNotice, exportNotice, listNotice } from '#/api/system/notice';
import { useDict } from '#/hooks';
import { useDownload } from '#/hooks/app/useDownload';

import {
  useNoticeGridFieldColumns,
  useNoticeGridSearchFormSchema,
} from './NoticeData';

const router = useRouter();
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

const [TableGrid, tableGridApi] = useVbenVxeGrid({
  formOptions: {
    schema: useNoticeGridSearchFormSchema(notice_type),
    submitOnChange: true,
    showCollapseButton: false,
    wrapperClass: 'grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4',
  },
  gridOptions: {
    columns: useNoticeGridFieldColumns(
      notice_type,
      notice_scope_enum,
      notice_method_enum,
      notice_status_enum,
    ),
    border: true,
    height: 'auto',
    keepSource: true,
    columnConfig: {
      resizable: true,
    },
    proxyConfig: {
      response: {
        list: 'list',
      },
      ajax: {
        query: async ({ page }, formValues) => {
          const res = await listNotice({
            page: page.currentPage,
            size: page.pageSize,
            ...formValues,
          });
          return res;
        },
        querySuccess: ({ $grid }) => {
          $grid?.setAllTreeExpand(true);
        },
      },
    },
    rowConfig: {
      keyField: 'id',
      isHover: true,
      isCurrent: true,
    },

    toolbarConfig: {
      custom: true,
      export: false,
      refresh: true,
      refreshOptions: {
        code: 'query',
      },
      search: true,
      zoom: true,
      zoomOptions: {},
    },
  } as VxeTableGridOptions<NoticeResp>,
});

// 预览
const handlePreview = (record: NoticeResp) => {
  router.push({ path: '/system/notice/view', query: { id: record.id } });
};

// 编辑
const handleEdit = (record: NoticeResp) => {
  router.push({
    path: '/system/notice/add',
    query: { id: record.id },
  });
};

// 新增
const handleAdd = () => {
  router.push({ name: 'SystemNoticeAdd' });
};

const deleteDialogVisible = ref(false);
const deleteRow = ref<NoticeResp | null>(null);
const message = useMessage();

const showDeleteDialog = (row: NoticeResp) => {
  deleteRow.value = row;
  deleteDialogVisible.value = true;
};

const handleDelete = async () => {
  if (!deleteRow.value) return;

  try {
    await deleteNotice(deleteRow.value.id);
    message.success($t('pages.common.deleteSuccess'));
    await tableGridApi.query();
    deleteDialogVisible.value = false;
    deleteRow.value = null;
    return true;
  } catch {
    return false;
  }
};

const handleExport = () => {
  useDownload(async () =>
    exportNotice(await tableGridApi.formApi.getValues<NoticeQuery>()),
  );
};
</script>

<template>
  <Page auto-content-height>
    <TableGrid :table-title="$t('system.notice.listTitle')">
      <template #toolbar-tools>
        <div class="flex items-center gap-2">
          <span v-access:code="['system:notice:create']">
            <VbenButton @click="handleAdd">
              {{ $t('pages.common.add') }}
            </VbenButton>
          </span>
          <span v-access:code="['system:notice:export']">
            <VbenButton variant="destructive" @click="handleExport">
              {{ $t('pages.common.export') }}
            </VbenButton>
          </span>
        </div>
      </template>
      <template #action="{ row }">
        <div class="flex items-center gap-2">
          <span v-access:code="['system:notice:view']">
            <VbenButton variant="ghost" size="icon" @click="handlePreview(row)">
              <IconifyIcon icon="lucide:eye" class="w-4 h-4" />
            </VbenButton>
          </span>
          <span v-access:code="['system:notice:update']">
            <VbenButton variant="ghost" size="icon" @click="handleEdit(row)">
              <IconifyIcon icon="lucide:pencil" class="w-4 h-4" />
            </VbenButton>
          </span>
          <span v-access:code="['system:notice:delete']">
            <VbenButton
              variant="ghost"
              size="icon"
              @click="showDeleteDialog(row)"
            >
              <IconifyIcon
                icon="lucide:trash-2"
                class="w-4 h-4 text-destructive"
              />
            </VbenButton>
          </span>
        </div>
      </template>
    </TableGrid>

    <!-- Delete Confirmation Dialog -->
    <Dialog
      :open="deleteDialogVisible"
      @update:open="(val) => (deleteDialogVisible = val)"
    >
      <DialogContent class="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle>{{ $t('ui.actionMessage.deleteTitle') }}</DialogTitle>
          <DialogDescription>
            {{ $t('ui.actionMessage.deleteConfirm', [deleteRow?.title]) }}
          </DialogDescription>
        </DialogHeader>
        <DialogFooter>
          <VbenButton variant="outline" @click="deleteDialogVisible = false">
            {{ $t('common.cancel') }}
          </VbenButton>
          <VbenButton variant="destructive" @click="handleDelete">
            {{ $t('common.confirm') }}
          </VbenButton>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  </Page>
</template>
<style lang="scss" scoped></style>
