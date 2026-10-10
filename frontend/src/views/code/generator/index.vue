<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { GenConfigResp } from '#/api';

import { Page, useVbenDrawer, useVbenModal } from '@vben/common-ui';

import { useVbenVxeGrid } from '#/adapter/vxe-table';
import { listGenConfig } from '#/api';
import { $t } from '#/locales';
import { Button } from '#/ui/button';
import { ToolbarActions } from '#/ui-patterns/toolbar-actions';

import { useGenConfigColumns, useGridFormSchema } from './data';
import GenConfigDrawer from './modules/gen-config-drawer.vue';
import GenPreviewModal from './modules/gen-preview-modal.vue';

const [FormDrawer, formDrawerApi] = useVbenDrawer({
  connectedComponent: GenConfigDrawer,
  destroyOnClose: true,
});

const [Grid, gridApi] = useVbenVxeGrid({
  formOptions: {
    fieldMappingTime: [['createTime', ['startTime', 'endTime']]],
    schema: useGridFormSchema(),
    submitOnChange: true,
    showCollapseButton: false,
    wrapperClass: 'grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4',
  },
  gridOptions: {
    columns: useGenConfigColumns(),
    border: true,
    height: 'auto',
    keepSource: true,
    columnConfig: {
      resizable: true,
    },
    proxyConfig: {
      response: {
        list: 'records',
      },
      ajax: {
        query: async ({ page }, formValues) => {
          const res = await listGenConfig({
            page: page.currentPage,
            pageSize: page.pageSize,
            ...formValues,
          });
          return res;
        },
      },
    },
    rowConfig: {
      keyField: 'id',
      isHover: true,
    },
    checkboxConfig: {
      highlight: true,
    },
    toolbarConfig: {
      custom: true,
      export: false,
      refresh: { code: 'query' },
      search: true,
      zoom: true,
    },
  } as VxeTableGridOptions<GenConfigResp>,
});

function openConfig(row: GenConfigResp) {
  formDrawerApi.setData(row).open();
}

const [FormModal, formModalApi] = useVbenModal({
  connectedComponent: GenPreviewModal,
  destroyOnClose: true,
});
// 预览
const onPreview = (tableNames: Array<string>) => {
  formModalApi.setData(tableNames).open();
};

/**
 * 刷新表格
 */
function refreshGrid() {
  gridApi.query();
}
</script>
<template>
  <Page auto-content-height>
    <FormDrawer @success="refreshGrid" />
    <FormModal />
    <Grid :table-title="$t('system.code.list')">
      <template #toolbar-tools> </template>
      <template #action="{ row }">
        <ToolbarActions>
          <Button
            type="button"
            v-access:code="['code:generator:config']"
            @click="openConfig(row)"
          >
            配置
          </Button>
          <Button
            type="button"
            variant="outline"
            :disabled="!row.author"
            v-access:code="['code:generator:preview']"
            @click="onPreview([row.tableName])"
          >
            生成
          </Button>
        </ToolbarActions>
      </template>
    </Grid>
  </Page>
</template>
