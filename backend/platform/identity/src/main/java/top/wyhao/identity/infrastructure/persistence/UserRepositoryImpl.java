package top.wyhao.identity.infrastructure.persistence;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import top.wyhao.identity.domain.gateway.UserRepository;
import top.wyhao.identity.domain.model.SysUser;
import top.wyhao.identity.infrastructure.persistence.mapper.SysUserMapper;
import top.wyhao.cmn.core.util.CollUtils;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * 用户仓储实现。
 */
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final SysUserMapper userMapper;

    @Override
    public SysUser findById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public SysUser findByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public SysUser findByPhone(String phone) {
        return userMapper.selectByPhone(phone);
    }

    @Override
    public SysUser findByEmail(String email) {
        return userMapper.selectByEmail(email);
    }

    @Override
    public void insert(SysUser user) {
        userMapper.insert(user);
    }

    @Override
    public void insertBatch(List<SysUser> users) {
        userMapper.insert(users);
    }

    @Override
    public void updateById(SysUser user) {
        userMapper.updateById(user);
    }

    @Override
    public void updateBatch(List<SysUser> users) {
        userMapper.updateBatchById(users);
    }

    @Override
    public void updatePartial(SysUser user) {
        userMapper.lambdaUpdate().update(user);
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        userMapper.deleteByIds(ids);
    }

    @Override
    public List<SysUser> listBriefByIds(List<Long> ids) {
        return userMapper.lambdaQuery()
                .select(SysUser::getId, SysUser::getNickname, SysUser::getIsBuiltin)
                .in(SysUser::getId, ids)
                .list();
    }

    @Override
    public void updatePasswordAndExpire(Long id, String encodedPassword, LocalDateTime pwdExpireDate) {
        userMapper.lambdaUpdate()
                .set(SysUser::getPassword, encodedPassword)
                .set(SysUser::getPwdExpireDate, pwdExpireDate)
                .eq(SysUser::getId, id)
                .update();
    }

    @Override
    public void updateAvatar(Long id, Long avatarId) {
        userMapper.lambdaUpdate()
                .set(SysUser::getAvatar, avatarId)
                .eq(SysUser::getId, id)
                .update();
    }

    @Override
    public void updateBasicInfo(Long id, String nickname, Object gender) {
        userMapper.lambdaUpdate()
                .set(SysUser::getNickname, nickname)
                .set(SysUser::getGender, gender)
                .eq(SysUser::getId, id)
                .update();
    }

    @Override
    public void updatePassword(Long id, String encodedPassword, LocalDateTime pwdUpdateTime) {
        userMapper.lambdaUpdate()
                .set(SysUser::getPassword, encodedPassword)
                .set(SysUser::getPwdUpdateTime, pwdUpdateTime)
                .eq(SysUser::getId, id)
                .update();
    }

    @Override
    public long countByDeptIds(Collection<Long> deptIds) {
        return userMapper.lambdaQuery().in(SysUser::getDeptId, deptIds).count();
    }

    @Override
    public List<String> findRoleCodesByUserId(Long userId) {
        return userMapper.selectRoleCodesByUserId(userId);
    }

    @Override
    public boolean existsUsername(String username) {
        return userMapper.lambdaQuery().eq(SysUser::getUsername, username).exists();
    }

    @Override
    public boolean existsEmail(String email, Long excludeUserId) {
        return userMapper.lambdaQuery()
                .eq(SysUser::getEmail, email)
                .ne(excludeUserId != null, SysUser::getId, excludeUserId)
                .exists();
    }

    @Override
    public boolean existsPhone(String phone, Long excludeUserId) {
        return userMapper.lambdaQuery()
                .eq(SysUser::getPhone, phone)
                .ne(excludeUserId != null, SysUser::getId, excludeUserId)
                .exists();
    }

    @Override
    public int countByUsernames(Collection<String> usernames) {
        return Math.toIntExact(userMapper.lambdaQuery().in(SysUser::getUsername, usernames).count());
    }

    @Override
    public int countByEmails(Collection<String> emails) {
        return Math.toIntExact(userMapper.lambdaQuery().in(SysUser::getEmail, emails).count());
    }

    @Override
    public int countByPhones(Collection<String> phones) {
        return Math.toIntExact(userMapper.lambdaQuery().in(SysUser::getPhone, phones).count());
    }

    @Override
    public List<String> listEmailsIn(Collection<String> emails) {
        List<SysUser> users = userMapper.lambdaQuery()
                .select(SysUser::getEmail)
                .in(SysUser::getEmail, emails)
                .list();
        return CollUtils.mapToList(users, SysUser::getEmail);
    }

    @Override
    public List<String> listPhonesIn(Collection<String> phones) {
        List<SysUser> users = userMapper.lambdaQuery()
                .select(SysUser::getPhone)
                .in(SysUser::getPhone, phones)
                .list();
        return CollUtils.mapToList(users, SysUser::getPhone);
    }

    @Override
    public List<SysUser> listByUsernames(Collection<String> usernames) {
        return userMapper.lambdaQuery()
                .in(SysUser::getUsername, usernames)
                .select(SysUser::getId, SysUser::getUsername)
                .list();
    }

    @Override
    public List<SysUser> listAll() {
        return userMapper.selectList(null);
    }

    @Override
    public void deleteAll() {
        userMapper.delete(Wrappers.<SysUser>query().eq("1", 1));
    }

    @Override
    public long countExcluding(Collection<Long> keepIds) {
        return userMapper.lambdaQuery().notIn(SysUser::getId, keepIds).count();
    }

    @Override
    public boolean deleteExcluding(Collection<Long> keepIds) {
        return userMapper.lambdaUpdate().notIn(SysUser::getId, keepIds).remove();
    }
}
