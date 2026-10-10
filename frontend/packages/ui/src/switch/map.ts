export type SwitchScalar = boolean | number | string;

/** 用严格相等判断开/关，和 Naive `checked-value` 一样，避免 `"1"` 被当成 `1`。 */
export function switchChecked(
  value: unknown,
  checkedValue: SwitchScalar,
): boolean {
  return value === checkedValue;
}

export function switchNextValue(
  checked: boolean,
  checkedValue: SwitchScalar,
  uncheckedValue: SwitchScalar,
): SwitchScalar {
  return checked ? checkedValue : uncheckedValue;
}
