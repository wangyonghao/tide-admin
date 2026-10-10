import { describe, expect, it } from 'vitest';

import {
  commitFormText,
  displayFormChecked,
  displayFormText,
} from './empty-value';

describe('form empty value', () => {
  it('renders null as an empty input without writing null back', () => {
    expect(displayFormText(null)).toBe('');
    expect(displayFormText(undefined)).toBe('');
    expect(displayFormText('tide')).toBe('tide');
    expect(commitFormText('')).toBe('');
    expect(commitFormText(null)).toBe('');
    expect(commitFormText('tide')).toBe('tide');
  });

  it('treats only true as checked', () => {
    expect(displayFormChecked(true)).toBe(true);
    expect(displayFormChecked(false)).toBe(false);
    expect(displayFormChecked(null)).toBe(false);
    expect(displayFormChecked(1)).toBe(false);
  });
});
