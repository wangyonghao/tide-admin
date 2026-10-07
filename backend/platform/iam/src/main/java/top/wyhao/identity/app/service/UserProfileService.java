package top.wyhao.identity.app.service;

import org.springframework.web.multipart.MultipartFile;
import top.wyhao.identity.adapter.web.dto.UserBasicInfoUpdateReq;
import top.wyhao.identity.domain.model.SysUser;

import java.util.List;

/**
 * 用户对自己账号的操作（个人中心 / 自助注册）。
 */
public interface UserProfileService {

    Long updateAvatar(MultipartFile avatar, Long userId);

    void updateBasicInfo(UserBasicInfoUpdateReq req, Long userId);

    void updatePhone(String newPhone, String oldPassword, Long userId);

    void updateEmail(String newEmail, String oldPassword, Long userId);

    /**
     * 自助注册：写入已校验的明文密码与角色。
     */
    SysUser registerLocal(String username, String rawPassword, Long departmentId, List<Long> roleIds);
}
