import { requestClient as http } from '#/api/request';

/** 图形验证码类型 */
export interface ImageCaptchaResp {
  uuid: string;
  img: string;
  expireTime: number;
  isEnabled: boolean;
}

/* 行为验证码类型 */
export interface BehaviorCaptchaResp {
  originalImageBase64: string;
  point: {
    x: number;
    y: number;
  };
  jigsawImageBase64: string;
  token: string;
  secretKey: string;
  wordList: string[];
}

export interface BehaviorCaptchaReq {
  captchaType?: string;
  captchaVerification?: string;
  clientUid?: string;
}

export interface CheckBehaviorCaptchaResp {
  repCode: string;
  repMsg: string;
}

/** 获取图片验证码 */
export function getCaptchaConfig() {
  return http.get<boolean>(`/captcha/config`);
}

/** 获取图片验证码 */
export function getImageCaptcha() {
  return http.get<ImageCaptchaResp>(`/captcha/image`);
}

/** 获取短信验证码 */
export function getSmsCaptcha(phone: string, captchaReq: BehaviorCaptchaReq) {
  return http.get<boolean>(
    `/captcha/sms?phone=${phone}&captchaVerification=${encodeURIComponent(captchaReq.captchaVerification || '')}`,
  );
}

/** 获取邮箱验证码 */
export function getEmailCaptcha(email: string, captchaReq: BehaviorCaptchaReq) {
  return http.get<boolean>(
    `/captcha/mail?email=${email}&captchaVerification=${encodeURIComponent(captchaReq.captchaVerification || '')}`,
  );
}

/** 获取行为验证码 */
export function getBehaviorCaptcha(req: any) {
  return http.get<BehaviorCaptchaResp>(`/captcha/behavior`, req);
}

/** 校验行为验证码 */
export function checkBehaviorCaptcha(req: any) {
  return http.post<CheckBehaviorCaptchaResp>(`/captcha/behavior`, req);
}
