import type { DeptResult } from '#/api/system/dept';

import { ref } from 'vue';

import { deptApi } from '#/api/system/dept';

/** 部门模块 */
export function useDept(options?: { onSuccess?: () => void }) {
  const loading = ref(false);
  const deptList = ref<DeptResult[]>([]);

  const getDeptList = async (keyword?: string) => {
    try {
      loading.value = true;
      const res = await deptApi.tree({ keyword: keyword });
      deptList.value = res;
      options?.onSuccess && options.onSuccess();
    } finally {
      loading.value = false;
    }
  };
  return { deptList, getDeptList, loading };
}
