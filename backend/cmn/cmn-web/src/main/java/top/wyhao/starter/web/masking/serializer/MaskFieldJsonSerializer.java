
package top.wyhao.starter.web.masking.serializer;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import top.wyhao.starter.core.constant.StringConstants;
import top.wyhao.starter.web.masking.annotation.MaskField;
import top.wyhao.starter.web.masking.enums.MaskStrategy;

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
        this.strategy = maskField.value();
        return this;
    }
}
