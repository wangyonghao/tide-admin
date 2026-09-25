package top.wyhao.identity.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import top.wyhao.cmn.db.query.PageFactory;
import top.wyhao.cmn.db.query.PageParam;
import top.wyhao.cmn.db.query.PageResult;
import top.wyhao.cmn.db.query.QueryWrapperBuilder;
import top.wyhao.identity.adapter.web.dto.UserQuery;
import top.wyhao.identity.adapter.web.vo.UserDetail;
import top.wyhao.identity.adapter.web.vo.UserResult;
import top.wyhao.identity.app.query.UserViewQuery;
import top.wyhao.identity.domain.model.SysUser;
import top.wyhao.identity.infrastructure.persistence.mapper.SysUserMapper;

import java.util.List;

/**
 * 用户列表读模型实现。数据权限仍由 Mapper 上的注解生效。
 */
@Repository
@RequiredArgsConstructor
public class UserViewQueryImpl implements UserViewQuery {

    private final SysUserMapper userMapper;

    @Override
    public PageResult<UserResult> page(UserQuery query, PageParam pageParam) {
        LambdaQueryWrapper<SysUser> wrapper = QueryWrapperBuilder.build(query, SysUser.class);
        IPage<UserResult> page = userMapper.selectUserPage(PageFactory.build(pageParam, query, SysUser.class), wrapper);
        return PageResult.of(page);
    }

    @Override
    public List<UserDetail> list(UserQuery query) {
        return userMapper.selectUserList(QueryWrapperBuilder.build(query, SysUser.class));
    }
}
