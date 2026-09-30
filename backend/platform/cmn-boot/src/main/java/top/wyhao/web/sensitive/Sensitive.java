
package top.wyhao.web.sensitive.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;import top.wyhao.web.sensitive.enums.SensitiveMethod;import top.wyhao.web.sensitive.serializer.SensitiveSerializer;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记敏感字段的脱敏策略, 用于序列化为JSON时脱敏
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = SensitiveSerializer.class)
public @interface Sensitive {

    /**
     * 脱敏类型
     */
    SensitiveMethod value() default SensitiveMethod.NONE;
}