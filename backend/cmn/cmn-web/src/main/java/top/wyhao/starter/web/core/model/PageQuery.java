
package top.wyhao.starter.web.core.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import org.springdoc.core.annotations.ParameterObject;

/**
 * 分页查询条件
 */
@Setter
@Getter
@ParameterObject
@Schema(description = "分页查询条件")
public class PageQuery {
    /**
     * 默认页码：1
     */
    private static final int DEFAULT_PAGE = 1;

    /**
     * 默认每页条数：10
     */
    private static final int DEFAULT_SIZE = 10;

    /**
     * 页码
     */
    @Schema(description = "页码", example = "1")
    @Min(value = 1, message = "页码最小值为 {value}")
    private Integer page = DEFAULT_PAGE;

    /**
     * 每页条数
     */
    @Schema(description = "每页条数", example = "10")
    @Range(min = 1, max = 1000, message = "每页条数（取值范围 {min}-{max}）")
    private Integer size = DEFAULT_SIZE;

    public PageQuery() {
    }

    /**
     * 构造方法
     *
     * <p>
     * 示例：{@code new PageQuery(1, 10)}
     * </p>
     *
     * @param page 页码
     * @param size 每页条数
     * @since 2.12.0
     */
    public PageQuery(Integer page, Integer size) {
        this.page = page;
        this.size = size;
    }
}
