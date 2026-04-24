import type { DeptResp } from '#/api/system/dept';

import { ref } from 'vue';

import { deptApi } from '#/api/system/dept';

/** 部门模块 */
export function useDept(options?: { onSuccess?: () => void }) {
  const loading = ref(false);
  const deptList = ref<DeptResp[]>([]);

  const getDeptList = async (name?: string) => {
    try {
      loading.value = true;
      const res = await deptApi.tree({ description: name });
      deptList.value = res;
      options?.onSuccess && options.onSuccess();
    } finally {
      loading.value = false;
    }
  };
  return { deptList, getDeptList, loading };
}
