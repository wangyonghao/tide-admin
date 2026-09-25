package top.wyhao.identity.domain.gateway;

import top.wyhao.identity.domain.model.SysUser;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * 用户仓储。
 */
public interface UserRepository {

    SysUser findById(Long id);

    SysUser findByUsername(String username);

    SysUser findByPhone(String phone);

    SysUser findByEmail(String email);

    void insert(SysUser user);

    void insertBatch(List<SysUser> users);

    void updateById(SysUser user);

    void updateBatch(List<SysUser> users);

    /**
     * 按实体非空字段更新，条件取主键。
     */
    void updatePartial(SysUser user);

    void deleteByIds(List<Long> ids);

    List<SysUser> listBriefByIds(List<Long> ids);

    void updatePasswordAndExpire(Long id, String encodedPassword, LocalDateTime pwdExpireDate);

    void updateAvatar(Long id, Long avatarId);

    void updateBasicInfo(Long id, String nickname, Object gender);

    void updatePassword(Long id, String encodedPassword, LocalDateTime pwdUpdateTime);

    long countByDeptIds(Collection<Long> deptIds);

    List<String> findRoleCodesByUserId(Long userId);

    boolean existsUsername(String username);

    boolean existsEmail(String email, Long excludeUserId);

    boolean existsPhone(String phone, Long excludeUserId);

    int countByUsernames(Collection<String> usernames);

    int countByEmails(Collection<String> emails);

    int countByPhones(Collection<String> phones);

    List<String> listEmailsIn(Collection<String> emails);

    List<String> listPhonesIn(Collection<String> phones);

    List<SysUser> listByUsernames(Collection<String> usernames);

    List<SysUser> listAll();

    void deleteAll();

    long countExcluding(Collection<Long> keepIds);

    boolean deleteExcluding(Collection<Long> keepIds);
}
