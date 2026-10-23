package top.wyhao.admin.system.security.app.assembler;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import top.wyhao.admin.system.security.domain.model.SysMenu;
import top.wyhao.admin.system.security.app.dto.MenuTreeVO;
import top.wyhao.admin.system.security.app.dto.MenuVO;
import top.wyhao.web.convert.MapStructConfig;

import java.util.List;
import top.wyhao.admin.system.security.app.dto.MenuRequest;

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
