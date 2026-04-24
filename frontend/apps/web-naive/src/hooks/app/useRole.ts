import type { Option } from '#/types/global';

import { ref } from 'vue';

import { roleApi } from '#/api/system/role';

/** 角色模块 */
export function useRole(options?: { onSuccess?: () => void }) {
  const loading = ref(false);
  const roleList = ref<Option[]>([]);

  const getRoleList = async () => {
    try {
      loading.value = true;
      const res = await roleApi.listOption();
      roleList.value = res;
      options?.onSuccess && options.onSuccess();
    } finally {
      loading.value = false;
    }
  };
  return { roleList, getRoleList, loading };
}
