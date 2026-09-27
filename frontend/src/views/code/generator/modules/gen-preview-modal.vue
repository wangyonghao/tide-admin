<script setup lang="ts">
import type { TreeSelectOption } from 'naive-ui';

import type { GeneratePreviewResp } from '#/api/code';

import { computed, ref, watch } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { useClipboard } from '@vueuse/core';
import {
  NCard,
  NIcon,
  NScrollbar,
  NTree,
  useMessage,
} from 'naive-ui';

import { downloadCode, generateCode, genPreview } from '#/api/code';
import { CnCodeView } from '#/components/code-view';

const message = useMessage();
const { copy, copied } = useClipboard();

const genPreviewList = ref<GeneratePreviewResp[]>([]);
const currentPreview = ref<GeneratePreviewResp>();
const visible = ref(false);
const previewTableNames = ref<string[]>([]);

const treeData = ref<[]>([]);
// 合并目录
const mergeDir = (parent: TreeSelectOption) => {
  // 合并目录
  if (
    parent.children?.length === 1 &&
    typeof parent.children[0].key === 'number'
  ) {
    const mergeTitle = mergeDir(parent.children[0]);
    if (mergeTitle !== '') {
      parent.title = `${parent.title}/${mergeTitle}`;
    }
    parent.children = parent.children[0].children;
    return parent.title;
  }
  // 合并子目录
  if (parent?.children) {
    for (const child of parent.children) {
      mergeDir(child);
    }
  }
  return parent.title;
};

const pushDir = (
  children: TreeSelectOption[] | undefined,
  treeNode: TreeSelectOption,
) => {
  if (children) {
    for (const child of children) {
      if (child.title === treeNode.title) {
        return child.children;
      }
    }
  }
  children?.push(treeNode);
  return treeNode.children;
};

// 自增的一个key 因为key相同的节点会出现一些问题
let autoIncrementKey = 0;
// 将生成的目录组装成树结构
const assembleTree = (genPreview: GeneratePreviewResp) => {
  const separator = genPreview.path.includes('/') ? '/' : '\\';
  const paths: string[] = genPreview.path.split(separator);
  let tempChildren: TreeSelectOption[] | undefined = treeData.value;
  for (const path of paths) {
    autoIncrementKey++;
    // 向treeData中推送目录,如果该级目录有那么不推送进行下级的合并
    tempChildren = pushDir(tempChildren, {
      title: path,
      key: `${autoIncrementKey}-0`,
      children: new Array<TreeSelectOption>(),
    });
  }
  tempChildren?.push({
    title: genPreview.fileName,
    key: genPreview.fileName,
    children: new Array<TreeSelectOption>(),
  });
};

// 下载
const onDownload = async () => {
  const tableNames = previewTableNames.value;
  const res: any = await downloadCode(tableNames);
  const contentDisposition = res.headers['content-disposition'];
  const pattern = /filename=([^;]+\.[^.;]+);*/;
  const result = pattern.exec(contentDisposition) || '';
  // 对名字进行解码
  const fileName = window.decodeURI(result[1] ?? '');
  // 创建下载的链接
  const blob = new Blob([res.data]);
  const downloadElement = document.createElement('a');
  const href = window.URL.createObjectURL(blob);
  downloadElement.style.display = 'none';
  downloadElement.href = href;
  // 下载后文件名
  downloadElement.download = fileName;
  document.body.append(downloadElement);
  // 点击下载
  downloadElement.click();
  // 下载完成，移除元素
  downloadElement.remove();
  // 释放掉 blob 对象
  window.URL.revokeObjectURL(href);
};

// 生成
const onGenerator = async () => {
  const tableNames = previewTableNames.value;
  await generateCode(tableNames);
  message.success('代码生成成功');
};

// 校验文件类型
const checkFileType = (title: string, type: string) => {
  return title.endsWith(type);
};

// 复制
const onCopy = () => {
  if (currentPreview.value) {
    copy(currentPreview.value?.content);
  }
};
watch(copied, () => {
  if (copied.value) {
    message.success('复制成功');
  }
});

const selectedKeys = ref();
// 选择文件预览
const onSelectPreview = (data: TreeSelectOption) => {
  currentPreview.value = genPreviewList.value.find(
    (p) => p.fileName === data.key,
  );
  selectedKeys.value = [data.key];
};

// 打开
const onOpen = async (tableNames: Array<string>) => {
  treeData.value = [];
  previewTableNames.value = tableNames;
  const data = await genPreview(tableNames);
  genPreviewList.value = data;
  for (const genPreview of genPreviewList.value) {
    assembleTree(genPreview);
  }
  for (const valueElement of treeData.value) {
    mergeDir(valueElement);
  }
  selectedKeys.value = [genPreviewList.value[0]?.fileName];
  currentPreview.value = genPreviewList.value[0];
  visible.value = true;
};

const [Modal, modalApi] = useVbenModal({
  centered: true,
  showCancelButton: false,
  showConfirmButton: false,
  onOpenChange(isOpen) {
    if (isOpen) {
      const data = modalApi.getData<string[]>();
      if (data) {
        onOpen(data);
      }
    }
  },
});
const getTitle = computed(() => {
  return previewTableNames.value?.join(',');
});

const treeProps = {
  value: 'key',
  label: 'title',
  children: 'children',
};

// 获取所有节点key
const allNodeKeys = computed(() => {
  // 递归函数，收集所有节点的 key
  const allNodeKeys: any[] = [];
  const getAllNodeKeys = (nodes: TreeSelectOption[]) => {
    nodes.forEach((node) => {
      allNodeKeys.push(node.key);
      if (node.children && node.children.length > 0) {
        getAllNodeKeys(node.children);
      }
    });
  };
  getAllNodeKeys(treeData.value);
  return allNodeKeys;
});
</script>

<template>
  <Modal class="h-[90%] w-[90%]" :title="getTitle">
    <template #title>
      <div style="display: flex; align-items: end; justify-content: center">
        {{
          previewTableNames.length === 1
            ? `生成 ${previewTableNames[0]} 表预览`
            : '批量生成预览'
        }}
        <a
          v-access:code="['code:generator:generate']"
          style="margin-left: 10px; color: var(--n-text-color); cursor: pointer"
          @click="onDownload"
        >
          下载源码
        </a>
        <a
          v-access:code="['code:generator:generate']"
          style="margin-left: 10px; color: var(--n-text-color); cursor: pointer"
          @click="onGenerator"
        >
          生成源码
        </a>
      </div>
    </template>
    <div class="preview-content">
          <div
            :style="{ minWidth: '250px', height: '100%' }"
            class="border-border bg-card mr-2 rounded-[var(--radius)] border p-2"
          >
                <NTree
                  v-if="treeData.length > 0"
                  :data="treeData"
                  :props="treeProps"
                  :selected-keys="selectedKeys"
                  :height="height"
                  :default-expanded-keys="allNodeKeys"
                  block-line
                  @update:selected-keys="(keys) => {
                    selectedKeys = keys;
                    if (keys.length > 0) {
                      const selectedNode = treeData.find(n => n.key === keys[0]);
                      if (selectedNode) {
                        onSelectPreview(selectedNode);
                      }
                    }
                  }"
                >
                  <template #default="{ option }">
                    <div style="display: flex; gap: 8px; align-items: center">
                      <NIcon
                        class="node-icon"
                        :class="{ 'is-leaf': !option.children?.length }"
                      >
                        <SvgDirectoryBlueIcon v-if="option.children?.length" />
                        <SvgFileJavaIcon
                          v-if="!option.children?.length && checkFileType(option.title, '.java')"
                        />
                        <SvgFileVueIcon
                          v-if="!option.children?.length && checkFileType(option.title, '.vue')"
                          :size="16"
                        />
                        <SvgFileTypescriptIcon
                          v-if="!option.children?.length && checkFileType(option.title, '.ts')"
                          :size="16"
                          name="file-typescript"
                        />
                        <SvgFileJavascriptIcon
                          v-if="!option.children?.length && checkFileType(option.title, '.js')"
                          :size="16"
                          name="file-javascript"
                        />
                        <SvgFileJsonIcon
                          v-if="!option.children?.length && checkFileType(option.title, '.json')"
                        />
                        <SvgFileMavenIcon
                          v-if="
                            !option.children?.length && checkFileType(option.title, 'pom.xml')
                          "
                        />
                        <SvgFileXmlIcon
                          v-if="
                            !option.children?.length &&
                            checkFileType(option.title, '.xml') &&
                            !checkFileType(option.title, 'pom.xml')
                          "
                        />
                        <SvgFileSqlIcon
                          v-if="!option.children?.length && checkFileType(option.title, '.sql')"
                        />
                      </NIcon>
                      <span>{{ option.title }}</span>
                    </div>
                  </template>
                </NTree>
          </div>
          <NCard style="height: 100%">
            <template #header>
              <div class="card-header">
                <span>
                  {{ currentPreview?.path }}
                  {{ currentPreview?.path.indexOf('/') !== -1 ? '/' : '\\' }}
                  {{ currentPreview?.fileName }}
                </span>
              </div>
            </template>
            <NScrollbar :style="{ height: `${height}px` }">
                  <div style="position: relative; padding: 20px">
                    <a
                      style="position: absolute; top: 10px; right: 20px; z-index: 999; display: flex; gap: 4px; align-items: center; cursor: pointer"
                      title="复制"
                      @click="onCopy"
                    >
                      <SvgCopyIcon />
                      <span>复制</span>
                    </a>
                    <CnCodeView
                      v-if="currentPreview"
                      :type="
                        'vue' === currentPreview?.fileName.split('.')[1]
                          ? 'vue'
                          : 'javascript'
                      "
                      :code-json="currentPreview!.content"
                    />
                  </div>
            </NScrollbar>
          </NCard>
    </div>
  </Modal>
</template>

<style scoped lang="scss">
.preview-content {
  height: 100%;
}

.preview-content :deep(.grid-content) {
  min-width: 200px;
  height: 100%;
  white-space: nowrap;
}

.preview-content :deep(.cep-bg-purple) {
  min-width: 200px;
  height: 100%;
  white-space: nowrap;
}
</style>
