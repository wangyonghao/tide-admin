package top.wyhao.security.app.assembler;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import top.wyhao.security.domain.model.SysMenu;
import top.wyhao.security.adapter.web.vo.MenuTreeVO;
import top.wyhao.security.adapter.web.vo.MenuVO;
import top.wyhao.starter.web.convert.MapStructConfig;

import java.util.List;
import top.wyhao.security.adapter.web.dto.MenuRequest;

/**
 * 菜单对象转换
 */
@Mapper(config = MapStructConfig.class)
public interface MenuAssembler {

    MenuVO toVO(SysMenu menu);

    List<MenuVO> toVOList(List<SysMenu> menus);

    MenuTreeVO toTreeVO(SysMenu menu);

    List<MenuTreeVO> toTreeVOList(List<SysMenu> menus);

    SysMenu toEntity(MenuRequest request);

    @Mapping(target = "type", ignore = true)
    void update(MenuRequest request, @MappingTarget SysMenu entity);
}
