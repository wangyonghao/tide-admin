package top.wyhao.admin.system.service;

import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;
import top.wyhao.admin.system.entity.SysFile;
import top.wyhao.admin.system.model.FileModel;
import top.wyhao.starter.web.core.model.PageQuery;
import top.wyhao.starter.web.core.model.PageResult;

import java.io.InputStream;
import java.util.List;

/**
 * 系统文件 Service
 */
public interface FileService {

    /**
     * 上传文件
     *
     * @param file 文件信息
     * @return 文件信息
     */
    SysFile upload(MultipartFile file, String path);

    PageResult<FileModel.Result> page(@Valid FileModel.Query query, @Valid PageQuery pageQuery);

    List<SysFile> list(@Valid FileModel.Query query);

    SysFile detail(Long id);

    void delete(List<Long> fileId);

    void delete(Long bizId, String bizType);

    /**
     * 获取文件输入流，
     * 可以直接写入到 HttpServletResponse 中，无需保存到本地
     * 用完后需要关闭输入流
     *
     * @param fileId 文件 ID
     * @return 文件输入流
     */
    InputStream getFileInputStream(Long fileId);
}