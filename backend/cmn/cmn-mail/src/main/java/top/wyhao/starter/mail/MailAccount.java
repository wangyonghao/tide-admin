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

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * 邮箱配置
 */

@Getter
@Setter
public class MailAccount {

    public static final String DEFAULT_PROTOCOL = "smtp";

    /**
     * SMTP服务器地址
     */
    private String host;

    /**
     * SMTP服务器端口
     */
    private Integer port;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码（授权码）
     */
    private String password;

    /**
     * 发送方，遵循RFC-822标准
     */
    private String from;

    /**
     * 使用 STARTTLS安全连接，STARTTLS是对纯文本通信协议的扩展。它将纯文本连接升级为加密连接（TLS或SSL）， 而不是使用一个单独的加密通信端口。
     */
    private boolean starttlsEnable = false;

    /**
     * 是否启用 SSL 连接
     */
    private boolean sslEnabled = false;

    /**
     * SSL 端口
     */
    private Integer sslPort;

    /**
     * 编码用于编码邮件正文和发送人、收件人等中文
     */
    private String defaultEncoding = "UTF-8";

    private final Map<String, String> properties = new HashMap<>();
}