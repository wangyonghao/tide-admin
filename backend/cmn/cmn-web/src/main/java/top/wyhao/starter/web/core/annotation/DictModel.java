
package top.wyhao.starter.web.core.annotation;

import java.lang.annotation.*;

/**
 * 字典结构映射
 *
 * <p>用于查询字典列表 API（下拉选项等场景）</p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DictModel {

    /**
     * 标签字段名
     *
     * @return 标签字段名
     */
    String labelKey() default "name";

    /**
     * 值字段名
     *
     * @return 值字段名
     */
    String valueKey() default "id";

    /**
     * 额外信息字段名
     *
     * @return 额外信息字段名
     */
    String[] extraKeys() default {};
}
