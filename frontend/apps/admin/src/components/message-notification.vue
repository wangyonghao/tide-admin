<script setup lang="ts">
import { useRouter } from 'vue-router';

import { useMessageStore } from '@/stores/message';
import {
  ChatbubbleOutline,
  CloseOutline,
  NotificationsOutline,
} from '@vicons/ionicons5';

const router = useRouter();
const messageStore = useMessageStore();

function formatTime(timestamp: number | string | undefined): string {
  if (!timestamp) return '';
  const date = new Date(timestamp);
  if (Number.isNaN(date.getTime())) return '';
  const hours = date.getHours().toString().padStart(2, '0');
  const minutes = date.getMinutes().toString().padStart(2, '0');
  return `${hours}:${minutes}`;
}

function handleView() {
  const notification = messageStore.currentNotification;
  messageStore.closeNotification();
  if (notification?.type === 'notice') {
    router.push('/message/notice');
  } else {
    // 如果有群ID，跳转到群聊
    if (notification?.groupId) {
      router.push({
        path: '/message/chat',
        query: { groupId: notification.groupId.toString() },
      });
    }
    // 如果有发送者ID，跳转到私聊
    else if (notification?.senderId) {
      router.push({
        path: '/message/chat',
        query: { userId: notification.senderId.toString() },
      });
    } else {
      router.push('/message/chat');
    }
  }
}
</script>

<template>
  <Transition name="slide-up">
    <div
      v-if="messageStore.showNotification && messageStore.currentNotification"
      class="notification-popup"
    >
      <div class="notification-header">
        <div class="notification-icon">
          <n-icon
            v-if="messageStore.currentNotification.type === 'notice'"
            size="20"
          >
            <NotificationsOutline />
          </n-icon>
          <n-icon v-else size="20">
            <ChatbubbleOutline />
          </n-icon>
        </div>
        <span class="notification-title">{{
          messageStore.currentNotification.title
        }}</span>
        <n-button
          quaternary
          circle
          size="tiny"
          @click="messageStore.closeNotification()"
        >
          <template #icon>
            <n-icon><CloseOutline /></n-icon>
          </template>
        </n-button>
      </div>
      <div class="notification-content">
        {{ messageStore.currentNotification.content }}
      </div>
      <div class="notification-footer">
        <span class="notification-time">{{
          formatTime(messageStore.currentNotification.time)
        }}</span>
        <n-button text type="primary" size="small" @click="handleView">
          查看详情
        </n-button>
      </div>
    </div>
  </Transition>
</template>

<style scoped>
.notification-popup {
  position: fixed;
  bottom: 20px;
  left: 20px;
  z-index: 9999;
  width: 320px;
  overflow: hidden;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgb(0 0 0 / 15%);
}

.notification-header {
  display: flex;
  gap: 8px;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #f0f0f0;
}

.notification-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  color: #18a058;
  background: #e8f5e9;
  border-radius: 50%;
}

.notification-title {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  font-size: 14px;
  font-weight: 500;
  color: #333;
  white-space: nowrap;
}

.notification-content {
  display: -webkit-box;
  max-height: 60px;
  padding: 12px 16px;
  overflow: hidden;
  text-overflow: ellipsis;
  -webkit-line-clamp: 2;
  font-size: 13px;
  line-height: 1.5;
  color: #666;
  -webkit-box-orient: vertical;
}

.notification-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 16px 12px;
}

.notification-time {
  font-size: 12px;
  color: #999;
}

/* 动画 */
.slide-up-enter-active,
.slide-up-leave-active {
  transition: all 0.3s ease;
}

.slide-up-enter-from,
.slide-up-leave-to {
  opacity: 0;
  transform: translateY(100%);
}
</style>
