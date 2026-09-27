package top.wyhao.identity.app.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.validation.ValidationUtil;
import cn.hutool.json.JSONUtil;
import cn.idev.excel.FastExcelFactory;
import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheUpdate;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ahoo.cosid.IdGenerator;
import me.ahoo.cosid.provider.DefaultIdGeneratorProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import top.wyhao.cmn.core.constant.CacheConstants;
import top.wyhao.cmn.core.constant.RegexConstants;
import top.wyhao.cmn.core.enums.GenderEnum;
import top.wyhao.cmn.core.enums.StatusEnum;
import top.wyhao.cmn.core.util.CollUtils;
import top.wyhao.cmn.core.util.ExceptionUtils;
import top.wyhao.cmn.core.util.RsaUtils;
import top.wyhao.cmn.db.query.PageParam;
import top.wyhao.cmn.db.query.PageResult;
import top.wyhao.common.security.util.LoginUtil;
import top.wyhao.file.domain.model.File;
import top.wyhao.file.app.service.FileService;
import top.wyhao.identity.adapter.web.dto.*;
import top.wyhao.identity.adapter.web.vo.UserDetail;
import top.wyhao.identity.adapter.web.vo.UserImportParseResp;
import top.wyhao.identity.adapter.web.vo.UserImportResp;
import top.wyhao.identity.adapter.web.vo.UserResult;
import top.wyhao.identity.app.assembler.UserAssembler;
import top.wyhao.identity.app.query.UserViewQuery;
import top.wyhao.identity.app.service.UserService;
import top.wyhao.identity.app.service.UserSocialService;
import top.wyhao.identity.client.PasswordApi;
import top.wyhao.identity.domain.exception.UserException;
import top.wyhao.identity.domain.gateway.UserRepository;
import top.wyhao.identity.domain.model.SysUser;
import top.wyhao.identity.domain.service.UserImportPolicy;
import top.wyhao.identity.domain.service.UserLifecycleRules;
import top.wyhao.identity.domain.service.UserUniquenessChecker;
import top.wyhao.organization.client.DeptApi;
import top.wyhao.security.client.RoleApi;
import top.wyhao.identity.client.PasswordPolicyConfig;
import top.wyhao.identity.client.PasswordPolicyConfigApi;
import top.wyhao.starter.cache.redisson.util.RedisUtils;
import top.wyhao.starter.excel.util.ExcelUtils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 用户业务实现：编排跨域能力与领域规则。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserSocialService userSocialService;
    private final RoleApi roleApi;
    private final DeptApi deptApi;
    private final PasswordApi passwordApi;
    private final FileService fileService;
    private final UserRepository userRepository;
    private final UserViewQuery userViewQuery;
    private final PasswordPolicyConfigApi passwordPolicyConfigApi;
    private final UserAssembler userAssembler;
    private final UserUniquenessChecker uniquenessChecker;
    private final UserLifecycleRules lifecycleRules;
    private final UserImportPolicy importPolicy;

    @Value("${avatar.support-suffix}")
    private String[] avatarSupportSuffix;
    private static final long avatarMaxSize = 1024 * 1024 * 2;;

    @Override
    public UserDetail detail(Long id) {
        return userAssembler.toDetail(lifecycleRules.requireUser(id));
    }

    @Override
    public PageResult<UserResult> page(UserQuery query, PageParam pageParam) {
        return userViewQuery.page(query, pageParam);
    }

    @Transactional(rollbackFor = Exception.class)
    public Long create(UserRequest request) {
        String rawPassword = ExceptionUtils.exToNull(() -> RsaUtils.decryptByRsaPrivateKey(request.getPassword()));
        if (CharSequenceUtil.isBlank(rawPassword)) {
            throw UserException.passwordDecryptFailed();
        }
        if (!ReUtil.isMatch(RegexConstants.PASSWORD, rawPassword)) {
            throw UserException.passwordFormatInvalid();
        }
        uniquenessChecker.assertEmailAvailable(request.getEmail(), null);
        uniquenessChecker.assertPhoneAvailable(request.getPhone(), null);
        uniquenessChecker.assertUsernameAvailable(request.getUsername());

        SysUser newUser = userAssembler.toEntity(request);
        newUser.setPassword(passwordEncoder.encode(rawPassword));
        PasswordPolicyConfig passwordPolicy = passwordPolicyConfigApi.get();
        int expireDays = passwordPolicy != null && passwordPolicy.getPasswordExpireDays() != null
            ? passwordPolicy.getPasswordExpireDays()
            : 90;
        newUser.setPwdExpireDate(LocalDate.now().plusDays(expireDays));
        userRepository.insert(newUser);

        roleApi.assignRolesToUser(request.getRoleIds(), newUser.getId());
        return newUser.getId();
    }

    @Override
    public Long save(SysUser user) {
        userRepository.insert(user);
        return user.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheUpdate(key = "#userId", value = "#userBO.nickname", name = CacheConstants.USER_KEY_PREFIX)
    public void update(Long userId, UserRequest userRequest) {
        SysUser oldUser = lifecycleRules.requireUser(userId);
        lifecycleRules.assertAdminUpdateAllowed(oldUser, CollUtil.isNotEmpty(userRequest.getRoleIds()));
        if (StrUtil.isNotBlank(userRequest.getEmail())) {
            uniquenessChecker.assertEmailAvailable(userRequest.getEmail(), userId);
        }
        if (StrUtil.isNotBlank(userRequest.getPhone())) {
            uniquenessChecker.assertPhoneAvailable(userRequest.getPhone(), userId);
        }

        SysUser updateUser = userAssembler.toEntity(userRequest);
        updateUser.setId(userId);
        userRepository.updateById(updateUser);
        roleApi.assignRolesToUser(userRequest.getRoleIds(), userId);

        if (StatusEnum.DISABLE.equals(userRequest.getStatus())) {
            LoginUtil.kickout(userId);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(key = "#ids", name = CacheConstants.USER_KEY_PREFIX, multi = true)
    public void delete(List<Long> ids) {
        List<SysUser> list = userRepository.listBriefByIds(ids);
        lifecycleRules.assertDeletable(ids, LoginUtil.getUserId(), list);

        roleApi.deleteUserRolesByUserIds(ids);
        passwordApi.deleteByUserIds(ids);
        userSocialService.deleteByUserIds(ids);
        userRepository.deleteByIds(ids);
        ids.forEach(LoginUtil::kickout);
    }

    @Override
    public UserImportParseResp parseImport(MultipartFile file) {
        UserImportParseResp userImportResp = new UserImportParseResp();
        List<UserImportRowReq> importRowList;
        try {
            importRowList = FastExcelFactory.read(file.getInputStream())
                    .head(UserImportRowReq.class)
                    .sheet()
                    .headRowNumber(1)
                    .doReadSync();
        } catch (Exception e) {
            log.error("用户导入数据文件解析异常：{}", e.getMessage(), e);
            throw UserException.importFormatError();
        }
        userImportResp.setTotalRows(importRowList.size());
        if (ObjectUtil.isEmpty(importRowList)) {
            throw UserException.importDataInvalid();
        }
        List<UserImportRowReq> validRowList = this.filterImportData(importRowList);
        userImportResp.setValidRows(validRowList.size());
        if (ObjectUtil.isEmpty(validRowList)) {
            throw UserException.importDataInvalid();
        }

        Set<String> seenEmails = new HashSet<>();
        boolean hasDuplicateEmail = validRowList.stream()
                .map(UserImportRowReq::getEmail)
                .anyMatch(email -> email != null && !seenEmails.add(email));
        if (hasDuplicateEmail) {
            throw UserException.importEmailDuplicate();
        }
        Set<String> seenPhones = new HashSet<>();
        boolean hasDuplicatePhone = validRowList.stream()
                .map(UserImportRowReq::getPhone)
                .anyMatch(phone -> phone != null && !seenPhones.add(phone));
        if (hasDuplicatePhone) {
            throw UserException.importPhoneDuplicate();
        }

        List<String> roleNames = validRowList.stream().map(UserImportRowReq::getRoleName).distinct().toList();
        int existRoleCount = roleApi.countByNames(roleNames);
        if (existRoleCount < roleNames.size()) {
            throw UserException.importRoleInvalid();
        }
        Set<String> deptNames = CollUtils.mapToSet(validRowList, UserImportRowReq::getDeptName);
        int existDeptCount = deptApi.countValidDeptPaths(deptNames);
        if (existDeptCount < deptNames.size()) {
            throw UserException.importDeptInvalid();
        }

        userImportResp.setDuplicateUserRows(countByValues(validRowList, UserImportRowReq::getUsername, userRepository::countByUsernames));
        userImportResp.setDuplicateEmailRows(countByValues(validRowList, UserImportRowReq::getEmail, userRepository::countByEmails));
        userImportResp.setDuplicatePhoneRows(countByValues(validRowList, UserImportRowReq::getPhone, userRepository::countByPhones));

        String importKey = UUID.fastUUID().toString(true);
        RedisUtils.set(CacheConstants.DATA_IMPORT_KEY + importKey, JSONUtil.toJsonStr(validRowList), Duration
                .ofMinutes(10));
        userImportResp.setImportKey(importKey);
        return userImportResp;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserImportResp importUser(UserImportRequest req) {
        List<UserImportRowReq> importUserList;
        try {
            String data = RedisUtils.get(CacheConstants.DATA_IMPORT_KEY + req.getImportKey());
            importUserList = JSONUtil.toList(data, UserImportRowReq.class);
            if (CollUtil.isEmpty(importUserList)) {
                throw UserException.importExpired();
            }
        } catch (Exception e) {
            log.error("导入异常:", e);
            throw UserException.importExpired();
        }
        List<String> existEmails = listByValues(importUserList, UserImportRowReq::getEmail, userRepository::listEmailsIn);
        List<String> existPhones = listByValues(importUserList, UserImportRowReq::getPhone, userRepository::listPhonesIn);
        List<SysUser> existUserList = userRepository.listByUsernames(CollUtils
                .mapToList(importUserList, UserImportRowReq::getUsername));
        List<String> existUsernames = CollUtils.mapToList(existUserList, SysUser::getUsername);
        List<UserImportPolicy.ContactRow> contactRows = importUserList.stream()
                .map(row -> new UserImportPolicy.ContactRow(row.getUsername(), row.getEmail(), row.getPhone()))
                .toList();
        if (importPolicy.shouldAbort(req.getDuplicateUser(), req.getDuplicateEmail(), req.getDuplicatePhone(),
                contactRows, existUsernames, existEmails, existPhones)) {
            throw UserException.importPolicyViolated();
        }

        Map<String, Long> userMap = existUserList.stream()
                .collect(Collectors.toMap(SysUser::getUsername, SysUser::getId));
        Map<String, Long> roleMap = roleApi.mapIdByNames(importUserList.stream()
                .map(UserImportRowReq::getRoleName)
                .distinct()
                .toList());
        Map<String, Long> deptMap = deptApi.resolveDeptIdsByPaths(importUserList.stream()
                .map(UserImportRowReq::getDeptName)
                .distinct()
                .toList());

        List<SysUser> insertList = new ArrayList<>();
        List<SysUser> updateList = new ArrayList<>();
        List<long[]> userRolePairs = new ArrayList<>();
        IdGenerator idGenerator = DefaultIdGeneratorProvider.INSTANCE.getShare();
        for (UserImportRowReq row : importUserList) {
            if (importPolicy.shouldSkip(req.getDuplicateUser(), req.getDuplicateEmail(), req.getDuplicatePhone(),
                    row.getUsername(), row.getEmail(), row.getPhone(),
                    existUsernames, existEmails, existPhones)) {
                continue;
            }
            SysUser userDO = BeanUtil.toBeanIgnoreError(row, SysUser.class);
            userDO.setStatus(req.getDefaultStatus().getValue());
            userDO.setPwdUpdateTime(LocalDateTime.now());
            userDO.setGender(GenderEnum.getByValue(Integer.parseInt(row.getGender())).getValue());
            userDO.setDeptId(deptMap.get(row.getDeptName()));
            if (importPolicy.shouldUpdateExistingUser(req.getDuplicateUser(), row.getUsername(), existUsernames)) {
                userDO. setId(userMap.get(row.getUsername()));
                updateList.add(userDO);
            } else {
                userDO.setId(idGenerator.generate());
                userDO.setIsBuiltin(false);
                insertList.add(userDO);
            }
            userRolePairs.add(new long[]{userDO.getId(), roleMap.get(row.getRoleName())});
        }
        doImportUser(insertList, updateList, userRolePairs);
        RedisUtils.delete(CacheConstants.DATA_IMPORT_KEY + req.getImportKey());
        return new UserImportResp(insertList.size() + updateList.size(), insertList.size(), updateList.size());
    }

    @Override
    public void export(UserQuery query, HttpServletResponse response) {
        List<UserDetail> userList = userViewQuery.list(query);
        ExcelUtils.export(userList, "用户数据", UserDetail.class, response);
    }

    @Override
    public void updateRole(UserRoleUpdateReq updateReq, Long id) {
        lifecycleRules.requireUser(id);
        roleApi.assignRolesToUser(updateReq.getRoleIds(), id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long updateAvatar(MultipartFile avatarFile, Long userId) {
        checkAvatar(avatarFile);

        UserDetail user = this.detail(userId);
        Long oldAvatarFileId = user.getAvatar();

        File uploaded = fileService.upload(avatarFile, userId);
        userRepository.updateAvatar(userId, uploaded.getId());

        if (oldAvatarFileId != null) {
            try {
                fileService.delete(oldAvatarFileId, userId);
            } catch (Exception e) {
                log.warn("删除旧头像文件失败: fileId={}", oldAvatarFileId, e);
            }
        }

        return uploaded.getId();
    }

    private void checkAvatar(MultipartFile avatarFile) {
        String avatarImageType = FileNameUtil.extName(avatarFile.getOriginalFilename());
        if (!CharSequenceUtil.equalsAnyIgnoreCase(avatarImageType, avatarSupportSuffix)) {
            throw UserException.avatarFormatNotSupported(String.join(",", avatarSupportSuffix));
        }

        long avatarSize = avatarFile.getSize();
        if (avatarSize > avatarMaxSize) {
            throw UserException.avatarSizeExceeded(avatarMaxSize / 1024 / 1024);
        }
    }

    @Override
    @CacheUpdate(key = "#id", value = "#req.nickname", name = CacheConstants.USER_KEY_PREFIX)
    public void updateBasicInfo(UserBasicInfoUpdateReq req, Long id) {
        lifecycleRules.requireUser(id);
        userRepository.updateBasicInfo(id, req.getNickname(), req.getGender());
    }

    @Override
    public void updatePhone(String newPhone, String oldPassword, Long id) {
        passwordApi.assertMatches(id, oldPassword);
        SysUser user = lifecycleRules.requireUser(id);
        lifecycleRules.assertPhoneChanged(user.getPhone(), newPhone);
        uniquenessChecker.assertPhoneAvailable(newPhone, id);
        SysUser updateUser = new SysUser();
        updateUser.setId(id);
        updateUser.setPhone(newPhone);
        userRepository.updatePartial(updateUser);
    }

    @Override
    public void updateEmail(String newEmail, String oldPassword, Long id) {
        SysUser user = lifecycleRules.requireUser(id);
        passwordApi.assertMatches(id, oldPassword);
        lifecycleRules.assertEmailChanged(user.getEmail(), newEmail);
        uniquenessChecker.assertEmailAvailable(newEmail, id);

        SysUser updateUser = new SysUser();
        updateUser.setId(id);
        updateUser.setEmail(newEmail);
        userRepository.updatePartial(updateUser);
    }

    @Override
    public SysUser getByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public SysUser getByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }

    @Override
    public SysUser getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Long countByDeptIds(List<Long> deptIds) {
        if (CollUtil.isEmpty(deptIds)) {
            return 0L;
        }
        return userRepository.countByDeptIds(deptIds);
    }

    private void doImportUser(List<SysUser> insertList, List<SysUser> updateList, List<long[]> userRolePairs) {
        if (CollUtil.isNotEmpty(insertList)) {
            userRepository.insertBatch(insertList);
        }
        if (CollUtil.isNotEmpty(updateList)) {
            userRepository.updateBatch(updateList);
            roleApi.deleteUserRolesByUserIds(CollUtils.mapToList(updateList, SysUser::getId));
        }
        if (CollUtil.isNotEmpty(userRolePairs)) {
            for (long[] pair : userRolePairs) {
                roleApi.assignRolesToUser(List.of(pair[1]), pair[0]);
            }
        }
    }

    private int countByValues(List<UserImportRowReq> userRowList,
                              Function<UserImportRowReq, String> rowField,
                              Function<List<String>, Integer> counter) {
        List<String> fieldValues = CollUtils.mapToList(userRowList, rowField);
        if (fieldValues.isEmpty()) {
            return 0;
        }
        return counter.apply(fieldValues);
    }

    private List<String> listByValues(List<UserImportRowReq> userRowList,
                                      Function<UserImportRowReq, String> rowField,
                                      Function<List<String>, List<String>> query) {
        List<String> fieldValues = CollUtils.mapToList(userRowList, rowField);
        if (fieldValues.isEmpty()) {
            return Collections.emptyList();
        }
        return query.apply(fieldValues);
    }

    private List<UserImportRowReq> filterImportData(List<UserImportRowReq> importRowList) {
        List<UserImportRowReq> list = importRowList.stream()
                .filter(row -> ValidationUtil.validate(row).isEmpty())
                .toList();
        return list.stream()
                .collect(Collectors.toMap(UserImportRowReq::getUsername, user -> user, (existing, replacement) -> existing))
                .values()
                .stream()
                .toList();
    }
}
