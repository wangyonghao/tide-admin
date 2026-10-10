import { describe, expect, it } from 'vitest';

import { takeSelectedFiles } from './files';

function file(name: string) {
  return new File(['x'], name, { type: 'text/plain' });
}

describe('page upload file pick', () => {
  it('returns nothing when the picker is empty', () => {
    expect(takeSelectedFiles(null)).toEqual([]);
    expect(takeSelectedFiles(undefined, { multiple: false })).toEqual([]);
  });

  it('keeps a single file when multiple is off, then respects max', () => {
    const files = [file('a.txt'), file('b.txt'), file('c.txt')];
    expect(
      takeSelectedFiles(files, { multiple: false }).map((item) => item.name),
    ).toEqual(['a.txt']);
    expect(
      takeSelectedFiles(files, { max: 2, multiple: true }).map(
        (item) => item.name,
      ),
    ).toEqual(['a.txt', 'b.txt']);
  });
});
