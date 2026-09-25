package top.wyhao.identity.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.wyhao.identity.entity.SysLoginLog;
import top.wyhao.cmn.db.model.BaseMapper;

/**
 * 登录日志 Mapper
 *

 * @since 2026/05/08
 */
@Mapper
public interface SysLoginLogMapper extends BaseMapper<SysLoginLog> {
}
