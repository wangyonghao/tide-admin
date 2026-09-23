package top.wyhao.file.core.exception;

/**
 * 文件类型不允许异常
 *
 * @author wyh
 * @since 2026/09/23
 */
public class FileTypeNotAllowedException extends FileException {

    public FileTypeNotAllowedException(String message) {
        super("FILE_TYPE_NOT_ALLOWED", message);
    }

    public static FileTypeNotAllowedException extensionNotAllowed(String extension) {
        return new FileTypeNotAllowedException(String.format("不支持上传 [%s] 类型的文件", extension));
    }

    public static FileTypeNotAllowedException contentMismatch(String extension) {
        return new FileTypeNotAllowedException(String.format("文件内容与扩展名 [%s] 不匹配", extension));
    }
}
