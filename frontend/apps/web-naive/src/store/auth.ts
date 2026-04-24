import type { Recordable } from '@vben/types';

import { ref } from 'vue';
import { useRouter } from 'vue-router';

import { LOGIN_PATH } from '@vben/constants';
import { preferences } from '@vben/preferences';
import { resetAllStores, useAccessStore, useUserStore } from '@vben/stores';

import { defineStore } from 'pinia';

import { message } from '#/adapter/naive';
import { AuthTypeConstants } from '#/api';
import { authApi } from '#/api/auth';
import { $t } from '#/locales';
import { encryptByRsa } from '#/utils/crypto';

export const useAuthStore = defineStore('auth', () => {
  const accessStore = useAccessStore();
  const userStore = useUserStore();
  const router = useRouter();

  const loginLoading = ref(false);

  /**
   * 处理登录操作
   * @param params 登录表单数据
   */
  async function login(
    params: Recordable<any>,
    onSuccess?: () => Promise<void> | void,
  ) {
    try {
      loginLoading.value = true;
      params.password = encryptByRsa(params.password) || '';
      params.clientId = import.meta.env.VITE_CLIENT_ID;
      params.authType = AuthTypeConstants.ACCOUNT;
      const loginResult = await authApi.login(params);

      // 检查是否密码过期
      if (loginResult.code === 'PASSWORD_EXPIRED') {
        return {
          userInfo: null,
          passwordExpired: true,
          userId: loginResult.userId,
          tempToken: loginResult.tempToken,
        };
      }

      const { token } = loginResult;
      // 将 accessToken 存储到 accessStore 中
      accessStore.setAccessToken(token);
      accessStore.setLoginExpired(false);

      // 获取用户信息（包含菜单、权限等）
      const userInfo = await fetchUserInfo();
      onSuccess
        ? await onSuccess?.()
        : await router.push(preferences.app.defaultHomePath);

      if (userInfo?.nickname) {
        message.success({
          message: `${$t('authentication.loginSuccessDesc')}:${userInfo?.nickname}`,
          title: $t('authentication.loginSuccess'),
        });
      }
      return { userInfo, passwordExpired: false };
    } finally {
      loginLoading.value = false;
    }
  }

  async function logout(redirect: boolean = true) {
    try {
      await authApi.logout();
    } catch {
      // 不做任何处理
    }
    resetAllStores();
    accessStore.setLoginExpired(false);

    // 回登录页带上当前路由地址
    await router.replace({
      path: LOGIN_PATH,
      query: redirect
        ? { redirect: encodeURIComponent(router.currentRoute.value.fullPath) }
        : {},
    });
  }

  async function fetchUserInfo() {
    const userInfo = await authApi.getUserInfo();

    userStore.setUserInfo(userInfo);
    accessStore.setAccessMenus(userInfo.menus);
    accessStore.setAccessCodes(userInfo.permissions);
    return userInfo;
  }

  function $reset() {
    loginLoading.value = false;
  }

  return {
    $reset,
    login,
    fetchUserInfo,
    loginLoading,
    logout,
  };
});
