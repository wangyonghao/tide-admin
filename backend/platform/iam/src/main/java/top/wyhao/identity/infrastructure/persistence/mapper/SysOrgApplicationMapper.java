package top.wyhao.identity.infrastructure.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.wyhao.cmn.db.model.BaseMapper;
import top.wyhao.identity.domain.model.SysOrgApplication;

@Mapper
public interface SysOrgApplicationMapper extends BaseMapper<SysOrgApplication> {
}
