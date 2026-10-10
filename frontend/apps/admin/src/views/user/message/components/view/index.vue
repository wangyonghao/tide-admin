<script setup lang="ts">
import { onMounted } from 'vue';
import { useRoute } from 'vue-router';

import { Page } from '@vben/common-ui';

import { userMessageApi } from '#/api/system/user-message';
import { useResetReactive } from '#/hooks';


defineOptions({ name: 'UserNotice' });

const route = useRoute();

const { id } = route.query;
const [form, resetForm] = useResetReactive({
  title: '',
  createUserString: '',
  publishTime: '',
  content: '',
});

// 打开
const onOpen = async (id: string) => {
  resetForm();
  const data = await userMessageApi.getNoticeDetail(id);
  Object.assign(form, data);
};

onMounted(() => {
  onOpen(id as string);
});
</script>

<template>
  <Page auto-content-height>
    <div class="detail" style="height: 100%">
      <div class="detail-content">
        <h1 class="title bottom-4">{{ form?.title }}</h1>
        <div class="info text-gray-500">
          <el-space>
            <span>
              <el-icon><user-icon /></el-icon>
              <span class="label">发布人：</span>
              <span>{{ form?.createUserString }}</span>
            </span>
            <el-divider direction="vertical" />
            <span>
              <el-icon><history-icon /></el-icon>
              <span class="label">发布时间：</span>
              <span>{{ form?.publishTime }}</span>
            </span>
            <el-divider v-if="form?.updateTime" direction="vertical" />
            <span v-if="form?.updateTime">
              <el-icon><schedule-icon /></el-icon>
              <span>更新时间：</span>
              <span>{{ form?.updateTime }}</span>
            </span>
          </el-space>
        </div>
        <div style="flex: 1">
          <!-- <AiEditor v-model="form.content" /> -->
        </div>
      </div>
    </div>
  </Page>
</template>

<style scoped lang="scss">
.detail-content {
  .title {
    font-size: 32px;
    text-align: center;
  }

  .info {
    margin-top: 16px;
    text-align: center;
  }

  .icon {
    margin-right: 3px;
  }
}
</style>
