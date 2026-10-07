package top.wyhao.identity.app.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
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
import top.wyhao.common.satoken.util.LoginUtil;
import top.wyhao.identity.adapter.web.dto.UserImportRequest;
import top.wyhao.identity.adapter.web.dto.UserImportRowReq;
import top.wyhao.identity.adapter.web.dto.UserQuery;
import top.wyhao.identity.adapter.web.dto.UserRequest;
import top.wyhao.identity.adapter.web.dto.UserRoleUpdateReq;
import top.wyhao.identity.adapter.web.vo.UserDetail;
import top.wyhao.identity.adapter.web.vo.UserImportParseResp;
import top.wyhao.identity.adapter.web.vo.UserImportResp;
import top.wyhao.identity.adapter.web.vo.UserResult;
import top.wyhao.identity.app.assembler.UserAssembler;
import top.wyhao.identity.app.query.UserViewQuery;
import top.wyhao.identity.app.service.MembershipService;
import top.wyhao.identity.app.service.UserAdminService;
import top.wyhao.identity.app.service.UserSocialService;
import top.wyhao.identity.client.PasswordPolicyConfig;
import top.wyhao.identity.client.PasswordPolicyConfigApi;
import top.wyhao.identity.domain.exception.UserException;
import top.wyhao.identity.domain.gateway.UserRepository;
import top.wyhao.identity.domain.model.SysUser;
import top.wyhao.identity.domain.service.UserImportPolicy;
import top.wyhao.identity.domain.service.UserLifecycleRules;
import top.wyhao.identity.domain.service.UserUniquenessChecker;
import top.wyhao.department.client.DepartmentApi;
import top.wyhao.redisson.util.RedisUtils;
import top.wyhao.security.client.RoleApi;
import top.wyhao.starter.excel.util.ExcelUtils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 管理员对用户的管理操作实现。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserAdminServiceImpl implements UserAdminService {

    private final PasswordEncoder passwordEncoder;
    private final UserSocialService userSocialService;
    private final RoleApi roleApi;
    private final DepartmentApi departmentApi;
    private final MembershipService membershipService;
    private final UserRepository userRepository;
    private final UserViewQuery userViewQuery;
    private final PasswordPolicyConfigApi passwordPolicyConfigApi;
    private final UserAssembler userAssembler;
    private final UserUniquenessChecker uniquenessChecker;
    private final UserLifecycleRules lifecycleRules;
    private final UserImportPolicy importPolicy;

    @Override
    public UserDetail detail(Long id) {
        return userAssembler.toDetail(lifecycleRules.requireUser(id));
    }

    @Override
    public PageResult<UserResult> page(UserQuery query, PageParam pageParam) {
        return userViewQuery.page(query, pageParam);
    }

    @Override
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
        membershipService.replacePrimaryDepartment(newUser.getId(), newUser.getDepartmentId());

        roleApi.assignRolesToUser(request.getRoleIds(), newUser.getId());
        return newUser.getId();
    }

    @Override
    public Long save(SysUser user) {
        userRepository.insert(user);
        membershipService.replacePrimaryDepartment(user.getId(), user.getDepartmentId());
        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheUpdate(key = "#userId", value = "#userRequest.displayName", name = CacheConstants.USER_KEY_PREFIX)
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
        membershipService.replacePrimaryDepartment(userId, updateUser.getDepartmentId());
        roleApi.assignRolesToUser(userRequest.getRoleIds(), userId);

        if (StatusEnum.DISABLE.equals(userRequest.getStatus())) {
            LoginUtil.kickout(userId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(key = "#ids", name = CacheConstants.USER_KEY_PREFIX, multi = true)
    public void delete(List<Long> ids) {
        List<SysUser> list = userRepository.listBriefByIds(ids);
        lifecycleRules.assertDeletable(ids, LoginUtil.getUserId(), list);

        roleApi.deleteUserRolesByUserIds(ids);
        membershipService.removeAllByUserIds(ids);
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
        Set<String> departmentNames = CollUtils.mapToSet(validRowList, UserImportRowReq::getDepartmentName);
        int existDepartmentCount = departmentApi.countValidDepartmentPaths(departmentNames);
        if (existDepartmentCount < departmentNames.size()) {
            throw UserException.importDepartmentInvalid();
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
        Map<String, Long> departmentMap = departmentApi.resolveDepartmentIdsByPaths(importUserList.stream()
                .map(UserImportRowReq::getDepartmentName)
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
            userDO.setDepartmentId(departmentMap.get(row.getDepartmentName()));
            if (importPolicy.shouldUpdateExistingUser(req.getDuplicateUser(), row.getUsername(), existUsernames)) {
                userDO.setId(userMap.get(row.getUsername()));
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
    public Long countByDepartmentIds(List<Long> departmentIds) {
        if (CollUtil.isEmpty(departmentIds)) {
            return 0L;
        }
        return userRepository.countByDepartmentIds(departmentIds);
    }

    private void doImportUser(List<SysUser> insertList, List<SysUser> updateList, List<long[]> userRolePairs) {
        if (CollUtil.isNotEmpty(insertList)) {
            userRepository.insertBatch(insertList);
        }
        if (CollUtil.isNotEmpty(updateList)) {
            userRepository.updateBatch(updateList);
            roleApi.deleteUserRolesByUserIds(CollUtils.mapToList(updateList, SysUser::getId));
        }
        List<long[]> departmentPairs = new ArrayList<>();
        for (SysUser user : CollUtil.emptyIfNull(insertList)) {
            if (user.getDepartmentId() != null) {
                departmentPairs.add(new long[]{user.getId(), user.getDepartmentId()});
            }
        }
        for (SysUser user : CollUtil.emptyIfNull(updateList)) {
            if (user.getDepartmentId() != null) {
                departmentPairs.add(new long[]{user.getId(), user.getDepartmentId()});
            }
        }
        membershipService.replacePrimaryDepartments(departmentPairs);
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
