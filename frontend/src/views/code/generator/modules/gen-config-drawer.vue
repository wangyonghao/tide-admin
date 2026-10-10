<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type {
  FieldConfigResp,
  GenConfigResp,
  GeneratorConfigResp,
} from '#/api';
import type { Option } from '#/types/global';

import { computed, ref } from 'vue';

import { useVbenDrawer } from '@vben/common-ui';
import { getPopupContainer } from '@vben/utils';

import { commitFormText, displayFormText } from '#/adapter/component/empty-value';
import FormSelect from '#/adapter/component/FormSelect.vue';

import { Checkbox } from '#/ui/checkbox';
import { Input } from '#/ui/input';

import { useVbenForm } from '#/adapter/form';
import { useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  getGenConfig,
  listDictMenu,
  listFieldConfig,
  listFieldConfigDict,
  saveGenConfig,
} from '#/api/code';
import { useDict } from '#/hooks/app';
import { $t } from '#/locales';

import { useFiledColumns } from '../data';
import { toast } from '#/ui-patterns/toast';

const emits = defineEmits(['success']);

const { form_type_enum, query_type_enum } = useDict(
  'form_type_enum',
  'query_type_enum',
);

// 步骤
const currentTab = ref(0);

// 获取存在的字典，方便筛选
const dictList = ref<Option[]>([]);

const fieldTypeOptions = [
  'String',
  'Integer',
  'Long',
  'Float',
  'Double',
  'Boolean',
  'BigDecimal',
  'LocalDate',
  'LocalTime',
  'LocalDateTime',
].map((item) => ({ label: item, value: item }));

// 生成配置表单
function onFirstSubmit(values: Record<string, any>) {
  if (values) {
    currentTab.value = 1;
  }
}

// 字段列表
const fields = ref<{ label: string; value: string }[]>([]);

// 创建from
const [FirstForm, firstFormApi] = useVbenForm({
  commonConfig: {
    // componentProps: {
    //   class: 'w-full',
    // },
    colon: true,
    formItemClass: 'col-span-2 md:col-span-1',
  },
  handleSubmit: onFirstSubmit,
  layout: 'horizontal',
  resetButtonOptions: {
    show: false,
  },
  schema: [
    {
      component: 'ApiTreeSelect',
      // 对应组件的参数
      componentProps: {
        // 菜单接口
        api: listDictMenu,
        childrenField: 'children',
        // 菜单接口转options格式
        props: {
          label: 'title',
          value: 'key',
        },
        // 是否在点击节点的时候展开或者收缩节点， 默认值为 true，如果为 false，则只有点箭头图标的时候才会展开或者收缩节点。
        expandOnClickNode: false,
        // 是否默认展开所有节点
        defaultExpandAll: true,
        // 是否在点击节点的时候选中节点，默认值为 false，即只有在点击复选框时才会选中节点。
        checkOnClickNode: true,
        // checkOnClickLeaf: false,
        getPopupContainer,
        // 设置弹窗滚动高度 默认256
        listHeight: 300,
        nodeKey: 'key',
        onNodeClick: (data: { key?: unknown }) => {
          firstFormApi.form.setFieldValue('parentMenuId', data.key);
        },
      },
      // 字段名
      fieldName: 'parentMenuId',
      // 界面显示的label
      label: '上级菜单',
    },
    {
      component: 'Input',
      componentProps: {
        placeholder: '请输入',
      },
      fieldName: 'author',
      label: '作者名称',
      rules: 'required',
    },
    {
      component: 'Input',
      componentProps: {
        placeholder: '请输入',
      },
      fieldName: 'entityName',
      label: '实体类名称',
      rules: 'required',
    },
    {
      component: 'RadioGroup',
      componentProps: {
        placeholder: '请输入',
        isButton: true,
        options: [
          { value: 1, label: '表格列表' },
          { value: 2, label: '树状列表' },
        ],
      },
      defaultValue: 1,
      fieldName: 'listType',
      label: '列表类型',
      rules: 'required',
    },
    {
      component: 'Select',
      componentProps: {
        placeholder: '树编码字段',
        maxLength: 60,
        showWordLimit: true,
        // props: {
        //   label: 'comment',
        //   value: 'columnName',
        // },
        options: fields,
      },
      dependencies: {
        if: (values) => values.listType === 2,
        triggerFields: ['listType'],
      },
      fieldName: 'treeId',
      label: '树编码字段',
      rules: 'required',
    },
    {
      component: 'Select',
      componentProps: {
        placeholder: '',
        maxLength: 60,
        showWordLimit: true,
        options: fields,
      },
      dependencies: {
        if: (values) => values.listType === 2,
        triggerFields: ['listType'],
      },
      fieldName: 'treePid',
      label: '树父编码字段',
      rules: 'required',
    },
    {
      component: 'Select',
      componentProps: {
        placeholder: '',
        maxLength: 60,
        showWordLimit: true,
        options: fields,
      },
      dependencies: {
        if: (values) => values.listType === 2,
        triggerFields: ['listType'],
      },
      fieldName: 'treeLabel',
      label: '树名称字段',
      rules: 'required',
    },
    {
      component: 'Input',
      componentProps: {
        placeholder: '项目模块名称，例如：continew-system',
        maxLength: 60,
        showWordLimit: true,
      },
      fieldName: 'moduleName',
      label: '所属模块',
      rules: 'required',
    },
    {
      component: 'Input',
      componentProps: {
        placeholder: '自定义业务名称，例如：用户',
        maxLength: 50,
      },
      fieldName: 'businessName',
      label: '业务名称',
      rules: 'required',
    },
    {
      component: 'Input',
      componentProps: {
        placeholder: '项目模块包名，例如：top.continew.admin.system',
        maxLength: 60,
      },
      fieldName: 'packageName',
      label: '模块包名',
      rules: 'required',
    },
    {
      component: 'Input',
      componentProps: {
        placeholder: '数据库表前缀，例如：sys_',
        maxLength: 20,
      },
      fieldName: 'tablePrefix',
      label: '去表前缀',
      rules: 'required',
    },
    {
      component: 'Input',
      componentProps: {
        placeholder: '请输入',
      },
      fieldName: 'frontPath',
      label: '前端项目路径',
    },
    {
      component: 'RadioGroup',
      componentProps: {
        placeholder: '请输入',
        options: [
          { value: 1, label: 'modal弹窗' },
          { value: 2, label: 'drawer抽屉' },
        ],
      },
      defaultValue: 1,
      fieldName: 'dialogType',
      label: '弹窗组件类型',
      rules: 'required',
    },
    {
      component: 'RadioGroup',
      componentProps: {
        buttonStyle: 'solid',
        options: [
          { label: $t('common.yes'), value: true },
          { label: $t('common.no'), value: false },
        ],
        optionType: 'button',
      },
      defaultValue: true,
      fieldName: 'isOverride',
      label: '是否覆盖',
    },
  ],
  submitButtonOptions: {
    content: '下一步',
  },
  wrapperClass: 'grid-cols-1 md:grid-cols-1 lg:grid-cols-1',
});

const genTable = ref<GenConfigResp>();

const [Grid, gridApi] = useVbenVxeGrid({
  gridOptions: {
    columns: useFiledColumns(),
    border: true,
    height: 'auto',
    keepSource: true,
    columnConfig: {
      resizable: true,
    },
    pagerConfig: {
      enabled: false,
    },
    proxyConfig: {
      response: {
        list: 'records',
      },
      autoLoad: true,
      ajax: {
        query: async () => {
          const res = await listFieldConfig(
            genTable.value?.tableName ?? '',
            false,
          );
          fields.value = res.map((item) => ({
            label: `${item.fieldName}  ${item.comment ? `---  ${item.comment}` : ''}`,
            value: item.fieldName,
          }));
          return { list: res, total: res.length };
        },
      },
    },
    rowConfig: {
      keyField: 'columnName',
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
  } as VxeTableGridOptions<FieldConfigResp>,
});

const [Drawer, drawerApi] = useVbenDrawer({
  async onConfirm() {
    const { valid } = await firstFormApi.validate();
    if (!valid) return false;
    drawerApi.lock();
    try {
      await saveGenConfig(genTable.value?.tableName, {
        genConfig: firstFormApi.form.values,
        fieldConfigs: gridApi.grid.getFullData(),
      } as GeneratorConfigResp);
      toast.success('保存成功');
      emits('success');
      drawerApi.close();
      return true;
    } finally {
      drawerApi.unlock();
    }
  },
  async onOpenChange(isOpen) {
    if (isOpen) {
      const data = drawerApi.getData<GenConfigResp>();
      if (data) {
        genTable.value = data;
        // 查询生成配置
        const genData = await getGenConfig(genTable.value?.tableName);
        firstFormApi.form.setValues(genData);
      }
      // 获取字典列表
      dictList.value = await listFieldConfigDict();
    }
  },
});

const configSteps = ['生成配置', '字段配置'];

function onFieldText(
  row: { comment?: null | string; fieldName?: null | string },
  field: 'comment' | 'fieldName',
  value: number | string,
) {
  row[field] = commitFormText(value);
}

const getDrawerTitle = computed(() => {
  let comment = '';
  if (genTable.value?.comment) {
    comment = `(${genTable.value?.comment})`;
  }
  return `${genTable.value?.tableName}${comment}${$t('common.config')} `;
});
</script>
<template>
  <Drawer :title="getDrawerTitle" class="w-[80%]">
    <div class="mx-auto flex h-full w-full flex-col">
      <ol class="flex items-center justify-center gap-10 px-16">
        <li
          v-for="(title, index) in configSteps"
          :key="title"
          class="flex items-center gap-2 text-sm"
          :class="
            index === currentTab
              ? 'text-primary font-medium'
              : index < currentTab
                ? 'text-foreground'
                : 'text-muted-foreground'
          "
        >
          <span
            class="flex size-6 items-center justify-center rounded-full border text-xs"
            :class="
              index === currentTab
                ? 'border-primary bg-primary text-primary-foreground'
                : index < currentTab
                  ? 'border-primary text-primary'
                  : 'border-border'
            "
          >
            {{ index + 1 }}
          </span>
          {{ title }}
        </li>
      </ol>
      <div class="w-full flex-1 p-6">
        <FirstForm v-show="currentTab === 0" />
        <Grid v-show="currentTab === 1">
          <template #toolbar-left>
            <!-- <a-popconfirm
              content="是否确定同步最新数据表结构？同步后只要不点击确定保存，则不影响原有配置数据。"
              type="warning"
              @ok="handleRefresh(form.tableName)"
            >
              <a-tooltip content="同步最新数据表结构">
                <a-button
                  type="primary"
                  status="success"
                  size="small"
                  title="同步"
                  :disabled="
                    dataList.length > 0 && dataList[0].createTime == null
                  "
                >
                  <template #icon><icon-sync /></template>同步
</a-button>
</a-tooltip>
</a-popconfirm> -->
          </template>
          <template #fieldName="{ row }">
            <Input
              class="h-8"
              :model-value="displayFormText(row.fieldName)"
              @update:model-value="onFieldText(row, 'fieldName', $event)"
            />
          </template>
          <template #fieldType="{ row }">
            <FormSelect
              v-model:value="row.fieldType"
              placeholder="请选择字段类型"
              :options="fieldTypeOptions"
              clearable
              filterable
              tag
            />
          </template>
          <template #comment="{ row }">
            <Input
              class="h-8"
              :model-value="displayFormText(row.comment)"
              @update:model-value="onFieldText(row, 'comment', $event)"
            />
          </template>
          <template #showInList="{ row }">
            <Checkbox v-model="row.showInList" />
          </template>
          <template #showInForm="{ row }">
            <Checkbox v-model="row.showInForm" />
          </template>
          <template #isRequired="{ row }">
            <Checkbox v-if="row.showInForm" v-model="row.isRequired" />
            <Checkbox v-else disabled />
          </template>
          <template #showInQuery="{ row }">
            <Checkbox v-model="row.showInQuery" />
          </template>
          <template #formType="{ row }">
            <FormSelect
              v-if="row.showInForm || row.showInQuery"
              v-model:value="row.formType"
              placeholder="请选择表单类型"
              :options="form_type_enum"
              clearable
            />
            <span v-else>无需设置</span>
          </template>
          <template #queryType="{ row }">
            <FormSelect
              v-if="row.showInQuery"
              v-model:value="row.queryType"
              placeholder="请选择查询方式"
              :options="query_type_enum"
              clearable
            />
            <span v-else>无需设置</span>
          </template>
          <template #dictCode="{ row }">
            <FormSelect
              v-model:value="row.dictCode"
              placeholder="请选择字典类型"
              :options="dictList"
              clearable
              filterable
            />
          </template>
        </Grid>
      </div>
    </div>
  </Drawer>
</template>
<style lang="css" scoped></style>
