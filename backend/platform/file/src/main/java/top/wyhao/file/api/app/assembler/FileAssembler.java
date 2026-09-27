package top.wyhao.file.api.app.assembler;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import top.wyhao.file.api.adapter.web.vo.FileResponse;
import top.wyhao.file.api.adapter.web.vo.FileUploadResponse;
import top.wyhao.file.domain.model.File;
import top.wyhao.starter.web.convert.MapStructConfig;

import java.util.List;

/**
 * 文件对象转换
 */
@Mapper(config = MapStructConfig.class)
public interface FileAssembler {

    FileResponse toResponse(File file);

    List<FileResponse> toResponseList(List<File> files);

    @Mapping(source = "id", target = "fileId")
    FileUploadResponse toUploadResponse(File file);
}
