import { describe, expect, it } from 'vitest';

import { switchChecked, switchNextValue } from './map';

describe('switch value bridge', () => {
  it('keeps boolean switches on true / false', () => {
    expect(switchChecked(true, true)).toBe(true);
    expect(switchChecked(false, true)).toBe(false);
    expect(switchChecked(null, true)).toBe(false);
    expect(switchNextValue(true, true, false)).toBe(true);
    expect(switchNextValue(false, true, false)).toBe(false);
  });

  it('maps 1/0 and other numeric pairs without coercing strings', () => {
    expect(switchChecked(1, 1)).toBe(true);
    expect(switchChecked(0, 1)).toBe(false);
    expect(switchChecked('1', 1)).toBe(false);
    expect(switchNextValue(true, 1, 0)).toBe(1);
    expect(switchNextValue(false, 1, 0)).toBe(0);
    expect(switchNextValue(false, 1, 2)).toBe(2);
  });
});
