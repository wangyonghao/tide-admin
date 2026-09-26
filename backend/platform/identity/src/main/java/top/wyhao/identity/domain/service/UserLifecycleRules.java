package top.wyhao.identity.domain.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import top.wyhao.cmn.core.constant.StringConstants;
import top.wyhao.cmn.core.enums.StatusEnum;
import top.wyhao.cmn.core.util.CollUtils;
import top.wyhao.identity.domain.exception.UserException;
import top.wyhao.identity.domain.gateway.UserRepository;
import top.wyhao.identity.domain.model.SysUser;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * 用户生命周期不变量：存在性、内置约束、联系方式变更。
 */
@Component
@RequiredArgsConstructor
public class UserLifecycleRules {

    private final UserRepository userRepository;

    public SysUser requireUser(Long id) {
        SysUser user = userRepository.findById(id);
        if (user == null) {
            throw UserException.notFound();
        }
        return user;
    }

    /**
     * 管理端更新前的约束。行为与原先 UserServiceImpl.update 一致。
     */
    public void assertAdminUpdateAllowed(SysUser existing, boolean changingRoles) {
        if (StatusEnum.DISABLE.getValue().equals(existing.getStatus()) && Boolean.TRUE.equals(existing.getIsBuiltin())) {
            throw UserException.disableNotAllowed();
        }
        if (changingRoles) {
            throw UserException.roleChangeNotAllowed();
        }
    }

    public void assertDeletable(List<Long> ids, Long operatorId, List<SysUser> foundUsers) {
        if (CollUtil.contains(ids, operatorId)) {
            throw UserException.deleteSelfNotAllowed();
        }
        List<Long> foundIds = CollUtils.mapToList(foundUsers, SysUser::getId);
        Collection<Long> missingIds = CollUtil.subtract(ids, foundIds);
        if (ObjectUtil.isNotEmpty(missingIds)) {
            throw UserException.notFound(CollUtil.join(missingIds, StringConstants.COMMA));
        }
        Optional<SysUser> builtinUser = foundUsers.stream().filter(SysUser::getIsBuiltin).findFirst();
        if (builtinUser.isPresent()) {
            throw UserException.builtinDeleteNotAllowed(builtinUser.get().getNickname());
        }
    }

    public void assertPhoneChanged(String oldPhone, String newPhone) {
        if (ObjectUtil.equal(newPhone, oldPhone)) {
            throw UserException.phoneSameAsOld();
        }
    }

    public void assertEmailChanged(String oldEmail, String newEmail) {
        if (ObjectUtil.equal(newEmail, oldEmail)) {
            throw UserException.emailSameAsOld();
        }
    }
}
