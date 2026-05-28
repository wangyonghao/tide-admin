
package top.wyhao.starter.web.core.model;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Sort;
import top.wyhao.starter.core.constant.StringConstants;
import top.wyhao.starter.core.util.validation.ValidationUtils;
import top.wyhao.cmn.db.util.SqlInjectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 排序查询条件
 */
@Schema(description = "排序查询条件")
public class SortQuery {

    /**
     * 排序条件
     */
    @Schema(description = "排序条件", example = "createTime,desc")
    private String[] sort;
}
