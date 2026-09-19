package top.wyhao.file.api.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import top.wyhao.file.core.exception.FileException;
import top.wyhao.file.core.exception.FileNotFoundException;
import top.wyhao.storage.api.StorageException;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件 API 异常处理器
 *
 * @author wyh
 * @since 2026/09/16
 */
@Slf4j
@Order(50)
@RestControllerAdvice(basePackages = "top.wyhao.file.api")
public class FileApiExceptionHandler {

    /**
     * 处理文件未找到异常
     *
     * @param e 文件未找到异常
     * @return 错误响应
     */
    @ExceptionHandler(FileNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleFileNotFoundException(FileNotFoundException e) {
        log.warn("文件未找到: {}", e.getMessage());
        Map<String, Object> result = new HashMap<>();
        result.put("code", e.getCode());
        result.put("msg", e.getMessage());
        return result;
    }

    /**
     * 处理文件异常
     *
     * @param e 文件异常
     * @return 错误响应
     */
    @ExceptionHandler(FileException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleFileException(FileException e) {
        log.error("文件操作异常: {}", e.getMessage(), e);
        Map<String, Object> result = new HashMap<>();
        result.put("code", e.getCode());
        result.put("msg", e.getMessage());
        return result;
    }

    /**
     * 处理存储异常
     *
     * @param e 存储异常
     * @return 错误响应
     */
    @ExceptionHandler(StorageException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleStorageException(StorageException e) {
        log.error("存储操作异常: {}", e.getMessage(), e);
        Map<String, Object> result = new HashMap<>();
        result.put("code", e.getCode());
        result.put("msg", e.getMessage());
        return result;
    }
}
