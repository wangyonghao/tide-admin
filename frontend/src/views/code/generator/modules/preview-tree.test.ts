import { describe, expect, it } from 'vitest';

import {
  findPreviewNode,
  previewFileIcon,
  type PreviewNode,
} from './preview-tree';

const tree: PreviewNode[] = [
  {
    title: 'src',
    key: '1-0',
    children: [
      {
        title: 'main',
        key: '2-0',
        children: [
          { title: 'User.java', key: 'User.java', children: [] },
          { title: 'pom.xml', key: 'pom.xml', children: [] },
        ],
      },
    ],
  },
];

describe('preview tree', () => {
  it('finds a file nested under directories', () => {
    expect(findPreviewNode(tree, 'User.java')?.title).toBe('User.java');
    expect(findPreviewNode(tree, 'User.java')?.key).toBe('User.java');
    expect(findPreviewNode(tree, '1-0')?.title).toBe('src');
    expect(findPreviewNode(tree, 'missing')).toBeUndefined();
  });

  it('picks an icon from the file name', () => {
    expect(previewFileIcon('src', true)).toBe('lucide:folder');
    expect(previewFileIcon('User.java', false)).toBe('lucide:file-code-2');
    expect(previewFileIcon('pom.xml', false)).toBe('lucide:file-code');
    expect(previewFileIcon('app.json', false)).toBe('lucide:file-json');
    expect(previewFileIcon('init.sql', false)).toBe('lucide:database');
    expect(previewFileIcon('README.md', false)).toBe('lucide:file');
  });
});
