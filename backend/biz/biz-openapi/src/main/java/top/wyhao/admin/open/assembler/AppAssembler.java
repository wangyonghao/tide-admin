package top.wyhao.admin.open.assembler;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import top.wyhao.admin.open.model.entity.SysApp;
import top.wyhao.admin.open.model.req.AppReq;
import top.wyhao.admin.open.model.resp.AppDetailResp;
import top.wyhao.admin.open.model.resp.AppResult;
import top.wyhao.starter.web.convert.BaseEnumConverters;
import top.wyhao.starter.web.convert.MapStructConfig;

import java.util.List;

/**
 * 开放应用对象转换
 */
@Mapper(config = MapStructConfig.class, uses = BaseEnumConverters.class)
public interface AppAssembler {

    SysApp toEntity(AppReq request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(AppReq request, @MappingTarget SysApp entity);

    AppResult toResult(SysApp app);

    List<AppResult> toResultList(List<SysApp> apps);

    AppDetailResp toDetail(SysApp app);

    List<AppDetailResp> toDetailList(List<SysApp> apps);
}
