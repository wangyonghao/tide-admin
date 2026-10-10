/**
 * 预览树的文件 key 是文件名，目录 key 是自增字符串，文件挂在目录下面。
 * 点选必须往下找：只扫顶层会对不上子目录里的文件。
 */

export interface PreviewNode {
  children?: PreviewNode[];
  key: string;
  title: string;
}

export function findPreviewNode(
  nodes: readonly PreviewNode[],
  key: string,
): PreviewNode | undefined {
  for (const node of nodes) {
    if (node.key === key) return node;
    const nested = findPreviewNode(node.children ?? [], key);
    if (nested) return nested;
  }
  return undefined;
}

/** 目录和常见源码后缀各用一个图标。对不上的文件用普通文件图标。 */
export function previewFileIcon(title: string, directory: boolean): string {
  if (directory) return 'lucide:folder';
  const name = title.toLowerCase();
  if (name.endsWith('pom.xml') || name.endsWith('.xml')) {
    return 'lucide:file-code';
  }
  if (
    name.endsWith('.java') ||
    name.endsWith('.vue') ||
    name.endsWith('.ts') ||
    name.endsWith('.js')
  ) {
    return 'lucide:file-code-2';
  }
  if (name.endsWith('.json')) return 'lucide:file-json';
  if (name.endsWith('.sql')) return 'lucide:database';
  return 'lucide:file';
}
