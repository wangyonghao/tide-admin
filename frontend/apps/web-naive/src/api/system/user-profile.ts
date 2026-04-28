import http from '#/api/http';

/** 绑定三方账号信息 */
export interface BindSocialAccountRes {
  source: string;
  description: string;
}

/** 上传头像 */
export function uploadAvatar(data: FormData) {
  return http.patch(`/user/profile/avatar`, data);
}

/** 修改用户基本信息 */
export function updateUserBaseInfo(data: { gender: number; nickname: string }) {
  return http.patch(`/user/profile/basic/info`, data);
}

/** 修改密码 */
export function updateUserPassword(data: {
  newPassword: string;
  oldPassword: string;
}) {
  return http.patch(`/user/profile/password`, data);
}

/** 修改手机号 */
export function updateUserPhone(data: {
  captcha: string;
  oldPassword: string;
  phone: string;
}) {
  return http.patch(`/user/profile/phone`, data);
}

/** 修改邮箱 */
export function updateUserEmail(data: {
  captcha: string;
  email: string;
  oldPassword: string;
}) {
  return http.patch(`/user/profile/email`, data);
}

/** 获取绑定的三方账号 */
export function listUserSocial() {
  return http.get<BindSocialAccountRes[]>(`/user/profile/social`);
}

/** 绑定三方账号 */
export function bindSocialAccount(source: string, data: any) {
  return http.post(`/user/profile/social/${source}`, data);
}

/** 解绑三方账号 */
export function unbindSocialAccount(source: string) {
  return http.delete(`/user/profile/social/${source}`);
}
