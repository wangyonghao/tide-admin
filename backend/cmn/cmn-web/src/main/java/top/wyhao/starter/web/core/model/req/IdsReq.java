
package top.wyhao.starter.web.core.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.io.Serializable;
import java.util.List;

/**
 * ID 列表请求参数
 *
 * @author Charles7c
 * @since 2.11.0
 */
public class IdsReq implements Serializable {

    /**
     * ID
     */
    @Schema(description = "ID", example = "[1,2]")
    @NotEmpty(message = "ID 不能为空")
    private List<Long> ids;

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }
}
