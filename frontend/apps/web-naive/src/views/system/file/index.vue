<script setup lang="ts">
import { computed, h, onMounted, ref, watch } from 'vue';
import { IconifyIcon } from '@vben/icons';
import { Page } from '@vben/common-ui';
import {
  NButton,
  NCard,
  NCheckbox,
  NDropdown,
  NIcon,
  NInput,
  NModal,
  NPagination,
  NProgress,
  NSpace,
  NSpin,
  NSplit,
  NTag,
  NUpload,
  useMessage,
  type UploadFileInfo,
} from 'naive-ui';
import { SearchOutline } from '@vicons/ionicons5';
import {
  type FileQuery,
  type FileResult,
  FileType,
  fileApi,
} from '#/api/system/file';

const message = useMessage();

// ==================== 文件类型过滤 ====================
const fileTypes = [
  { key: 'all', label: '全部', icon: 'lucide:folder', value: undefined },
  { key: 'image', label: '图片', icon: 'lucide:image', value: FileType.IMAGE },
  { key: 'video', label: '视频', icon: 'lucide:video', value: FileType.VIDEO },
  { key: 'audio', label: '音频', icon: 'lucide:music', value: FileType.AUDIO },
  { key: 'document', label: '文档', icon: 'lucide:file-text', value: FileType.DOCUMENT },
  { key: 'other', label: '其他', icon: 'lucide:file', value: FileType.OTHER },
];

const selectedFileType = ref<FileType | undefined>(undefined);
const searchKeyword = ref('');

// ==================== 视图模式 ====================
const viewMode = ref<'grid' | 'list'>('grid');

// ==================== 存储空间 ====================
const storageInfo = ref({
  used: 2.5, // GB
  total: 10, // GB
});

const storagePercent = computed(() => {
  return (storageInfo.value.used / storageInfo.value.total) * 100;
});

// ==================== 文件数据 ====================
interface FileItem extends FileResult {
  starred?: boolean;
  selected?: boolean;
}

const currentPath = ref<string>('/');
const fileList = ref<FileItem[]>([]);
const loading = ref(false);
const total = ref(0);
const pageNum = ref(1);
const pageSize = ref(20);

// 加载文件列表
async function loadFileList() {
  try {
    loading.value = true;
    const query: FileQuery = {
      parentPath: currentPath.value,
      type: selectedFileType.value,
      fileName: searchKeyword.value || undefined,
      page: pageNum.value,
      size: pageSize.value,
    };
    
    const response = await fileApi.list(query);
    fileList.value = response.list.map(file => ({
      ...file,
      starred: false,
      selected: false,
    }));
    total.value = response.total;
  } catch (error) {
    message.error('加载文件列表失败');
    console.error(error);
  } finally {
    loading.value = false;
  }
}

// 监听筛选条件变化
watch([selectedFileType, searchKeyword, currentPath], () => {
  pageNum.value = 1;
  loadFileList();
});

// 监听分页变化
watch([pageNum, pageSize], () => {
  loadFileList();
});

// ==================== 文件图标 ====================
function getFileIcon(item: FileItem) {
  const ext = item.extension?.toLowerCase();
  const iconMap: Record<string, string> = {
    pdf: 'lucide:file-text',
    doc: 'lucide:file-text',
    docx: 'lucide:file-text',
    xls: 'lucide:file-spreadsheet',
    xlsx: 'lucide:file-spreadsheet',
    ppt: 'lucide:presentation',
    pptx: 'lucide:presentation',
    jpg: 'lucide:image',
    jpeg: 'lucide:image',
    png: 'lucide:image',
    gif: 'lucide:image',
    mp4: 'lucide:video',
    avi: 'lucide:video',
    mp3: 'lucide:music',
    wav: 'lucide:music',
    zip: 'lucide:file-archive',
    rar: 'lucide:file-archive',
  };
  
  return iconMap[ext || ''] || 'lucide:file';
}

// ==================== 文件大小格式化 ====================
function formatFileSize(bytes?: number) {
  if (!bytes) return '-';
  if (bytes < 1024) return bytes + ' B';
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(2) + ' KB';
  if (bytes < 1024 * 1024 * 1024) return (bytes / (1024 * 1024)).toFixed(2) + ' MB';
  return (bytes / (1024 * 1024 * 1024)).toFixed(2) + ' GB';
}

// ==================== 文件选择 ====================
const selectedFiles = computed(() => fileList.value.filter(f => f.selected));

function toggleFileSelection(file: FileItem) {
  file.selected = !file.selected;
}

function selectAll() {
  const allSelected = fileList.value.every(f => f.selected);
  fileList.value.forEach(f => f.selected = !allSelected);
}

function clearSelection() {
  fileList.value.forEach(f => f.selected = false);
}

// ==================== 文件操作 ====================
const contextMenuOptions = (file: FileItem) => [
  {
    label: '下载',
    key: 'download',
    icon: () => h(IconifyIcon, { icon: 'lucide:download' }),
  },
  {
    label: '分享',
    key: 'share',
    icon: () => h(IconifyIcon, { icon: 'lucide:share-2' }),
  },
  {
    label: '重命名',
    key: 'rename',
    icon: () => h(IconifyIcon, { icon: 'lucide:pencil' }),
  },
  {
    type: 'divider',
    key: 'd1',
  },
  {
    label: '复制',
    key: 'copy',
    icon: () => h(IconifyIcon, { icon: 'lucide:copy' }),
  },
  {
    label: '剪切',
    key: 'cut',
    icon: () => h(IconifyIcon, { icon: 'lucide:scissors' }),
  },
  {
    type: 'divider',
    key: 'd2',
  },
  {
    label: file.starred ? '取消星标' : '添加星标',
    key: 'star',
    icon: () => h(IconifyIcon, { icon: file.starred ? 'lucide:star-off' : 'lucide:star' }),
  },
  {
    type: 'divider',
    key: 'd3',
  },
  {
    label: '删除',
    key: 'delete',
    icon: () => h(IconifyIcon, { icon: 'lucide:trash-2', class: 'text-red-500' }),
  },
];

function handleContextMenu(key: string, file: FileItem) {
  switch (key) {
    case 'download':
      fileApi.download(file.id, file.originalName);
      break;
    case 'share':
      message.info(`分享: ${file.originalName}`);
      break;
    case 'rename':
      handleRename(file);
      break;
    case 'copy':
      message.info(`复制: ${file.originalName}`);
      break;
    case 'cut':
      message.info(`剪切: ${file.originalName}`);
      break;
    case 'star':
      file.starred = !file.starred;
      message.success(file.starred ? '已添加星标' : '已取消星标');
      break;
    case 'delete':
      handleDelete(file);
      break;
  }
}

// ==================== 重命名 ====================
const renameModalVisible = ref(false);
const renameFile = ref<FileItem | null>(null);
const newFileName = ref('');

function handleRename(file: FileItem) {
  renameFile.value = file;
  newFileName.value = file.originalName;
  renameModalVisible.value = true;
}

function confirmRename() {
  message.info('暂不支持重命名功能');
  renameModalVisible.value = false;
}

// ==================== 删除 ====================
async function handleDelete(file: FileItem) {
  try {
    await fileApi.delete(file.id);
    message.success('删除成功');
    await loadFileList();
  } catch (error) {
    message.error('删除失败');
    console.error(error);
  }
}

async function batchDelete() {
  const ids = selectedFiles.value.map(f => f.id);
  if (ids.length === 0) {
    message.warning('请选择要删除的文件');
    return;
  }
  
  try {
    await fileApi.batchDelete(ids);
    message.success('批量删除成功');
    await loadFileList();
  } catch (error) {
    message.error('批量删除失败');
    console.error(error);
  }
}

// ==================== 新建 ====================
const newMenuOptions = [
  {
    label: '新建文件夹',
    key: 'folder',
    icon: () => h(IconifyIcon, { icon: 'lucide:folder-plus' }),
    disabled: true, // 暂不支持新建文件夹
  },
  {
    label: '新建文档',
    key: 'document',
    icon: () => h(IconifyIcon, { icon: 'lucide:file-plus' }),
    disabled: true, // 暂不支持新建文档
  },
];

const newModalVisible = ref(false);
const newItemType = ref<'folder' | 'document'>('folder');
const newItemName = ref('');

function handleNewMenu(key: string) {
  newItemType.value = key as 'folder' | 'document';
  newItemName.value = '';
  newModalVisible.value = true;
}

function confirmNew() {
  message.info('暂不支持此功能');
  newModalVisible.value = false;
}

// ==================== 上传 ====================
const uploadModalVisible = ref(false);
const uploading = ref(false);

async function handleUpload(options: { file: UploadFileInfo }) {
  if (!options.file.file) {
    message.error('文件不能为空');
    return;
  }
  
  try {
    uploading.value = true;
    await fileApi.upload(options.file.file, currentPath.value);
    message.success(`上传文件成功: ${options.file.name}`);
    await loadFileList();
    uploadModalVisible.value = false;
  } catch (error) {
    message.error(`上传文件失败: ${options.file.name}`);
    console.error(error);
  } finally {
    uploading.value = false;
  }
}

// ==================== 双击打开 ====================
function handleDoubleClick(file: FileItem) {
  // 文件系统不支持文件夹，直接打开文件
  window.open(file.url, '_blank');
}

// ==================== 面包屑导航 ====================
const pathSegments = computed(() => {
  const segments = currentPath.value.split('/').filter(Boolean);
  return [{ name: '根目录', path: '/' }, ...segments.map((seg, idx) => ({
    name: seg,
    path: '/' + segments.slice(0, idx + 1).join('/'),
  }))];
});

function navigateToPath(path: string) {
  currentPath.value = path;
}

onMounted(() => {
  loadFileList();
});
</script>

<template>
  <Page>
    <NSplit
      direction="horizontal"
      default-size="240px"
      min="200px"
      max="320px"
      :resizable="true"
      class="h-full p-2"
    >
      <template #1>
        <!-- 左侧导航栏 -->
        <div class="h-full bg-background p-4 space-y-4">
          <!-- 文件类型过滤 -->
          <div class="space-y-1">
            <div
              v-for="type in fileTypes"
              :key="type.key"
              class="flex items-center gap-2 px-3 py-2 rounded cursor-pointer transition-colors"
              :class="
                selectedFileType === type.value
                  ? 'bg-primary text-primary-foreground'
                  : 'hover:bg-muted'
              "
              @click="selectedFileType = type.value"
            >
              <IconifyIcon :icon="type.icon" class="text-lg" />
              <span>{{ type.label }}</span>
            </div>
          </div>

          <!-- 存储空间 -->
          <NCard size="small" class="mt-4">
            <div class="space-y-2">
              <div class="flex items-center justify-between text-sm">
                <span>存储空间</span>
                <span class="text-gray-500">
                  {{ storageInfo.used }}GB / {{ storageInfo.total }}GB
                </span>
              </div>
              <NProgress
                type="line"
                :percentage="storagePercent"
                :show-indicator="false"
                :color="storagePercent > 80 ? '#f56c6c' : '#409eff'"
              />
            </div>
          </NCard>
        </div>
      </template>

      <template #2>
        <!-- 右侧主内容区 -->
        <div class="h-full bg-background p-4 flex flex-col">
          <!-- 顶部工具栏 -->
          <div class="flex items-center justify-between mb-4 gap-3">
            <!-- 面包屑导航 -->
            <div class="flex items-center gap-2 flex-1">
              <span
                v-for="(segment, index) in pathSegments"
                :key="index"
                class="flex items-center gap-2"
              >
                <span
                  class="cursor-pointer hover:text-primary"
                  @click="navigateToPath(segment.path)"
                >
                  {{ segment.name }}
                </span>
                <IconifyIcon
                  v-if="index < pathSegments.length - 1"
                  icon="lucide:chevron-right"
                  class="text-gray-400"
                />
              </span>
            </div>

            <!-- 搜索和操作 -->
            <NSpace>
              <NInput
                v-model:value="searchKeyword"
                placeholder="搜索文件"
                clearable
                class="w-[200px]"
              >
                <template #prefix>
                  <NIcon><SearchOutline /></NIcon>
                </template>
              </NInput>

              <NButton type="primary" @click="uploadModalVisible = true">
                <template #icon><IconifyIcon icon="lucide:upload" /></template>
                上传
              </NButton>

              <NDropdown :options="newMenuOptions" @select="handleNewMenu">
                <NButton>
                  <template #icon><IconifyIcon icon="lucide:plus" /></template>
                  新建
                </NButton>
              </NDropdown>

              <!-- 视图切换 -->
              <NButton
                :type="viewMode === 'grid' ? 'primary' : 'default'"
                @click="viewMode = 'grid'"
              >
                <template #icon><IconifyIcon icon="lucide:grid-3x3" /></template>
              </NButton>
              <NButton
                :type="viewMode === 'list' ? 'primary' : 'default'"
                @click="viewMode = 'list'"
              >
                <template #icon><IconifyIcon icon="lucide:list" /></template>
              </NButton>
            </NSpace>
          </div>

          <!-- 批量操作栏 -->
          <div
            v-if="selectedFiles.length > 0"
            class="flex items-center justify-between mb-4 p-3 bg-primary/10 rounded"
          >
            <span class="text-sm">
              已选中 <span class="font-bold text-primary">{{ selectedFiles.length }}</span> 项
            </span>
            <NSpace>
              <NButton size="small" @click="clearSelection">取消选择</NButton>
              <NButton size="small" type="error" @click="batchDelete">
                <template #icon><IconifyIcon icon="lucide:trash-2" /></template>
                批量删除
              </NButton>
            </NSpace>
          </div>

          <!-- 文件列表 - 网格视图 -->
          <NSpin :show="loading">
            <div v-if="viewMode === 'grid'" class="flex-1 overflow-auto">
              <div v-if="fileList.length === 0" class="flex items-center justify-center h-64 text-gray-400">
                <div class="text-center">
                  <IconifyIcon icon="lucide:folder-open" class="text-6xl mb-4" />
                  <p>暂无文件</p>
                </div>
              </div>
              <div v-else class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-4">
                <NCard
                  v-for="file in fileList"
                  :key="file.id"
                  size="small"
                  hoverable
                  class="cursor-pointer relative"
                  :class="{ 'ring-2 ring-primary': file.selected }"
                  @click="toggleFileSelection(file)"
                  @dblclick="handleDoubleClick(file)"
                >
                  <NDropdown
                    trigger="manual"
                    placement="bottom-start"
                    :options="contextMenuOptions(file)"
                    :show="false"
                    @select="(key) => handleContextMenu(key as string, file)"
                  >
                    <div class="flex flex-col items-center gap-2">
                      <!-- 文件图标 -->
                      <div class="relative">
                        <IconifyIcon
                          :icon="getFileIcon(file)"
                          class="text-6xl text-blue-500"
                        />
                        <IconifyIcon
                          v-if="file.starred"
                          icon="lucide:star"
                          class="absolute -top-1 -right-1 text-yellow-500 text-lg"
                        />
                      </div>

                      <!-- 文件名 -->
                      <div class="text-sm text-center break-all line-clamp-2 w-full">
                        {{ file.fileName }}
                      </div>

                      <!-- 文件信息 -->
                      <div class="text-xs text-gray-500 text-center w-full">
                        <div>{{ formatFileSize(file.size) }}</div>
                        <div>{{ file.createTime }}</div>
                      </div>
                    </div>
                  </NDropdown>
                </NCard>
              </div>
            </div>

            <!-- 文件列表 - 列表视图 -->
            <div v-else class="flex-1 overflow-auto">
              <div v-if="fileList.length === 0" class="flex items-center justify-center h-64 text-gray-400">
                <div class="text-center">
                  <IconifyIcon icon="lucide:folder-open" class="text-6xl mb-4" />
                  <p>暂无文件</p>
                </div>
              </div>
              <table v-else class="w-full">
                <thead class="bg-gray-50 dark:bg-gray-800 sticky top-0">
                  <tr>
                    <th class="px-4 py-2 text-left w-12">
                      <NCheckbox :checked="selectedFiles.length === fileList.length" @update:checked="selectAll" />
                    </th>
                    <th class="px-4 py-2 text-left">名称</th>
                    <th class="px-4 py-2 text-left w-32">大小</th>
                    <th class="px-4 py-2 text-left w-48">创建时间</th>
                    <th class="px-4 py-2 text-left w-32">操作</th>
                  </tr>
                </thead>
                <tbody>
                  <tr
                    v-for="file in fileList"
                    :key="file.id"
                    class="border-b hover:bg-gray-50 dark:hover:bg-gray-800 cursor-pointer"
                    :class="{ 'bg-primary/10': file.selected }"
                    @click="toggleFileSelection(file)"
                    @dblclick="handleDoubleClick(file)"
                  >
                    <td class="px-4 py-2">
                      <NCheckbox v-model:checked="file.selected" @click.stop />
                    </td>
                    <td class="px-4 py-2">
                      <div class="flex items-center gap-2">
                        <IconifyIcon
                          :icon="getFileIcon(file)"
                          class="text-2xl text-blue-500"
                        />
                        <span>{{ file.fileName }}</span>
                        <IconifyIcon
                          v-if="file.starred"
                          icon="lucide:star"
                          class="text-yellow-500"
                        />
                      </div>
                    </td>
                    <td class="px-4 py-2 text-gray-500">
                      {{ formatFileSize(file.size) }}
                    </td>
                    <td class="px-4 py-2 text-gray-500">{{ file.createTime }}</td>
                    <td class="px-4 py-2">
                      <NDropdown
                        trigger="click"
                        :options="contextMenuOptions(file)"
                        @select="(key) => handleContextMenu(key as string, file)"
                      >
                        <NButton size="small" text @click.stop>
                          <template #icon><IconifyIcon icon="lucide:more-horizontal" /></template>
                        </NButton>
                      </NDropdown>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </NSpin>

          <!-- 分页 -->
          <div v-if="total > 0" class="flex justify-end mt-4">
            <NPagination
              v-model:page="pageNum"
              v-model:page-size="pageSize"
              :page-count="Math.ceil(total / pageSize)"
              :page-sizes="[10, 20, 50, 100]"
              show-size-picker
            />
          </div>
        </div>
      </template>
    </NSplit>

    <!-- 重命名对话框 -->
    <NModal
      v-model:show="renameModalVisible"
      preset="dialog"
      title="重命名"
      positive-text="确定"
      negative-text="取消"
      @positive-click="confirmRename"
    >
      <NInput v-model:value="newFileName" placeholder="请输入新名称" />
    </NModal>

    <!-- 新建对话框 -->
    <NModal
      v-model:show="newModalVisible"
      preset="dialog"
      :title="`新建${newItemType === 'folder' ? '文件夹' : '文档'}`"
      positive-text="确定"
      negative-text="取消"
      @positive-click="confirmNew"
    >
      <NInput
        v-model:value="newItemName"
        :placeholder="`请输入${newItemType === 'folder' ? '文件夹' : '文档'}名称`"
      />
    </NModal>

    <!-- 上传对话框 -->
    <NModal v-model:show="uploadModalVisible" preset="dialog" title="上传文件">
      <NSpin :show="uploading">
        <NUpload
          multiple
          directory-dnd
          :custom-request="handleUpload"
        >
          <div class="flex flex-col items-center justify-center p-8 border-2 border-dashed rounded cursor-pointer hover:border-primary">
            <IconifyIcon icon="lucide:upload-cloud" class="text-6xl text-gray-400 mb-4" />
            <p class="text-gray-600">点击或拖拽文件到此处上传</p>
            <p class="text-sm text-gray-400 mt-2">支持批量上传</p>
          </div>
        </NUpload>
      </NSpin>
    </NModal>
  </Page>
</template>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
