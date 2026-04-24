<script setup lang="ts">
import { Page } from '@vben/common-ui';

import { Tabs, TabsContent, TabsList, TabsTrigger } from '@vben-core/shadcn-ui';

import LoginLogList from './login/index.vue';
import OperationLogList from './operation/index.vue';
</script>
<template>
  <Page>
    <Tabs default-value="login" class="w-full">
      <TabsList class="mb-4">
        <TabsTrigger value="login">
          {{ $t('monitor.loginLog.title') }}
        </TabsTrigger>
        <TabsTrigger value="operation">
          {{ $t('monitor.operationLog.title') }}
        </TabsTrigger>
      </TabsList>
      <TabsContent value="login">
        <LoginLogList />
      </TabsContent>
      <TabsContent value="operation">
        <OperationLogList />
      </TabsContent>
    </Tabs>
  </Page>
</template>
<style lang="scss" scoped></style>
