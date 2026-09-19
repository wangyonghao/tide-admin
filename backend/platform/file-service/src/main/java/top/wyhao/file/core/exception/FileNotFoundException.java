package top.wyhao.file.core.exception;

/**
 * 文件未找到异常
 *
 * @author wyh
 * @since 2026/09/16
 */
public class FileNotFoundException extends FileException {

    public FileNotFoundException(Long fileId) {
        super("FILE_NOT_FOUND", String.format("文件不存在: %d", fileId));
    }

    public FileNotFoundException(String storageKey) {
        super("FILE_NOT_FOUND", String.format("文件不存在: %s", storageKey));
    }

    public FileNotFoundException(Long fileId, Throwable cause) {
        super("FILE_NOT_FOUND", String.format("文件不存在: %d", fileId), cause);
    }
}
