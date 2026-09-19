package top.wyhao.admin.system.assembler;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import top.wyhao.admin.system.entity.SysMenu;
import top.wyhao.admin.system.model.MenuModel;
import top.wyhao.admin.system.model.result.MenuTreeVO;
import top.wyhao.admin.system.model.result.MenuVO;
import top.wyhao.starter.web.convert.MapStructConfig;

import java.util.List;

/**
 * 菜单对象转换
 */
@Mapper(config = MapStructConfig.class)
public interface MenuAssembler {

    MenuVO toVO(SysMenu menu);

    List<MenuVO> toVOList(List<SysMenu> menus);

    MenuTreeVO toTreeVO(SysMenu menu);

    List<MenuTreeVO> toTreeVOList(List<SysMenu> menus);

    SysMenu toEntity(MenuModel.Request request);

    @Mapping(target = "type", ignore = true)
    void update(MenuModel.Request request, @MappingTarget SysMenu entity);
}
