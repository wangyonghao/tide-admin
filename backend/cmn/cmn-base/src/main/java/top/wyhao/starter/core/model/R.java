/*
 * Copyright (c) 2022-present wangyonghao Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
