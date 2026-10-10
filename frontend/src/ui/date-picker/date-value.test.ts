import { describe, expect, it } from 'vitest';

import {
  buildMonthCells,
  commitDateSelection,
  displayDateLabel,
  isDateEmpty,
  parseDateValue,
  partToTimestamp,
  resolveDateKind,
} from './date-value';

const day: import('./date-value').DatePart = {
  year: 2024,
  month: 5,
  day: 6,
  hour: 8,
  minute: 7,
  second: 5,
};

describe('form date empty value', () => {
  it('treats null as empty and keeps timestamp 0', () => {
    expect(isDateEmpty(null)).toBe(true);
    expect(isDateEmpty(undefined)).toBe(true);
    expect(isDateEmpty('')).toBe(true);
    expect(isDateEmpty([])).toBe(true);
    expect(isDateEmpty([1, null])).toBe(true);
    expect(isDateEmpty(0)).toBe(false);
    expect(isDateEmpty([0, 1])).toBe(false);
    expect(displayDateLabel(null, 'date')).toBe('');
  });

  it('writes null until a range has both ends, and keeps a single day as a timestamp', () => {
    const midnight = { ...day, hour: 0, minute: 0, second: 0 };
    expect(commitDateSelection('date', null, null)).toBeNull();
    expect(commitDateSelection('daterange', midnight, null)).toBeNull();
    expect(commitDateSelection('date', day, null)).toBe(
      partToTimestamp(midnight),
    );
    expect(commitDateSelection('datetime', day, null)).toBe(
      partToTimestamp(day),
    );
    expect(commitDateSelection('daterange', day, { ...day, day: 8 })).toEqual([
      partToTimestamp(midnight),
      partToTimestamp({ ...midnight, day: 8 }),
    ]);
  });

  it('formats with yyyy-MM-dd and YYYY-MM-DD, and reads the string back', () => {
    expect(resolveDateKind(undefined, true)).toBe('datetime');
    expect(
      commitDateSelection('datetime', day, null, 'YYYY-MM-DD HH:mm:ss'),
    ).toBe('2024-05-06 08:07:05');
    expect(
      commitDateSelection('date', day, null, 'yyyy-MM-dd'),
    ).toBe('2024-05-06');
    expect(parseDateValue('2024-05-06 08:07:05', 'yyyy-MM-dd HH:mm:ss')).toEqual(
      day,
    );
    expect(parseDateValue('2024-02-31', 'yyyy-MM-dd')).toBeNull();
    expect(
      displayDateLabel(
        '2024-05-06 08:07:05',
        'datetime',
        'yyyy-MM-dd HH:mm:ss',
        'yyyy-MM-dd HH:mm:ss',
      ),
    ).toBe('2024-05-06 08:07:05');
  });

  it('builds a Monday-first month and pads May 2024 with April 29', () => {
    const cells = buildMonthCells(2024, 5);
    expect(cells[0]).toEqual({
      day: 29,
      month: 4,
      outside: true,
      year: 2024,
    });
    expect(cells[2]).toEqual({
      day: 1,
      month: 5,
      outside: false,
      year: 2024,
    });
    expect(cells).toHaveLength(35);
  });
});
