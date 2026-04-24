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

package top.wyhao.starter.json.masking.serializer;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import top.wyhao.starter.core.constant.StringConstants;
import top.wyhao.starter.json.masking.annotation.MaskField;
import top.wyhao.starter.json.masking.enums.MaskStrategy;

import java.io.IOException;
import java.util.Objects;

/**
 * JSON 脱敏序列化器
 */
public class MaskFieldJsonSerializer extends JsonSerializer<String> implements ContextualSerializer {

    private MaskStrategy strategy;

    @Override
    public void serialize(String str, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (CharSequenceUtil.isBlank(str)) {
            jsonGenerator.writeString(StringConstants.EMPTY);
            return;
        }
        // 使用自定义脱敏策略
        jsonGenerator.writeString(strategy.mask(str));
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException {
        if (beanProperty == null) {
            return serializerProvider.findNullValueSerializer(null);
        }
        if (!Objects.equals(beanProperty.getType().getRawClass(), String.class)) {
            return serializerProvider.findValueSerializer(beanProperty.getType(), beanProperty);
        }
        MaskField maskField = ObjectUtil.defaultIfNull(beanProperty.getAnnotation(MaskField.class), beanProperty.getContextAnnotation(MaskField.class));
        if (maskField == null) {
            return serializerProvider.findValueSerializer(beanProperty.getType(), beanProperty);
        }
        this.strategy=maskField.value();
        return this;
    }
}
