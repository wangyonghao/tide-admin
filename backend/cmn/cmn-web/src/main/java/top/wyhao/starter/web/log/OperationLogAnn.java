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

package top.wyhao.starter.web.log;

import java.lang.annotation.*;

/**
 * 日志注解
 * <p>用于接口方法或类上，辅助 Spring Doc 使用效果最佳</p>
 *
 * @author Charles7c
 * @since 1.1.0
 */
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface OperationLogAnn {
    // 业务对象类型: user/order/task/bug
    String objectType();
    // 操作类型：create/update/delete
    String operationType();
    // 业务ID参数名（从入参拿id）
    String objectIdParam() default "id";
    // 备注
    String remark() default "";
}
