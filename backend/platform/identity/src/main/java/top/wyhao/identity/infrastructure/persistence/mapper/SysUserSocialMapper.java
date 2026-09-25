
package top.wyhao.identity.infrastructure.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.wyhao.identity.domain.model.SysUserSocial;
import top.wyhao.cmn.db.model.BaseMapper;

/**
 * 用户绑定的每三方平台账号 Mapper
 *

 * @since 2023/10/11 22:10
 */
@Mapper
public interface SysUserSocialMapper extends BaseMapper<SysUserSocial> {

    /**
     * 根据来源和开放 ID 查询
     *
     * @param source 来源
     * @param openId 开放 ID
     * @return 用户社会化关联信息
     */
    SysUserSocial selectBySourceAndOpenId(@Param("source") String source, @Param("openId") String openId);
}
