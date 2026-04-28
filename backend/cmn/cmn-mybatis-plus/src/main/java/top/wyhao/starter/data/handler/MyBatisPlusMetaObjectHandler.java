
package top.wyhao.starter.data.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import top.wyhao.common.security.util.LoginUtil;

import java.time.LocalDateTime;

/**
 * MyBatis Plus 元对象处理器配置（插入或修改时自动填充）
 *
 * @author Charles7c
 * @since 2022/12/22 19:52
 */
public class MyBatisPlusMetaObjectHandler implements MetaObjectHandler {

    /**
     * 创建人
     */
    private static final String CREATE_USER = "createUser";
    /**
     * 创建时间
     */
    private static final String CREATE_TIME = "createTime";
    /**
     * 修改人
     */
    private static final String UPDATE_USER = "updateUser";
    /**
     * 修改时间
     */
    private static final String UPDATE_TIME = "updateTime";

    /**
     * 插入数据时填充
     *
     * @param metaObject 元对象
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        if (metaObject == null) {
            return;
        }
        if (metaObject.hasSetter(CREATE_USER)) {
            this.setFieldValByName(CREATE_USER, LoginUtil.getUserId(), metaObject);
        }
        if (metaObject.hasSetter(CREATE_TIME)) {
            this.setFieldValByName(CREATE_TIME, LocalDateTime.now(), metaObject);
        }
    }

    /**
     * 修改数据时填充
     *
     * @param metaObject 元对象
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        if (metaObject == null) {
            return;
        }
        if (metaObject.hasSetter(UPDATE_USER)) {
            this.setFieldValByName(UPDATE_USER, LoginUtil.getUserId(), metaObject);
        }
        if (metaObject.hasSetter(UPDATE_TIME)) {
            this.setFieldValByName(UPDATE_TIME, LocalDateTime.now(), metaObject);
        }
    }

    /**
     * 填充字段值
     *
     * @param metaObject     元数据对象
     * @param fieldName      要填充的字段名
     * @param fillFieldValue 要填充的字段值
     * @param isOverride     如果字段值不为空，是否覆盖（true：覆盖；false：不覆盖）
     */
    private void fillFieldValue(MetaObject metaObject, String fieldName, Object fillFieldValue, boolean isOverride) {
        if (!metaObject.hasSetter(fieldName)) {
            return;
        }
        Object fieldValue = metaObject.getValue(fieldName);
        setFieldValByName(fieldName, fieldValue != null && !isOverride ? fieldValue : fillFieldValue, metaObject);
    }
}
