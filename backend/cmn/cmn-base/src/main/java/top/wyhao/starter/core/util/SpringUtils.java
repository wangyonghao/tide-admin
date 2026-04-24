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

package top.wyhao.starter.core.util;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

/**
 * Spring 工具类
 *
 * @author Charles7c
 * @since 2.8.2
 */
public class SpringUtils {

    private SpringUtils() {
    }

    /**
     * 获取代理对象
     *
     * @param target 目标对象
     * @param <T>    目标对象类型
     * @return 代理对象
     * @since 2.8.2
     */
    public static <T> T getProxy(T target) {
        return (T)SpringUtil.getBean(target.getClass());
    }

    /**
     * 通过 class 获取 Bean
     *
     * @param <T>                Bean类型
     * @param clazz              Bean类
     * @return Bean对象
     * @see SpringUtil#getBean(Class)
     */
    public static <T> T getBean(Class<T> clazz) {
        try {
            return SpringUtil.getBean(clazz);
        } catch (NoSuchBeanDefinitionException ignored) {
        }
        return null;
    }
}
