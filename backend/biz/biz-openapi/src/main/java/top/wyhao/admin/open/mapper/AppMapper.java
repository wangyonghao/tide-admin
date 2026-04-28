
package top.wyhao.admin.open.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import top.wyhao.admin.open.model.entity.AppDO;
import top.wyhao.starter.data.mapper.BaseMapper;
import top.wyhao.starter.encrypt.field.annotation.FieldEncrypt;

/**
 * 应用 Mapper
 *
 * @author chengzi
 * @since 2024/10/17 16:03
 */
@Mapper
public interface AppMapper extends BaseMapper<AppDO> {

    /**
     * 根据 Access Key 查询
     *
     * @param accessKey Access Key
     * @return 应用信息
     */
    @Select("select * from sys_app where access_key = #{accessKey}")
    AppDO selectByAccessKey(@FieldEncrypt @Param("accessKey") String accessKey);
}