package top.wyhao.admin.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.wyhao.admin.system.model.entity.LoginLogDO;
import top.wyhao.starter.data.mapper.BaseMapper;

/**
 * 登录日志 Mapper
 *
 * @author Yonghao Wang
 * @since 2026/05/08
 */
@Mapper
public interface LoginLogMapper extends BaseMapper<LoginLogDO> {
}
