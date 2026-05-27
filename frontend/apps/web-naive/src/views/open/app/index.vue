<script lang="ts" setup>
import type {
  OnActionClickParams,
  VxeTableGridOptions,
} from '#/adapter/vxe-table';
import type { OpenAppApi } from '#/api';

import { Page, useVbenDrawer } from '@vben/common-ui';
import { $t } from '@vben/locales';

import { NButton, useDialog, useMessage } from 'naive-ui';

import { useVbenVxeGrid } from '#/adapter/vxe-table';
import { openAppApi } from '#/api/open';

import { useColumns, useGridFormSchema } from './data';
import AppDetail from './modules/detail.vue';
import AppForm from './modules/form.vue';

defineOptions({ name: 'OpenApp' });
const message = useMessage();
const dialog = useDialog();

const [FormDrawer, formDrawerApi] = useVbenDrawer({
  connectedComponent: AppForm,
  destroyOnClose: true,
});

const [DetailDrawer, detailDrawerApi] = useVbenDrawer({
  connectedComponent: AppDetail,
  destroyOnClose: true,
});

const [Grid, gridApi] = useVbenVxeGrid({
  formOptions: {
    schema: useGridFormSchema(),
  },
  gridOptions: {
    columns: useColumns(onActionClick),
    height: 'auto',
    keepSource: true,
    proxyConfig: {
      ajax: {
        query: async ({ page }, formValues) => {
          return await openAppApi.list({
            current: page.currentPage,
            size: page.pageSize,
            ...formValues,
          });
        },
      },
    },
    rowConfig: {
      keyField: 'id',
    },
    toolbarConfig: {
      custom: true,
      export: false,
      refresh: { code: 'query' },
      search: true,
      zoom: true,
    },
  } as VxeTableGridOptions<OpenAppApi.AppResp>,
});

function onActionClick(e: OnActionClickParams<OpenAppApi.AppResp>) {
  switch (e.code) {
    case 'delete': {
      onDelete(e.row);
      break;
    }
    case 'detail': {
      onDetail(e.row);
      break;
    }
    case 'edit': {
      onEdit(e.row);
      break;
    }
    case 'resetSecret': {
      onResetSecret(e.row);
      break;
    }
  }
}

/**
 * 将Naive UI的dialog.warning封装为promise，方便在异步函数中调用。
 * @param content 提示内容
 * @param title 提示标题
 */
function confirm(content: string, title: string) {
  return new Promise((resolve, reject) => {
    dialog.warning({
      title,
      content,
      positiveText: '确定',
      negativeText: '取消',
      onPositiveClick: () => resolve(true),
      onNegativeClick: () => reject(new Error('cancel')),
    });
  });
}

function onEdit(row: OpenAppApi.AppResp) {
  formDrawerApi.setData(row).open();
}

function onDetail(row: OpenAppApi.AppResp) {
  detailDrawerApi.setData(row).open();
}

function onDelete(row: OpenAppApi.AppResp) {
  openAppApi.delete(row.id)
    .then(() => {
      message.success(`删除应用 "${row.name}" 成功`);
      onRefresh();
    })
    .catch(() => {
      message.error(`删除应用 "${row.name}" 失败`);
    });
}

async function onShowSecret(row: OpenAppApi.AppResp) {
  try {
    const { secretKey } = await openAppApi.getSecretKey(row.id);
    row.secretKey = secretKey;
  } catch {
    message.error('获取密钥失败');
  }
}

function onHideSecret(row: OpenAppApi.AppResp) {
  row.secretKey = undefined;
}

async function onResetSecret(row: OpenAppApi.AppResp) {
  try {
    await confirm(
      `确定要重置应用 "${row.name}" 的密钥吗？重置后原密钥将失效。`,
      '确认重置密钥',
    );
    await openAppApi.resetSecretKey(row.id);
    message.success('密钥重置成功');
    gridApi.query();
  } catch (error: any) {
    if (error !== 'cancel') {
      message.error('密钥重置失败');
    }
  }
}

/**
 * 复制密钥到剪贴板
 * @param secretKey 要复制的密钥
 */
async function onCopySecret(secretKey: string) {
  try {
    await navigator.clipboard.writeText(secretKey);
    message.success('密钥已复制到剪贴板');
  } catch {
    // 降级方案：使用传统的复制方法
    const textArea = document.createElement('textarea');
    textArea.value = secretKey;
    textArea.style.position = 'fixed';
    textArea.style.opacity = '0';
    document.body.append(textArea);
    textArea.focus();
    textArea.select();
    try {
      document.execCommand('copy');
      message.success('密钥已复制到剪贴板');
    } catch {
      message.error('复制失败，请手动复制');
    } finally {
      textArea.remove();
    }
  }
}

function onRefresh() {
  gridApi.query();
}

function onCreate() {
  formDrawerApi.setData({}).open();
}

// 导出
async function onExport() {
  try {
    const blob = await openAppApi.export(gridApi.formApi.form.values);
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = '应用列表.xlsx';
    link.click();
    window.URL.revokeObjectURL(url);
    message.success('导出成功');
  } catch {
    message.error('导出失败');
  }
}
</script>

<template>
  <Page auto-content-height>
    <FormDrawer @success="onRefresh" />
    <DetailDrawer />
    <Grid :table-title="$t('open.app.listTitle')">
      <template #toolbar-tools>
        <NButton type="primary" @click="onCreate">
          {{ $t('pages.common.add') }}
        </NButton>
        <NButton @click="onExport">
          {{ $t('pages.common.export') }}
        </NButton>
      </template>
      <template #secretKey="{ row }">
        <div class="flex items-center justify-center gap-2">
          <span v-if="row.secretKey" class="font-mono">
            {{ row.secretKey }}
          </span>
          <span v-else class="text-gray-400">***********</span>

          <div class="flex gap-1">
            <!-- 复制按钮 -->
            <NButton
              v-if="row.secretKey"
              text
              type="primary"
              size="small"
              @click="onCopySecret(row.secretKey)"
            >
              {{ $t('open.app.copy') }}
            </NButton>

            <!-- 显示/隐藏按钮 -->
            <NButton
              v-if="row.secretKey"
              text
              type="warning"
              size="small"
              @click="onHideSecret(row)"
            >
              {{ $t('open.app.hide') }}
            </NButton>
            <NButton
              v-else
              text
              type="primary"
              size="small"
              @click="onShowSecret(row)"
            >
              {{ $t('open.app.show') }}
            </NButton>
          </div>
        </div>
      </template>
    </Grid>
  </Page>
</template>
