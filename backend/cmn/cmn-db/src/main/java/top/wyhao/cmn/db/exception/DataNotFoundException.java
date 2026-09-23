package top.wyhao.cmn.db.exception;

import cn.hutool.core.text.CharSequenceUtil;
import top.wyhao.starter.core.constant.StringConstants;
import top.wyhao.starter.core.exception.BizException;

/**
 * 数据不存在异常
 */
public class DataNotFoundException extends BizException {

    public DataNotFoundException(String message) {
        super(message);
    }

    public DataNotFoundException(String code, String message) {
        super(code, message);
    }

    public static DataNotFoundException of(String entityName, Object id) {
        return new DataNotFoundException("DATA_NOT_FOUND", "%s 为 [%s] 的 %s 记录已不存在"
            .formatted("ID", id, CharSequenceUtil.replace(entityName, "DO", StringConstants.EMPTY)));
    }
}
