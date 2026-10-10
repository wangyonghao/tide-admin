import { describe, expect, it } from 'vitest';

import { buildNoticePayload, formatNoticePublishTime } from './form-values';

describe('notice form values', () => {
  it('formats a timestamp in UTC and drops an unfinished time', () => {
    expect(formatNoticePublishTime('false', Date.UTC(2024, 0, 2, 3, 4, 5))).toBe(
      undefined,
    );
    expect(formatNoticePublishTime('true', null)).toBe(undefined);
    expect(
      formatNoticePublishTime('true', Date.UTC(2024, 0, 2, 3, 4, 5)),
    ).toBe('2024-01-02 03:04:05');
  });

  it('joins selected users only when the scope is specific people', () => {
    expect(
      buildNoticePayload({
        title: '标题',
        content: '正文',
        type: '1',
        noticeScope: '1',
        noticeUsers: ['9'],
        noticeMethods: ['1', '2'],
        isTiming: 'false',
        isTop: 'true',
      }),
    ).toMatchObject({
      noticeMethods: '1,2',
      noticeScope: '1',
      noticeUsers: undefined,
      publishTime: undefined,
      isTop: 'true',
    });

    expect(
      buildNoticePayload({
        noticeScope: '2',
        noticeUsers: null,
        noticeMethods: [],
        isTiming: 'true',
        publishTime: Date.UTC(2024, 5, 1, 0, 0, 0),
      }).noticeUsers,
    ).toBeUndefined();

    expect(
      buildNoticePayload({
        noticeScope: '2',
        noticeUsers: ['3', '4'],
        isTiming: 'true',
        publishTime: Date.UTC(2024, 5, 1, 0, 0, 0),
      }),
    ).toMatchObject({
      noticeUsers: '3,4',
      publishTime: '2024-06-01 00:00:00',
    });
  });
});
