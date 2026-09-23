package top.wyhao.file.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.wyhao.file.api.assembler.FileAssembler;
import top.wyhao.file.api.model.FileResponse;
import top.wyhao.file.api.model.FileUploadResponse;
import top.wyhao.file.core.domain.File;
import top.wyhao.file.core.service.FileService;
import top.wyhao.starter.core.UserContextHolder;
import top.wyhao.starter.web.core.model.IdsRequest;
import top.wyhao.starter.web.core.model.PageQuery;
import top.wyhao.starter.web.core.model.PageResult;
import top.wyhao.starter.web.util.HttpUtil;

import java.io.InputStream;
import java.util.List;

/**
 * 文件 API
 *
 * <p>提供基础文件能力：上传、下载、预览、删除、分页查询。
 * 不处理业务附件、网盘等业务领域。
 *
 * @author wyh
 * @since 2026/09/16
 */
@Tag(name = "文件 API")
@RestController
@Validated
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final FileAssembler fileAssembler;

    @Operation(summary = "分页查询文件")
    @GetMapping("/api/files")
    public PageResult<FileResponse> page(
            @RequestParam(required = false) String fileName,
            @RequestParam(required = false) String category,
            @RequestParam(required = false, defaultValue = "desc") String sortOrder,
            @Validated PageQuery pageQuery) {
        return PageResult.build(
                fileService.page(fileName, category, sortOrder, pageQuery.getPage(), pageQuery.getPageSize()),
                fileAssembler::toResponseList);
    }

    @Operation(summary = "上传文件")
    @PostMapping("/api/files")
    public FileUploadResponse upload(
            @RequestPart @NotNull(message = "文件不能为空") MultipartFile file) {
        File uploadedFile = fileService.upload(file, currentUserId());
        return fileAssembler.toUploadResponse(uploadedFile);
    }

    @Operation(summary = "获取文件信息")
    @Parameter(name = "fileId", description = "文件 ID", example = "1", in = ParameterIn.PATH)
    @GetMapping("/api/files/{fileId}")
    public FileResponse getFileInfo(@PathVariable Long fileId) {
        return fileAssembler.toResponse(fileService.get(fileId));
    }

    @Operation(summary = "下载文件")
    @Parameter(name = "fileId", description = "文件 ID", example = "1", in = ParameterIn.PATH)
    @GetMapping("/api/files/{fileId}/download")
    public void download(@PathVariable Long fileId, HttpServletResponse response) {
        File file = fileService.get(fileId);
        InputStream inputStream = fileService.download(fileId);
        HttpUtil.writeAttachmentToResponse(inputStream, file.getFileName(), response);
    }

    @Operation(summary = "预览文件")
    @Parameter(name = "fileId", description = "文件 ID", example = "1", in = ParameterIn.PATH)
    @GetMapping("/api/files/{fileId}/preview")
    public void preview(@PathVariable Long fileId, HttpServletResponse response) {
        File file = fileService.get(fileId);
        InputStream inputStream = fileService.download(fileId);
        HttpUtil.preview(inputStream, file.getFileName(), file.getContentType(), response);
    }

    @Operation(summary = "删除文件")
    @Parameter(name = "fileId", description = "文件 ID", example = "1", in = ParameterIn.PATH)
    @DeleteMapping("/api/files/{fileId}")
    public void delete(@PathVariable Long fileId) {
        fileService.delete(fileId, currentUserId());
    }

    @Operation(summary = "批量删除文件")
    @DeleteMapping("/api/files")
    public void batchDelete(@RequestBody @Validated IdsRequest request) {
        List<Long> ids = request.getIds();
        if (ids == null || ids.isEmpty()) {
            return;
        }
        fileService.delete(ids, currentUserId());
    }

    private Long currentUserId() {
        Long userId = UserContextHolder.getUserId();
        return userId != null ? userId : 0L;
    }
}
