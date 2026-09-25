package top.wyhao.identity.app.service.impl;

import cn.dev33.satoken.stp.StpUtil;
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
import top.wyhao.cmn.db.query.PageParam;
import top.wyhao.cmn.db.query.PageResult;
import top.wyhao.common.security.util.LoginUtil;
import top.wyhao.file.core.domain.File;
import top.wyhao.file.core.service.FileService;
import top.wyhao.identity.app.assembler.UserAssembler;
import top.wyhao.identity.app.query.UserViewQuery;
import top.wyhao.identity.domain.exception.UserException;
import top.wyhao.identity.domain.gateway.PasswordHistoryRepository;
import top.wyhao.identity.domain.gateway.SystemConfigApi;
import top.wyhao.identity.domain.gateway.UserRepository;
import top.wyhao.identity.domain.model.SysUser;
import top.wyhao.identity.adapter.web.dto.*;
import top.wyhao.identity.adapter.web.result.config.SecurityConfigVO;
import top.wyhao.identity.adapter.web.result.user.UserImportParseResp;
import top.wyhao.identity.adapter.web.result.user.UserImportResp;
import top.wyhao.identity.adapter.web.vo.UserDetail;
import top.wyhao.identity.adapter.web.vo.UserResult;
import top.wyhao.identity.app.service.UserPasswordHistoryService;
import top.wyhao.identity.app.service.UserService;
import top.wyhao.identity.app.service.UserSocialService;
import top.wyhao.starter.cache.redisson.util.RedisUtils;
import top.wyhao.cmn.core.constant.CacheConstants;
import top.wyhao.cmn.core.constant.RegexConstants;
import top.wyhao.cmn.core.constant.StringConstants;
import top.wyhao.cmn.core.enums.GenderEnum;
import top.wyhao.cmn.core.enums.RoleCodeEnum;
import top.wyhao.cmn.core.enums.StatusEnum;
import top.wyhao.security.client.SecurityClient;
import top.wyhao.organization.client.DeptApi;
import top.wyhao.security.client.RoleApi;
import top.wyhao.cmn.core.util.CollUtils;
import top.wyhao.cmn.core.util.ExceptionUtils;
import top.wyhao.cmn.core.util.RsaUtils;
import top.wyhao.starter.excel.util.ExcelUtils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static top.wyhao.identity.domain.model.ImportPolicies.*;
import static top.wyhao.identity.domain.model.PasswordPolicies.*;

/**
 * 用户业务实现
 *
 * @since 2022/12/21 21:49
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserPasswordHistoryService userPasswordHistoryService;
    private final UserSocialService userSocialService;
    private final RoleApi roleApi;
    private final DeptApi deptApi;
    private final SecurityClient securityClient;
    private final FileService fileService;
    private final UserRepository userRepository;
    private final PasswordHistoryRepository passwordHistoryRepository;
    private final UserViewQuery userViewQuery;
    private final SystemConfigApi systemConfigApi;
    private final UserAssembler userAssembler;

    @Value("${avatar.support-suffix}")
    private String[] avatarSupportSuffix;


    @Override
    public UserDetail detail(Long id) {
        SysUser userDO = userRepository.findById(id);
        if (userDO == null) {
            throw UserException.notFound();
        }
        return userAssembler.toDetail(userDO);
    }

    @Override
    public PageResult<UserResult> page(UserQuery query, PageParam pageParam) {
        return userViewQuery.page(query, pageParam);
    }

    @Transactional(rollbackFor = Exception.class)
    public Long create(UserRequest request) {
        /* 入参格式校验 */
        String rawPassword = ExceptionUtils.exToNull(() -> RsaUtils.decryptByRsaPrivateKey(request.getPassword()));
        if (CharSequenceUtil.isBlank(rawPassword)) {
            throw UserException.passwordDecryptFailed();
        }
        if (!ReUtil.isMatch(RegexConstants.PASSWORD, rawPassword)) {
            throw UserException.passwordFormatInvalid();
        }
        this.checkEmailUnique(request.getEmail(), null);
        this.checkPhoneUnique(request.getPhone(), null);
        this.checkUsernameUnique(request.getUsername());

        SysUser newUser = userAssembler.toEntity(request);
        /* 业务逻辑校验 */



        /* 执行业务 */
        newUser.setPassword(passwordEncoder.encode(rawPassword));
        SecurityConfigVO loginConfig = systemConfigApi.getSecurityConfig();
        newUser.setPwdExpireDate(LocalDate.now().plusDays(loginConfig.getPasswordExpireDays()));
        userRepository.insert(newUser);

        // 保存用户和角色的关联
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
        SysUser oldUser = this.getById(userId);

        if (StatusEnum.DISABLE.getValue().equals(oldUser.getStatus())) {
            if (oldUser.getIsBuiltin()) {
                throw UserException.disableNotAllowed();
            }
        }
        if (CollUtil.isNotEmpty(userRequest.getRoleIds())) {
            throw UserException.roleChangeNotAllowed();
        }
        if (StrUtil.isNotBlank(userRequest.getEmail())) {
            this.checkEmailUnique(userRequest.getEmail(), userId);
        }
        if (StrUtil.isNotBlank(userRequest.getPhone())) {
            this.checkPhoneUnique(userRequest.getPhone(), userId);
        }

        SysUser updateUser = userAssembler.toEntity(userRequest);
        updateUser.setId(userId);
        userRepository.updateById(updateUser);
        // 保存用户和角色的关联
        roleApi.assignRolesToUser(userRequest.getRoleIds(), userId);

        // 用户被禁用，则踢出在线用户
        if (StatusEnum.DISABLE.equals(userRequest.getStatus())) {
            LoginUtil.kickout(userId);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(key = "#ids", name = CacheConstants.USER_KEY_PREFIX, multi = true)
    public void delete(List<Long> ids) {
        if (CollUtil.contains(ids, LoginUtil.getUserId())) {
            throw UserException.deleteSelfNotAllowed();
        }
        List<SysUser> list = userRepository.listBriefByIds(ids);
        List<Long> idList = CollUtils.mapToList(list, SysUser::getId);
        Collection<Long> subtractIds = CollUtil.subtract(ids, idList);
        if (ObjectUtil.isNotEmpty(subtractIds)) {
            throw UserException.notFound(CollUtil.join(subtractIds, StringConstants.COMMA));
        }
        Optional<SysUser> builtinUser = list.stream().filter(SysUser::getIsBuiltin).findFirst();
        if (builtinUser.isPresent()) {
            throw UserException.builtinDeleteNotAllowed(builtinUser.get().getNickname());
        }
        // 删除用户和角色关联
        roleApi.deleteUserRolesByUserIds(ids);
        // 删除历史密码
        userPasswordHistoryService.deleteByUserIds(ids);
        // 删除用户绑定的第三方账号信息
        userSocialService.deleteByUserIds(ids);
        // 删除用户
        userRepository.deleteByIds(ids);
        // 踢出在线用户
        ids.forEach(LoginUtil::kickout);
    }

    @Override
    public UserImportParseResp parseImport(MultipartFile file) {
        UserImportParseResp userImportResp = new UserImportParseResp();
        List<UserImportRowReq> importRowList;
        // 读取表格数据
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
        // 总计行数
        userImportResp.setTotalRows(importRowList.size());
        if (ObjectUtil.isEmpty(importRowList)) {
            throw UserException.importDataInvalid();
        }
        // 有效行数：过滤无效数据
        List<UserImportRowReq> validRowList = this.filterImportData(importRowList);
        userImportResp.setValidRows(validRowList.size());
        if (ObjectUtil.isEmpty(validRowList)) {
            throw UserException.importDataInvalid();
        }

        // 检测表格内数据是否合法
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

        // 校验是否存在无效角色
        List<String> roleNames = validRowList.stream().map(UserImportRowReq::getRoleName).distinct().toList();
        int existRoleCount = roleApi.countByNames(roleNames);
        if (existRoleCount < roleNames.size()) {
            throw UserException.importRoleInvalid();
        }
        // 校验是否存在无效部门（支持多级部门解析）
        Set<String> deptNames = CollUtils.mapToSet(validRowList, UserImportRowReq::getDeptName);
        int existDeptCount = deptApi.countValidDeptPaths(deptNames);
        if (existDeptCount < deptNames.size()) {
            throw UserException.importDeptInvalid();
        }

        // 查询重复用户
        userImportResp.setDuplicateUserRows(countByValues(validRowList, UserImportRowReq::getUsername, userRepository::countByUsernames));
        // 查询重复邮箱
        userImportResp.setDuplicateEmailRows(countByValues(validRowList, UserImportRowReq::getEmail, userRepository::countByEmails));
        // 查询重复手机
        userImportResp.setDuplicatePhoneRows(countByValues(validRowList, UserImportRowReq::getPhone, userRepository::countByPhones));

        // 设置导入会话并缓存数据，有效期10分钟
        String importKey = UUID.fastUUID().toString(true);
        RedisUtils.set(CacheConstants.DATA_IMPORT_KEY + importKey, JSONUtil.toJsonStr(validRowList), Duration
                .ofMinutes(10));
        userImportResp.setImportKey(importKey);
        return userImportResp;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserImportResp importUser(UserImportRequest req) {
        // 校验导入会话是否过期
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
        // 已存在数据查询
        List<String> existEmails = listByValues(importUserList, UserImportRowReq::getEmail, userRepository::listEmailsIn);
        List<String> existPhones = listByValues(importUserList, UserImportRowReq::getPhone, userRepository::listPhonesIn);
        List<SysUser> existUserList = listByUsernames(CollUtils
                .mapToList(importUserList, UserImportRowReq::getUsername));
        List<String> existUsernames = CollUtils.mapToList(existUserList, SysUser::getUsername);
        if (isExitImportUser(req, importUserList, existUsernames, existEmails, existPhones)) {
            throw UserException.importPolicyViolated();
        }

        // 基础数据准备
        Map<String, Long> userMap = existUserList.stream()
                .collect(Collectors.toMap(SysUser::getUsername, SysUser::getId));
        Map<String, Long> roleMap = roleApi.mapIdByNames(importUserList.stream()
                .map(UserImportRowReq::getRoleName)
                .distinct()
                .toList());
        // 获取多级部门映射
        Map<String, Long> deptMap = deptApi.resolveDeptIdsByPaths(importUserList.stream()
                .map(UserImportRowReq::getDeptName)
                .distinct()
                .toList());

        // 批量操作数据库集合
        List<SysUser> insertList = new ArrayList<>();
        List<SysUser> updateList = new ArrayList<>();
        List<long[]> userRolePairs = new ArrayList<>();
        // ID生成器
        IdGenerator idGenerator = DefaultIdGeneratorProvider.INSTANCE.getShare();
        for (UserImportRowReq row : importUserList) {
            if (isSkipUserImport(req, row, existUsernames, existPhones, existEmails)) {
                // 按规则跳过该行
                continue;
            }
            SysUser userDO = BeanUtil.toBeanIgnoreError(row, SysUser.class);
            userDO.setStatus(req.getDefaultStatus().getValue());
            userDO.setPwdUpdateTime(LocalDateTime.now());
            userDO.setGender(GenderEnum.getByValue(Integer.parseInt(row.getGender())).getValue());
            userDO.setDeptId(deptMap.get(row.getDeptName()));
            // 修改 or 新增
            if (UPDATE.validate(req.getDuplicateUser(), row.getUsername(), existUsernames)) {
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

        // 导出Excel
        ExcelUtils.export(userList, "用户数据", UserDetail.class, response);
    }

    @Override
    public void resetPassword(UserPasswordResetRequest resetRequest, Long id) {
        String rawPassword = resetRequest.getNewPassword();

        SecurityConfigVO loginConfig = systemConfigApi.getSecurityConfig();
        userRepository.updatePasswordAndExpire(id, passwordEncoder.encode(rawPassword), LocalDateTime.now()
                .plusDays(loginConfig.getPasswordExpireDays()));
    }

    @Override
    public String resetPassword(Long id) {
        SysUser userDO = userRepository.findById(id);
        if (userDO == null) {
            throw UserException.notFound();
        }
        // 生成12位安全随机密码
        String newPassword = generateSecurePassword(12);

        // 重置密码
        UserPasswordResetRequest req = new UserPasswordResetRequest();
        req.setNewPassword(newPassword);
        this.resetPassword(req, id);

        String retryKey = "login:retry:" + userDO.getUsername() + ":*";
        RedisUtils.deleteByPattern(retryKey);
        return newPassword;
    }

    /**
     * 生成安全的随机密码
     *
     * @param length 密码长度
     * @return 随机密码
     */
    private String generateSecurePassword(int length) {
        String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCase = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String specialChars = "!@#$%^&*";
        String allChars = upperCase + lowerCase + digits + specialChars;

        java.security.SecureRandom random = new java.security.SecureRandom();
        StringBuilder password = new StringBuilder(length);

        // 确保至少包含一个大写字母、小写字母、数字和特殊字符
        password.append(upperCase.charAt(random.nextInt(upperCase.length())));
        password.append(lowerCase.charAt(random.nextInt(lowerCase.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(specialChars.charAt(random.nextInt(specialChars.length())));

        // 填充剩余字符
        for (int i = 4; i < length; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        // 打乱字符顺序
        char[] passwordArray = password.toString().toCharArray();
        for (int i = passwordArray.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[j];
            passwordArray[j] = temp;
        }

        return new String(passwordArray);
    }

    @Override
    public void updateRole(UserRoleUpdateReq updateReq, Long id) {
        this.getById(id);
        List<Long> roleIds = updateReq.getRoleIds();
        // 保存用户和角色关联
        roleApi.assignRolesToUser(roleIds, id);
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

    /**
     * 校验头像文件类型和大小
     *
     * @param avatarFile 头像文件
     */
    private void checkAvatar(MultipartFile avatarFile) {
        String avatarImageType = FileNameUtil.extName(avatarFile.getOriginalFilename());
        if (!CharSequenceUtil.equalsAnyIgnoreCase(avatarImageType, avatarSupportSuffix)) {
            throw UserException.avatarFormatNotSupported(String.join(StringConstants.COMMA, avatarSupportSuffix));
        }

        long avatarMaxSize = 1024 * 1024 * 2; // 2MB
        long avatarSize = avatarFile.getSize();
        if (avatarSize > avatarMaxSize) {
            throw UserException.avatarSizeExceeded(avatarMaxSize / 1024 / 1024);
        }
    }

    @Override
    @CacheUpdate(key = "#id", value = "#req.nickname", name = CacheConstants.USER_KEY_PREFIX)
    public void updateBasicInfo(UserBasicInfoUpdateReq req, Long id) {
        this.detail(id);
        userRepository.updateBasicInfo(id, req.getNickname(), req.getGender());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(String oldPassword, String newPassword, Long id) {
        if (ObjectUtil.equal(newPassword, oldPassword)) {
            throw UserException.passwordSameAsOld();
        }
        SysUser oldUser = this.getById(id);
        if (CharSequenceUtil.isNotBlank(oldUser.getPassword())
                && !passwordEncoder.matches(oldPassword, oldUser.getPassword())) {
            throw UserException.passwordIncorrect();
        }
        // 校验密码合法性
        int passwordRepetitionTimes = this.checkPassword(newPassword, oldUser);
        // 更新密码和密码重置时间
        userRepository.updatePassword(id, newPassword, LocalDateTime.now());
        // 保存历史密码
        passwordHistoryRepository.add(id, oldUser.getPassword(), passwordRepetitionTimes);
        // 修改后登出
        StpUtil.logout();
    }

    @Override
    public void updatePhone(String newPhone, String oldPassword, Long id) {
        SysUser user = userRepository.findById(id);
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw UserException.passwordIncorrect();
        }
        if (ObjectUtil.equal(newPhone, user.getPhone())) {
            throw UserException.phoneSameAsOld();
        }
        this.checkPhoneUnique(newPhone, id);
        SysUser updateUser = new SysUser();
        updateUser.setId(id);
        updateUser.setPhone(newPhone);
        // 更新
        userRepository.updatePartial(updateUser);
    }

    @Override
    public void updateEmail(String newEmail, String oldPassword, Long id) {
        SysUser user = this.getById(id);
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw UserException.passwordIncorrect();
        }
        if (ObjectUtil.equal(newEmail, user.getEmail())) {
            throw UserException.emailSameAsOld();
        }
        this.checkEmailUnique(newEmail, id);

        SysUser updateUser = new SysUser();
        updateUser.setId(id);
        updateUser.setEmail(newEmail);

        // 更新
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

    @Override
    public List<String> findUserPermissions(Long userId) {
        List<String> roleCodeSet = this.findUserRoles(userId);
        // 超级管理员赋予全部权限
        if (roleCodeSet.contains(RoleCodeEnum.SUPER_ADMIN.getCode())) {
            return List.of("*:*:*");
        }
        return securityClient.listPermissionsByUserId(userId);

    }

    @Override
    public List<String> findUserRoles(Long userId) {
        return userRepository.findRoleCodesByUserId(userId);
    }

    /**
     * 导入用户
     *
     * @param insertList   新增用户
     * @param updateList   修改用户
     */
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

    /**
     * 判断是否跳过导入
     *
     * @param req            导入参数
     * @param row            导入数据
     * @param existUsernames 导入数据中已存在的用户名
     * @param existEmails    导入数据中已存在的邮箱
     * @param existPhones    导入数据中已存在的手机号
     * @return 是否跳过
     */
    private boolean isSkipUserImport(UserImportRequest req,
                                     UserImportRowReq row,
                                     List<String> existUsernames,
                                     List<String> existEmails,
                                     List<String> existPhones) {
        return SKIP.validate(req.getDuplicateUser(), row.getUsername(), existUsernames) || SKIP.validate(req
                .getDuplicateEmail(), row.getEmail(), existEmails) || SKIP.validate(req.getDuplicatePhone(), row
                .getPhone(), existPhones);
    }

    /**
     * 判断是否退出导入
     *
     * @param req            导入参数
     * @param list           导入数据
     * @param existUsernames 导入数据中已存在的用户名
     * @param existEmails    导入数据中已存在的邮箱
     * @param existPhones    导入数据中已存在的手机号
     * @return 是否退出
     */
    private boolean isExitImportUser(UserImportRequest req,
                                     List<UserImportRowReq> list,
                                     List<String> existUsernames,
                                     List<String> existEmails,
                                     List<String> existPhones) {
        return list.stream()
                .anyMatch(row -> EXIT.validate(req.getDuplicateUser(), row.getUsername(), existUsernames) || EXIT
                        .validate(req.getDuplicateEmail(), row.getEmail(), existEmails) || EXIT.validate(req
                        .getDuplicatePhone(), row.getPhone(), existPhones));
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

    /**
     * 过滤无效的导入用户数据（批量导入不严格校验数据）
     *
     * @param importRowList 导入数据
     */
    private List<UserImportRowReq> filterImportData(List<UserImportRowReq> importRowList) {
        // 校验过滤
        List<UserImportRowReq> list = importRowList.stream()
                .filter(row -> ValidationUtil.validate(row).isEmpty())
                .toList();
        // 用户名去重
        return list.stream()
                .collect(Collectors.toMap(UserImportRowReq::getUsername, user -> user, (existing, replacement) -> existing))
                .values()
                .stream()
                .toList();
    }

    /**
     * 检测密码合法性
     *
     * @param password 密码
     * @param user     用户信息
     * @return 密码允许重复使用次数
     */
    private int checkPassword(String password, SysUser user) {
        SecurityConfigVO securityConfig = systemConfigApi.getSecurityConfig();
        // 密码最小长度
        PASSWORD_MIN_LENGTH.validate(password, securityConfig.getPasswordMinLength(), user);
        // 密码是否必须包含特殊字符
        PASSWORD_REQUIRE_SYMBOLS.validate(password, securityConfig.getPasswordRequireSpecial() ? 1 : 0, user);
        // 密码是否允许包含正反序账号名
        PASSWORD_ALLOW_CONTAIN_USERNAME.validate(password, securityConfig.getPasswordAllowContainUsername() ? 1 : 0, user);
        // 密码重复使用次数
        int passwordRepetitionTimes = securityConfig.getPasswordRepetitionTimes();
        PASSWORD_REPETITION_TIMES.validate(password, securityConfig.getPasswordRepetitionTimes(), user);
        return passwordRepetitionTimes;
    }

    /**
     * 检查用户名是否重复
     *
     * @param username 用户名
     */
    private void checkUsernameUnique(String username) {
        boolean isExists = userRepository.existsUsername(username);
        if (isExists) {
            throw UserException.usernameExists();
        }
    }

    /**
     * 检查邮箱是否重复
     */
    private void checkEmailUnique(String email, Long selfUserId) {
        boolean isEmailExists = userRepository.existsEmail(email, selfUserId);
        if (isEmailExists) {
            throw UserException.emailExists();
        }
    }

    /**
     * 检查手机号码是否重复
     *
     * @param phone      手机号
     * @param selfUserId 自身用户ID
     */
    private void checkPhoneUnique(String phone, Long selfUserId) {
        boolean isExists = userRepository.existsPhone(phone, selfUserId);
        if (isExists) {
            throw UserException.phoneExists();
        }
    }

    /**
     * 根据用户名获取用户列表
     *
     * @param usernames 用户名列表
     * @return 用户列表
     */
    private List<SysUser> listByUsernames(List<String> usernames) {
        return userRepository.listByUsernames(usernames);
    }

    /**
     * 根据 ID 获取用户信息（数据权限）
     *
     * @param id ID
     * @return 用户信息
     */
    private SysUser getById(Long id) {
        SysUser user = userRepository.findById(id);
        if (user == null) {
            throw UserException.notFound();
        }
        return user;
    }


}