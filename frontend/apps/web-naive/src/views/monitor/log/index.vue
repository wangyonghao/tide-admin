<script setup lang="ts">
import { Page } from '@vben/common-ui';

import { Tabs, TabsContent, TabsList, TabsTrigger } from '@vben-core/shadcn-ui';

import LoginLogList from './login-log.vue';
import OperationLogList from './operation-log.vue';
import SmsLogList from './sms-log.vue';
</script>
<template>
  <Page h-full bg-background p-4>
    <Tabs default-value="login" class="w-full">
      <TabsList>
        <TabsTrigger value="login">
          {{ $t('monitor.loginLog.title') }}
        </TabsTrigger>
        <TabsTrigger value="operation">
          {{ $t('monitor.operationLog.title') }}
        </TabsTrigger>
        <TabsTrigger value="sms">
          {{ $t('system.smsLog.listTitle') }}
        </TabsTrigger>
      </TabsList>
      <TabsContent value="login">
        <LoginLogList />
      </TabsContent>
      <TabsContent value="operation">
        <OperationLogList />
      </TabsContent>
      <TabsContent value="sms">
        <SmsLogList />
      </TabsContent>
    </Tabs>
  </Page>
</template>
<style lang="scss" scoped></style>
