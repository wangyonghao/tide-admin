package top.wyhao.security.infrastructure.persistence;

import cn.hutool.core.text.CharSequenceUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import top.wyhao.cmn.db.query.PageResult;
import top.wyhao.security.domain.gateway.AuthenticationLogRepository;
import top.wyhao.security.domain.model.AuthenticationLogCriteria;
import top.wyhao.security.domain.model.SysAuthenticationLog;
import top.wyhao.security.infrastructure.persistence.mapper.SysAuthenticationLogMapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 登录日志仓储实现。
 */
@Repository
@RequiredArgsConstructor
public class AuthenticationLogRepositoryImpl implements AuthenticationLogRepository {

    private final SysAuthenticationLogMapper authenticationLogMapper;

    @Override
    public void insert(SysAuthenticationLog loginLog) {
        authenticationLogMapper.insert(loginLog);
    }

    @Override
    public PageResult<SysAuthenticationLog> page(AuthenticationLogCriteria criteria, long page, long pageSize) {
        IPage<SysAuthenticationLog> result = authenticationLogMapper.selectPage(new Page<>(page, pageSize), wrapper(criteria));
        return PageResult.of(result);
    }

    @Override
    public List<SysAuthenticationLog> list(AuthenticationLogCriteria criteria) {
        return authenticationLogMapper.selectList(wrapper(criteria));
    }

    @Override
    public int deleteBefore(LocalDateTime expireTime) {
        return authenticationLogMapper.delete(new LambdaQueryWrapper<SysAuthenticationLog>().lt(SysAuthenticationLog::getLoginTime, expireTime));
    }

    private LambdaQueryWrapper<SysAuthenticationLog> wrapper(AuthenticationLogCriteria criteria) {
        LambdaQueryWrapper<SysAuthenticationLog> queryWrapper = new LambdaQueryWrapper<>();
        if (CharSequenceUtil.isNotBlank(criteria.getUsername())) {
            queryWrapper.like(SysAuthenticationLog::getUsername, criteria.getUsername());
        }
        if (CharSequenceUtil.isNotBlank(criteria.getIpAddress())) {
            queryWrapper.like(SysAuthenticationLog::getIpAddress, criteria.getIpAddress());
        }
        if (criteria.getLoginStatus() != null) {
            queryWrapper.eq(SysAuthenticationLog::getLoginStatus, criteria.getLoginStatus());
        }
        if (criteria.getLoginTimeStart() != null) {
            queryWrapper.ge(SysAuthenticationLog::getLoginTime, criteria.getLoginTimeStart());
        }
        if (criteria.getLoginTimeEnd() != null) {
            queryWrapper.le(SysAuthenticationLog::getLoginTime, criteria.getLoginTimeEnd());
        }
        if (criteria.isTenantRestricted()) {
            queryWrapper.eq(SysAuthenticationLog::getTenantId, criteria.getTenantId());
        }
        queryWrapper.orderByDesc(SysAuthenticationLog::getLoginTime);
        return queryWrapper;
    }
}
