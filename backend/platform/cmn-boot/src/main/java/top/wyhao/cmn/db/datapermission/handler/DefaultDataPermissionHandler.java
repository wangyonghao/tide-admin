
package top.wyhao.cmn.db.datapermission.handler;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.handler.MultiDataPermissionHandler;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.ParenthesedExpressionList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import top.wyhao.cmn.core.model.RoleDataScope;
import top.wyhao.cmn.db.datapermission.annotation.DataScope;
import top.wyhao.identity.client.LoginUser;
import top.wyhao.common.satoken.util.LoginUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认数据权限处理器
 *
 */
@Slf4j
public class DefaultDataPermissionHandler implements MultiDataPermissionHandler {
    /**
     * 缓存 mappedStatementId -> DataScope注解,避免每次反射解析
     */
    private static final Map<String, DataScope> ANNOTATION_CACHE = new ConcurrentHashMap<>();

    @Override
    public Expression getSqlSegment(final Table table, final Expression where, final String mappedStatementId) {
        LoginUser user = LoginUtil.getLoginUser();
        // 未登录或超管:不加任何限制
        if (user == null || LoginUtil.isSuperadmin()) {
            return null;
        }

        DataScope dataPermission = getAnnotation(mappedStatementId);
        if (dataPermission == null) {
            return null; // 该方法没标注解,不做数据权限过滤
        }

        List<Expression> orConditions = new ArrayList<>();
        for (RoleDataScope role : user.getRoleDataScopes()) {
            switch (role.getDataScope()) {
                case ALL:
                    return null; // 不加任何限制
                case SELF:
                    orConditions.add(buildEqExpression(dataPermission.userAlias(), dataPermission.userIdColumn(), user.getUserId()));
                case DEPT_AND_CHILD, DEPT, CUSTOM_DEPT:
                    orConditions.add(buildInExpression(dataPermission.deptAlias(), dataPermission.deptIdColumn(), role.getVisibleDeptIds()));
                default:
                    break;
            } ;
        }
        if (orConditions.isEmpty()) {
            // 没有任何一个角色命中有效范围,兜底:让查询查不到任何数据(避免误开权限)
            return new EqualsTo(new LongValue(1), new LongValue(0));
        }

        // 多角色取并集(OR),用括号包起来,和原有 WHERE 用 AND 连接
        Expression combined = orConditions.get(0);
        for (int i = 1; i < orConditions.size(); i++) {
            combined = new OrExpression(combined, orConditions.get(i));
        }
        return new ParenthesedExpressionList(combined);
    }

    /**
     * 根据 mappedStatementId(形如 com.xxx.mapper.SysUserMapper.selectUserList)
     * 反射拿到方法上的 @DataScope 注解
     */
    private DataScope getAnnotation(String mappedStatementId) {
        return ANNOTATION_CACHE.computeIfAbsent(mappedStatementId, id -> {
            try {
                int idx = id.lastIndexOf(".");
                String className = id.substring(0, idx);
                String methodName = id.substring(idx + 1);
                Class<?> clazz = Class.forName(className);
                for (Method method : clazz.getMethods()) {
                    if (method.getName().equals(methodName)
                            && method.isAnnotationPresent(DataScope.class)) {
                        return method.getAnnotation(DataScope.class);
                    }
                }
            } catch (Exception ignored) {}
            return null;
        });
    }

    private Expression buildInExpression(String alias, String column, List<Long> values) {
        Column col = new Column(StringUtils.isBlank(alias) ? column : alias + "." + column);
        ExpressionList<Expression> list = new ExpressionList<>();
        values.forEach(v -> list.addExpressions(new LongValue(v)));
        InExpression in = new InExpression();
        in.setLeftExpression(col);
        in.setRightExpression(list);
        return in;
    }

    private Expression buildEqExpression(String alias, String column, Long value) {
        Column col = new Column(StringUtils.isBlank(alias) ? column : alias + "." + column);
        return new EqualsTo(col, new LongValue(value));
    }
}
