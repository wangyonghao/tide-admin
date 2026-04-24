/*
 * Copyright (c) 2022-present wangyonghao Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.wyhao.starter.mail;

import org.springframework.mail.MailPreparationException;
import top.wyhao.starter.core.util.validation.ValidationUtils;

/**
 * 邮件配置
 *
 * @author Charles7c
 * @since 2.1.0
 */
public interface MailConfigurer {

    /**
     * 获取邮件配置
     *
     * @return 邮件配置
     */
    MailAccount getMailAccount();

    default void checkMailAccount(MailAccount mailAccount) throws MailPreparationException {
        ValidationUtils.throwIfBlank(mailAccount.getHost(), "邮件配置不正确：服务器地址不能为空");
        ValidationUtils.throwIfNull(mailAccount.getPort(), "邮件配置不正确：服务器端口不能为空");
        ValidationUtils.throwIfBlank(mailAccount.getUsername(), "邮件配置不正确：用户名不能为空");
        ValidationUtils.throwIfBlank(mailAccount.getPassword(), "邮件配置不正确：密码不能为空");
    }
}
