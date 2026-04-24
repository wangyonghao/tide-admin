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

package top.wyhao.starter.json.serializer;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ReflectUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import top.wyhao.starter.core.enums.BaseEnum;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Objects;

/**
 * 枚举接口 BaseEnum 反序列化器
 *
 * @see BaseEnum
 *
 * @author Charles7c
 * @since 2.4.0
 */
@JacksonStdImpl
public class BaseEnumDeserializer<T extends BaseEnum> extends JsonDeserializer<T> {

    /**
     * 静态实例
     */
    public static final BaseEnumDeserializer INSTANCE = new BaseEnumDeserializer();

    @Override
    public T deserialize(JsonParser jsonParser,
                                DeserializationContext deserializationContext) throws IOException {
        Class<?> targetClass = jsonParser.currentValue().getClass();
        String fieldName = jsonParser.currentName();
        String value = jsonParser.getText();
        return this.getEnum(targetClass, value, fieldName);
    }

    /**
     * 通过某字段对应值获取枚举实例，获取不到时为 {@code null}
     *
     * @param targetClass 目标类型
     * @param value       字段值
     * @param fieldName   字段名
     * @return 对应枚举实例 ，获取不到时为 {@code null}
     */
    private T getEnum(Class<?> targetClass, String value, String fieldName) {
        Field field = ReflectUtil.getField(targetClass, fieldName);
        Class<?> fieldTypeClass = field.getType();
        Object[] enumConstants = fieldTypeClass.getEnumConstants();
        for (Object enumConstant : enumConstants) {
            if (enumConstant instanceof BaseEnum) {
                T baseEnum = (T)enumConstant;
                if (Objects.equals(Convert.toStr(baseEnum.getValue()), Convert.toStr(value))) {
                    return baseEnum;
                }
            }
        }
        return null;
    }
}
