<script setup lang="ts">
import { computed, h, onMounted, ref } from 'vue';
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
  NProgress,
  NSpace,
  NSplit,
  NTag,
  NUpload,
  useMessage,
  type UploadFileInfo,
} from 'naive-ui';
import { SearchOutline } from '@vicons/ionicons5';

const message = useMessage();

// ==================== 文件类型过滤 ====================
const fileTypes = [
  { key: 'all', label: '全部', icon: 'lucide:folder' },
  { key: 'image', label: '图片', icon: 'lucide:image' },
  { key: 'video', label: '视频', icon: 'lucide:video' },
  { key: 'audio', label: '音频', icon: 'lucide:music' },
  { key: 'document', label: '文档', icon: 'lucide:file-text' },
  { key: 'other', label: '其他', icon: 'lucide:file' },
];

const selectedFileType = ref('all');
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
interface FileItem {
  id: string;
  name: string;
  type: 'folder' | 'file';
  fileType?: string;
  size?: number;
  modifiedTime: string;
  starred: boolean;
  selected: boolean;
  thumbnail?: string;
}

const currentPath = ref<string[]>(['根目录']);
const fileList = ref<FileItem[]>([
  {
    id: '1',
    name: '文档',
    type: 'folder',
    modifiedTime: '2024-01-15 10:30',
    starred: false,
    selected: false,
  },
  {
    id: '2',
    name: '图片',
    type: 'folder',
    modifiedTime: '2024-01-14 15:20',
    starred: true,
    selected: false,
  },
  {
    id: '3',
    name: '项目文档.pdf',
    type: 'file',
    fileType: 'pdf',
    size: 2048576,
    modifiedTime: '2024-01-13 09:15',
    starred: false,
    selected: false,
  },
  {
    id: '4',
    name: '演示视频.mp4',
    type: 'file',
    fileType: 'video',
    size: 10485760,
    modifiedTime: '2024-01-12 14:45',
    starred: true,
    selected: false,
  },
]);

// ==================== 文件图标 ====================
function getFileIcon(item: FileItem) {
  if (item.type === 'folder') return 'lucide:folder';
  
  const ext = item.name.split('.').pop()?.toLowerCase();
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
      message.info(`下载: ${file.name}`);
      break;
    case 'share':
      message.info(`分享: ${file.name}`);
      break;
    case 'rename':
      handleRename(file);
      break;
    case 'copy':
      message.info(`复制: ${file.name}`);
      break;
    case 'cut':
      message.info(`剪切: ${file.name}`);
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
  newFileName.value = file.name;
  renameModalVisible.value = true;
}

function confirmRename() {
  if (renameFile.value && newFileName.value) {
    renameFile.value.name = newFileName.value;
    message.success('重命名成功');
    renameModalVisible.value = false;
  }
}

// ==================== 删除 ====================
function handleDelete(file: FileItem) {
  const index = fileList.value.findIndex(f => f.id === file.id);
  if (index > -1) {
    fileList.value.splice(index, 1);
    message.success('删除成功');
  }
}

function batchDelete() {
  fileList.value = fileList.value.filter(f => !f.selected);
  message.success('批量删除成功');
}

// ==================== 新建 ====================
const newMenuOptions = [
  {
    label: '新建文件夹',
    key: 'folder',
    icon: () => h(IconifyIcon, { icon: 'lucide:folder-plus' }),
  },
  {
    label: '新建文档',
    key: 'document',
    icon: () => h(IconifyIcon, { icon: 'lucide:file-plus' }),
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
  if (newItemName.value) {
    const newItem: FileItem = {
      id: Date.now().toString(),
      name: newItemType.value === 'folder' ? newItemName.value : `${newItemName.value}.txt`,
      type: newItemType.value === 'folder' ? 'folder' : 'file',
      fileType: newItemType.value === 'document' ? 'txt' : undefined,
      size: newItemType.value === 'document' ? 0 : undefined,
      modifiedTime: new Date().toLocaleString('zh-CN'),
      starred: false,
      selected: false,
    };
    fileList.value.unshift(newItem);
    message.success(`创建${newItemType.value === 'folder' ? '文件夹' : '文档'}成功`);
    newModalVisible.value = false;
  }
}

// ==================== 上传 ====================
const uploadModalVisible = ref(false);

function handleUpload(options: { file: UploadFileInfo }) {
  message.success(`上传文件: ${options.file.name}`);
  // 这里添加实际的上传逻辑
}

// ==================== 双击打开 ====================
function handleDoubleClick(file: FileItem) {
  if (file.type === 'folder') {
    currentPath.value.push(file.name);
    // 这里加载文件夹内容
    message.info(`打开文件夹: ${file.name}`);
  } else {
    message.info(`打开文件: ${file.name}`);
  }
}

// ==================== 面包屑导航 ====================
function navigateToPath(index: number) {
  currentPath.value = currentPath.value.slice(0, index + 1);
  // 这里加载对应路径的内容
}

onMounted(() => {
  // 加载文件列表
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
                selectedFileType === type.key
                  ? 'bg-primary text-primary-foreground'
                  : 'hover:bg-muted'
              "
              @click="selectedFileType = type.key"
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
                v-for="(path, index) in currentPath"
                :key="index"
                class="flex items-center gap-2"
              >
                <span
                  class="cursor-pointer hover:text-primary"
                  @click="navigateToPath(index)"
                >
                  {{ path }}
                </span>
                <IconifyIcon
                  v-if="index < currentPath.length - 1"
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
          <div v-if="viewMode === 'grid'" class="flex-1 overflow-auto">
            <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-4">
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
                        class="text-6xl"
                        :class="file.type === 'folder' ? 'text-yellow-500' : 'text-blue-500'"
                      />
                      <IconifyIcon
                        v-if="file.starred"
                        icon="lucide:star"
                        class="absolute -top-1 -right-1 text-yellow-500 text-lg"
                      />
                    </div>

                    <!-- 文件名 -->
                    <div class="text-sm text-center break-all line-clamp-2 w-full">
                      {{ file.name }}
                    </div>

                    <!-- 文件信息 -->
                    <div class="text-xs text-gray-500 text-center w-full">
                      <div v-if="file.type === 'file'">{{ formatFileSize(file.size) }}</div>
                      <div>{{ file.modifiedTime }}</div>
                    </div>
                  </div>
                </NDropdown>
              </NCard>
            </div>
          </div>

          <!-- 文件列表 - 列表视图 -->
          <div v-else class="flex-1 overflow-auto">
            <table class="w-full">
              <thead class="bg-gray-50 dark:bg-gray-800 sticky top-0">
                <tr>
                  <th class="px-4 py-2 text-left w-12">
                    <NCheckbox :checked="selectedFiles.length === fileList.length" @update:checked="selectAll" />
                  </th>
                  <th class="px-4 py-2 text-left">名称</th>
                  <th class="px-4 py-2 text-left w-32">大小</th>
                  <th class="px-4 py-2 text-left w-48">修改时间</th>
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
                        class="text-2xl"
                        :class="file.type === 'folder' ? 'text-yellow-500' : 'text-blue-500'"
                      />
                      <span>{{ file.name }}</span>
                      <IconifyIcon
                        v-if="file.starred"
                        icon="lucide:star"
                        class="text-yellow-500"
                      />
                    </div>
                  </td>
                  <td class="px-4 py-2 text-gray-500">
                    {{ file.type === 'folder' ? '-' : formatFileSize(file.size) }}
                  </td>
                  <td class="px-4 py-2 text-gray-500">{{ file.modifiedTime }}</td>
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
