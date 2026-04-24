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

package top.wyhao.admin.open.handler;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.handler.SaAnnotationHandlerInterface;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.sign.template.SaSignTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.AnnotatedElement;
import java.util.Collection;

import static cn.dev33.satoken.annotation.handler.SaCheckPermissionHandler._checkMethod;

/**
 * 重定义注解 SaCheckPermission 的处理器
 */
@Component
public class SaCheckPermissionHandler implements SaAnnotationHandlerInterface<SaCheckPermission> {

    @Override
    public Class<SaCheckPermission> getHandlerAnnotationClass() {
        return SaCheckPermission.class;
    }

    @Override
    public void checkMethod(SaCheckPermission saCheckPermission, AnnotatedElement annotatedElement) {
        if (!isSignParamExists()) {
            _checkMethod(saCheckPermission.type(), saCheckPermission.value(), saCheckPermission
                .mode(), saCheckPermission.orRole());
        }
    }

    /**
     * 判断请求是否包含sign参数
     *
     * @return 是否包含sign参数（true：包含；false：不包含）
     */
    public static boolean isSignParamExists() {
        SaRequest saRequest = SaHolder.getRequest();
        Collection<String> paramNames = saRequest.getParamNames();
        return paramNames.stream().anyMatch(SaSignTemplate.sign::equals);
    }
}
