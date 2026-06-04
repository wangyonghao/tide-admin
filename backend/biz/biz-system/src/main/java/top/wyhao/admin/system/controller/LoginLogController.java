package top.wyhao.admin.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.wyhao.admin.system.model.LoginLogModel;
import top.wyhao.admin.system.service.LoginLogService;
import top.wyhao.starter.web.core.model.PageQuery;
import top.wyhao.starter.web.core.model.PageResult;

/**
 * 登录日志 API
 *
 * @since 2026/05/08
 */
@Tag(name = "登录日志 API")
@Validated
@RestController
@RequiredArgsConstructor
public class LoginLogController {

}
