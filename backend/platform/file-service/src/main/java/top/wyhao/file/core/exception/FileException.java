package top.wyhao.file.core.exception;

/**
 * 文件异常基类
 *
 * @author wyh
 * @since 2026/09/16
 */
public class FileException extends RuntimeException {

    /**
     * 错误码
     */
    private final String code;

    public FileException(String message) {
        super(message);
        this.code = "FILE_ERROR";
    }

    public FileException(String code, String message) {
        super(message);
        this.code = code;
    }

    public FileException(String message, Throwable cause) {
        super(message, cause);
        this.code = "FILE_ERROR";
    }

    public FileException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
