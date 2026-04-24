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

package top.wyhao.starter.core.exception;

/**
 * 自定义验证异常-错误请求
 *
 * @author Charles7c
 * @since 1.0.0
 */
public class BadRequestException extends BusinessException {
    public BadRequestException(String code, Object[] args) {
        super(code, args);
    }

    public BadRequestException(String code, String defaultMessage) {
        super(code, defaultMessage);
    }

    public BadRequestException(String code, Object[] args, String defaultMessage) {
        super(code, args, defaultMessage);
    }
}
