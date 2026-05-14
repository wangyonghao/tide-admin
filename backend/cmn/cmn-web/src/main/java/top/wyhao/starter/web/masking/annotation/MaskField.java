
package top.wyhao.starter.web.masking.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;import top.wyhao.starter.web.masking.enums.MaskStrategy;import top.wyhao.starter.web.masking.serializer.MaskFieldJsonSerializer;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * JSON 脱敏注解
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = MaskFieldJsonSerializer.class)
public @interface MaskField {

    /**
     * 脱敏类型
     */
    MaskStrategy value() default MaskStrategy.NONE;
}