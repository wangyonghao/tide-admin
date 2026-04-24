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

import lombok.Data;
import top.wyhao.starter.core.util.I18nUtils;

/**
 * 业务异常基类
 *
 * @author wyhao
 */
@Data
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        this(null, null, message);
    }

    public BusinessException(String code, Object[] args) {
        this( code, args, null);
    }

    public BusinessException(String code, String defaultMessage) {
        this(null, null, defaultMessage);
    }

    public BusinessException(String code, Object[] args, String defaultMessage) {
        super(defaultMessage);
        this.code = code;
        this.args = args;
        this.defaultMessage = defaultMessage;
    }

    /**
     * 错误码
     */
    private String code;

    /**
     * 错误码对应的参数
     */
    private Object[] args;

    /**
     * 错误消息
     */
    private String defaultMessage;

    // 性能优化：避免重复创建栈轨迹
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

    @Override
    public String getMessage() {
        String message = null;
        if (code != null) {
            message = I18nUtils.message(code, args);
        }
        if (message == null) {
            message = defaultMessage;
        }
        return message;
    }
}