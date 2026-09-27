package ${packageName}.${subPackageName};

import org.apache.ibatis.annotations.Mapper;
import ${packageName}.model.entity.${classNamePrefix}DO;
import model.top.wyhao.cmn.db.BaseMapper;

/**
* ${businessName} Mapper
*
* @author ${author}
* @since ${datetime}
*/
@Mapper
public interface ${className} extends BaseMapper<${classNamePrefix}DO> {}