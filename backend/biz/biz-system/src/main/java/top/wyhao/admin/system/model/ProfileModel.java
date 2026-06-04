package top.wyhao.admin.system.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import top.wyhao.starter.core.validation.Mobile;

public class ProfileModel {

    /**
     * 邮箱修改请求参数
     */
    @Schema(description = "邮箱修改请求参数")
    public record EmailUpdate(
            /**
             * 新邮箱
             */
            @Schema(description = "新邮箱", example = "123456789@qq.com")
            @NotBlank(message = "新邮箱不能为空")
            @Email(message = "新邮箱格式不正确")
            String email,

            /**
             * 验证码
             */
            @Schema(description = "验证码", example = "888888")
            @NotBlank(message = "验证码不能为空")
            @Length(max = 6, message = "验证码无效")
            String captcha,

            /**
             * 当前密码
             */
            @Schema(description = "当前密码", example = "RSA 公钥加密的当前密码")
            @NotBlank(message = "当前密码不能为空")
            String oldPassword
    ) {
    }

    /**
     * 手机号修改请求参数
     */
    @Schema(description = "手机号修改请求参数")
    public record PhoneUpdate(
            /**
             * 新手机号
             */
            @Schema(description = "新手机号", example = "13811111111")
            @NotBlank(message = "新手机号不能为空")
            @Mobile(message = "新手机号格式不正确")
            String phone,

            /**
             * 验证码
             */
            @Schema(description = "验证码", example = "888888")
            @NotBlank(message = "验证码不能为空")
            @Length(max = 6, message = "验证码无效")
            String captcha,

            /**
             * 当前密码
             */
            @Schema(description = "当前密码", example = "RSA 公钥加密的当前密码")
            @NotBlank(message = "当前密码不能为空")
            String oldPassword
    ) {
    }

    /**
     * 密码修改请求参数
     */
    @Schema(description = "密码修改请求参数")
    public record PasswordUpdate(
            /**
             * 当前密码
             */
            @Schema(description = "当前密码", example = "RSA 公钥加密的当前密码")
            String oldPassword,

            /**
             * 新密码
             */
            @Schema(description = "新密码", example = "RSA 公钥加密的新密码")
            @NotBlank(message = "新密码不能为空")
            String newPassword
    ) {
    }

    @Schema(description = "头像上传响应参数")
    public record AvatarResult(
            /**
             * 头像地址
             */
            @Schema(description = "头像地址", example = "https://himg.bdimg.com/sys/portrait/item/public.1.81ac9a9e.rf1ix17UfughLQjNo7XQ_w.jpg")
            String avatar
    ) {}
}
