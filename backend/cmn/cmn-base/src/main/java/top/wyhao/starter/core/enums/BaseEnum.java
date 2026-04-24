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

package top.wyhao.starter.core.enums;

import java.util.Objects;

/**
 * 枚举接口
 *
 * @author wyhao
 */
public interface BaseEnum {

    /**
     * 枚举值
     *
     * @return 枚举值
     */
    Integer getValue();

    String getDescription();
    /**
     * 根据枚举值获取
     *
     * @param value 枚举值
     * @return 枚举对象
     */
    static <E extends BaseEnum> E getByValue(Integer value, Class<E> clazz) {
        for (E e : clazz.getEnumConstants()) {
            if (Objects.equals(e.getValue(), value)) {
                return e;
            }
        }
        return null;
    }

}
