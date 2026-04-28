
package top.wyhao.starter.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 响应信息
 *
 * @author wyhao
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
public class R<T> {
    /**
     * 状态码
     */
    private String code;

    /**
     * 状态信息
     */
    private String msg;

    /**
     * 响应数据
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;
    /**
     * 时间戳
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long timestamp;

    /**
     * 操作成功
     *
     * @return R /
     */
    public static <T> R<T> ok() {
        return ok(null);
    }

    /**
     * 操作成功
     *
     * @param data 响应数据
     * @return R /
     */
    public static <T> R<T> ok(T data) {
        return ok(data, "操作成功");
    }

    /**
     * 操作成功
     *
     * @param msg  业务状态信息
     * @param data 响应数据
     * @return R /
     */
    public static <T> R<T> ok(T data, String msg) {
        return of(data, "200", msg);
    }

    /**
     * 操作失败
     *
     * @param code 业务状态码
     * @param msg  业务状态信息
     * @return R /
     */
    public static <T> R<T> fail(String code, String msg) {
        return of(null, code, msg);
    }

    private static <T> R<T> of(T data, String code, String msg) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setData(data);
        r.setMsg(msg);
        r.setTimestamp(System.currentTimeMillis());
        return r;
    }

    @JsonIgnore
    public boolean isOk() {
        return "200".equals(code);
    }
}
