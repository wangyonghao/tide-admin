package top.wyhao.admin.system.identity.app.assembler;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import top.wyhao.admin.system.identity.app.dto.UserDetail;
import top.wyhao.admin.system.identity.app.dto.UserRequest;
import top.wyhao.admin.system.identity.domain.model.SysUser;
import top.wyhao.cmn.identity.LoginUser;
import top.wyhao.web.convert.BaseEnumConverters;
import top.wyhao.web.convert.MapStructConfig;

/**
 * 用户对象转换
 */
@Mapper(config = MapStructConfig.class, uses = BaseEnumConverters.class)
public interface UserAssembler {

    @Mapping(source = "pwdUpdateTime", target = "pwdResetTime")
    UserDetail toDetail(SysUser user);

    SysUser toEntity(UserRequest request);

    @Mapping(source = "pwdResetTime", target = "pwdUpdateTime")
    SysUser toEntity(UserDetail detail);

    @Mapping(source = "id", target = "userId")
    @Mapping(source = "pwdUpdateTime", target = "pwdResetTime")
    LoginUser toLoginUser(SysUser user);
}
