package top.wyhao.identity.infrastructure.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.wyhao.cmn.db.model.BaseMapper;
import top.wyhao.identity.domain.model.SysMembership;

/**
 * 成员关系 Mapper。
 */
@Mapper
public interface SysMembershipMapper extends BaseMapper<SysMembership> {
}
