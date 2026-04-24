package top.wyhao.starter.tenant.config;

import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.springframework.beans.factory.InitializingBean;
import top.wyhao.starter.tenant.core.TenantedEntity;

import java.util.HashSet;
import java.util.Set;

public class TenantTableRegistry implements InitializingBean {
    private final Set<String> tenantedTables = new HashSet<>();

    @Override
    public void afterPropertiesSet() {
        // MyBatis-Plus 缓存的所有实体和表结构
        TableInfoHelper.getTableInfos().forEach(tableInfo -> {
            Class<?> entityType = tableInfo.getEntityType();
            if (TenantedEntity.class.isAssignableFrom(entityType)) {
                tenantedTables.add(tableInfo.getTableName());
            }
        });
    }

    public boolean isTenantedTable(String tableName) {
        return tenantedTables.contains(tableName);
    }
}
