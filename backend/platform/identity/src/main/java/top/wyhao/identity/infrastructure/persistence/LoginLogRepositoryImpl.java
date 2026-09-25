package top.wyhao.identity.infrastructure.persistence;

import cn.hutool.core.text.CharSequenceUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import top.wyhao.cmn.db.query.PageResult;
import top.wyhao.identity.domain.gateway.LoginLogRepository;
import top.wyhao.identity.domain.model.LoginLogCriteria;
import top.wyhao.identity.domain.model.SysLoginLog;
import top.wyhao.identity.infrastructure.persistence.mapper.SysLoginLogMapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 登录日志仓储实现。
 */
@Repository
@RequiredArgsConstructor
public class LoginLogRepositoryImpl implements LoginLogRepository {

    private final SysLoginLogMapper loginLogMapper;

    @Override
    public void insert(SysLoginLog loginLog) {
        loginLogMapper.insert(loginLog);
    }

    @Override
    public PageResult<SysLoginLog> page(LoginLogCriteria criteria, long page, long pageSize) {
        IPage<SysLoginLog> result = loginLogMapper.selectPage(new Page<>(page, pageSize), wrapper(criteria));
        return PageResult.of(result);
    }

    @Override
    public List<SysLoginLog> list(LoginLogCriteria criteria) {
        return loginLogMapper.selectList(wrapper(criteria));
    }

    @Override
    public int deleteBefore(LocalDateTime expireTime) {
        return loginLogMapper.delete(new LambdaQueryWrapper<SysLoginLog>().lt(SysLoginLog::getLoginTime, expireTime));
    }

    private LambdaQueryWrapper<SysLoginLog> wrapper(LoginLogCriteria criteria) {
        LambdaQueryWrapper<SysLoginLog> queryWrapper = new LambdaQueryWrapper<>();
        if (CharSequenceUtil.isNotBlank(criteria.getUsername())) {
            queryWrapper.like(SysLoginLog::getUsername, criteria.getUsername());
        }
        if (CharSequenceUtil.isNotBlank(criteria.getIpAddress())) {
            queryWrapper.like(SysLoginLog::getIpAddress, criteria.getIpAddress());
        }
        if (criteria.getLoginStatus() != null) {
            queryWrapper.eq(SysLoginLog::getLoginStatus, criteria.getLoginStatus());
        }
        if (criteria.getLoginTimeStart() != null) {
            queryWrapper.ge(SysLoginLog::getLoginTime, criteria.getLoginTimeStart());
        }
        if (criteria.getLoginTimeEnd() != null) {
            queryWrapper.le(SysLoginLog::getLoginTime, criteria.getLoginTimeEnd());
        }
        if (criteria.isTenantRestricted()) {
            queryWrapper.eq(SysLoginLog::getTenantId, criteria.getTenantId());
        }
        queryWrapper.orderByDesc(SysLoginLog::getLoginTime);
        return queryWrapper;
    }
}
