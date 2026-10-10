/** 页面上传从文件选择框取出 File。multiple 为 false 时只留第一个，max 再封顶。 */
export function takeSelectedFiles(
  list: FileList | readonly File[] | null | undefined,
  options: { max?: number; multiple?: boolean } = {},
): File[] {
  const files = list ? [...list] : [];
  const picked = options.multiple === false ? files.slice(0, 1) : files;
  const max =
    typeof options.max === 'number' && options.max > 0
      ? options.max
      : undefined;
  return max == null ? picked : picked.slice(0, max);
}
