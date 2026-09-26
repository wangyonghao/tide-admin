package top.wyhao.identity.infrastructure.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.wyhao.cmn.db.model.BaseMapper;
import top.wyhao.identity.domain.model.SysUserPasswordHistory;

/**
 * 用户历史密码 Mapper
 */
@Mapper
public interface SysUserPasswordHistoryMapper extends BaseMapper<SysUserPasswordHistory> {

    /**
     * 删除过期历史密码，仅保留最近 count 条。
     */
    void deleteExpired(@Param("userId") Long userId, @Param("count") int count);
}
