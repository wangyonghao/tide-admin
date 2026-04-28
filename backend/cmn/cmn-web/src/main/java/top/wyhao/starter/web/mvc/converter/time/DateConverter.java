
package top.wyhao.starter.web.mvc.converter.time;

import cn.hutool.core.date.DateUtil;
import org.jspecify.annotations.NonNull;
import org.springframework.core.convert.converter.Converter;

import java.util.Date;

/**
 * Date 参数转换器
 *
 * @author Charles7c
 * @since 2.10.0
 */
public class DateConverter implements Converter<String, Date> {

    @Override
    public Date convert(@NonNull String source) {
        return DateUtil.parse(source);
    }
}
