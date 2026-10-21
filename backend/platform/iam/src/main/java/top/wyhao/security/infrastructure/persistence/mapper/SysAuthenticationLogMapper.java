package top.wyhao.security.infrastructure.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.wyhao.security.domain.model.SysAuthenticationLog;
import top.wyhao.mybatis.model.BaseMapper;

/**
 * 登录日志 Mapper
 *

 * @since 2026/05/08
 */
@Mapper
public interface SysAuthenticationLogMapper extends BaseMapper<SysAuthenticationLog> {
}
