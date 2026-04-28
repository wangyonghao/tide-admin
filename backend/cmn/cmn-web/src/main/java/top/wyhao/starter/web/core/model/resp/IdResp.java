
package top.wyhao.starter.web.core.model.resp;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

/**
 * ID 响应参数
 *
 * @author Charles7c
 * @since 2.5.0
 */
public class IdResp<T extends Serializable> implements Serializable {

    /**
     * ID
     */
    @Schema(description = "ID", example = "1")
    private T id;

    public T getId() {
        return id;
    }

    public void setId(T id) {
        this.id = id;
    }

    public IdResp() {
    }

    public IdResp(final T id) {
        this.id = id;
    }
}
