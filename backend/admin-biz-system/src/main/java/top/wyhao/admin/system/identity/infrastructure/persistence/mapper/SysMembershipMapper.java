package top.wyhao.admin.system.identity.infrastructure.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.wyhao.mybatis.model.BaseMapper;
import top.wyhao.admin.system.identity.domain.model.SysMembership;

/**
 * 成员关系 Mapper。
 */
@Mapper
public interface SysMembershipMapper extends BaseMapper<SysMembership> {
}
