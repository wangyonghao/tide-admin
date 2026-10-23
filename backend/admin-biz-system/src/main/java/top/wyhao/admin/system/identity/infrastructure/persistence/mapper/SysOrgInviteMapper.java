package top.wyhao.admin.system.identity.infrastructure.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.wyhao.mybatis.model.BaseMapper;
import top.wyhao.admin.system.identity.domain.model.SysOrgInvite;

@Mapper
public interface SysOrgInviteMapper extends BaseMapper<SysOrgInvite> {
}
