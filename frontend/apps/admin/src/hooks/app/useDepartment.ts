import type { DepartmentResult } from '#/api/system/department';

import { ref } from 'vue';

import { departmentApi } from '#/api/system/department';

/** 部门模块 */
export function useDepartment(options?: { onSuccess?: () => void }) {
  const loading = ref(false);
  const departmentList = ref<DepartmentResult[]>([]);

  const getDepartmentList = async (keyword?: string) => {
    try {
      loading.value = true;
      const res = await departmentApi.tree({ keyword: keyword });
      departmentList.value = res;
      options?.onSuccess && options.onSuccess();
    } finally {
      loading.value = false;
    }
  };
  return { departmentList, getDepartmentList, loading };
}
