/**
 * useVbenForm 的空值是 null（见 adapter/form.ts）。
 * 输入框只把 null 画成空字符串；用户清空后仍回写空字符串，
 * 这样 zod 的 string 规则按空字符串失败，而不是类型错误。
 * 重置由表单把字段写成 null，组件读取即可。
 */
export function displayFormText(value: unknown): string {
  if (value == null) return '';
  return String(value);
}

export function commitFormText(value: unknown): string {
  if (value == null) return '';
  return String(value);
}

/** 勾选只认 true。null / false 都是未勾选，重置写成 null 时显示为关。 */
export function displayFormChecked(value: unknown): boolean {
  return value === true;
}
