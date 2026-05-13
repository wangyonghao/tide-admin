package top.wyhao.admin.system.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import top.wyhao.starter.core.spi.OSSConfigProvider;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 本地文件访问控制器
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class FileAccessController {

    private final OSSConfigProvider ossConfigProvider;

    /**
     * 访问本地文件
     */
    @SaIgnore
    @GetMapping("/fs/**")
    public ResponseEntity<byte[]> getFile(HttpServletRequest request) {
        // 获取文件路径
        // 注意：ApiPrefixConfig 给 @RestController 自动加了 /api 前缀
        // 所以实际请求URI是 /api/fs/xxx，需要从 /api/fs 之后截取
        String requestUri = request.getRequestURI();
        int filesIndex = requestUri.indexOf("/fs/");
        String filePath = (filesIndex >= 0) ? requestUri.substring(filesIndex + "/fs".length()) : requestUri;

        // URL 解码（浏览器发送的中文/特殊字符默认是 %xx 编码的）
        filePath = URLDecoder.decode(filePath, StandardCharsets.UTF_8);

        // 获取本地存储路径
        String basePath = ossConfigProvider.getStorageLocalPath();

        try {
            // 构建完整路径
            Path fullPath = Paths.get(basePath, filePath);

            // 安全检查：确保路径在基础目录内,防路径穿越
            if (!fullPath.normalize().startsWith(Paths.get(basePath).normalize())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 权限失败或文件不存在一律返回 404, 不暴露 403 或真实路径
            }

            if (!Files.exists(fullPath)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // 读取文件
            byte[] bytes = Files.readAllBytes(fullPath);
            String contentType = Files.probeContentType(fullPath);
            if (contentType == null) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .body(bytes);
        } catch (IOException e) {
            log.error("读取文件失败: {}", filePath, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
