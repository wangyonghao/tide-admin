
package top.wyhao.cmn.db.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import net.dreamlu.mica.core.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import top.wyhao.starter.core.constant.StringConstants;
import top.wyhao.starter.core.util.ReflectUtils;
import top.wyhao.starter.core.util.validation.ValidationUtils;

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * QueryWrapper 工具类
 *
 */
public class WrapperUtil {

    private static final Logger log = LoggerFactory.getLogger(WrapperUtil.class);

    private WrapperUtil() {}

    public static Sort parseSort(String[] sort) {
        if (ArrayUtil.isEmpty(sort)) {
            return Sort.unsorted();
        }
        ValidationUtils.throwIf(sort.length < 2, "排序条件无效");
        List<Sort.Order> orders = new ArrayList<>(sort.length);
        if (CharSequenceUtil.contains(sort[0], ",")) {
            // e.g "sort=createTime,desc&sort=name,asc"
            for (String item : sort) {
                List<String> order = CharSequenceUtil.splitTrim(item, ",");
                orders.add(parseOrder(order.get(0), order.get(1)));
            }
        } else {
            // e.g "sort=createTime,desc"
            orders.add(parseOrder(sort[0], sort[1]));
        }
        return Sort.by(orders);
    }

    /**
     * 获取排序条件
     *
     * @param field     字段
     * @param direction 排序方向
     * @return 排序条件
     */
    public static Sort.Order parseOrder(String field, String direction) {
        ValidationUtils.throwIf(SqlInjectionUtils.check(field), "排序字段包含无效字符");
        return new Sort.Order(Sort.Direction.valueOf(direction.toUpperCase()), field);
    }

    /**
     * 设置排序
     *
     * @param queryWrapper 查询条件封装对象
     * @param sort         排序条件
     * @since 2.9.0
     */
    public static <T> void applySort(QueryWrapper<T> queryWrapper, Sort sort, Class<T> entityClass) {
        if (sort == null || sort.isUnsorted()) {
            return;
        }
        for (Sort.Order order : sort) {
            String property = order.getProperty();
            String checkProperty;
            // 携带表别名则获取 . 后面的字段名
            if (property.contains(StringConstants.DOT)) {
                checkProperty = CollUtil.getLast(CharSequenceUtil.split(property, StringConstants.DOT));
            } else {
                checkProperty = property;
            }
            Optional<String> optional = getValidFieldNames(entityClass).stream()
                    .filter(checkProperty::equals)
                    .findFirst();
            ValidationUtils.throwIf(optional.isEmpty(), "无效的排序字段 [{}]", property);
            queryWrapper.orderBy(true, order.isAscending(), CharSequenceUtil.toUnderlineCase(property));
        }
    }

    /**
     * 反射获取实体类的所有属性名（含父类属性），转小写去重
     */
    private static Set<String> getValidFieldNames(Class<?> entityClass) {
        Set<String> fieldNames = new HashSet<>();
        // 遍历当前类及父类的所有字段
        Class<?> currentClass = entityClass;
        while (currentClass != null && currentClass != Object.class) {
            List<Field> fields = ReflectUtils.getNonStaticFields(currentClass);
            for (Field field : fields) {
                // 添加字段名（转小写，兼容前端传入大写/小写的情况）
                fieldNames.add(field.getName().toLowerCase());
            }
            currentClass = currentClass.getSuperclass();
        }
        return fieldNames;
    }

    /**
     * 构建 QueryWrapper
     *
     * @param query 查询条件
     * @param <Q>   查询条件数据类型
     * @param <R>   查询数据类型
     * @return QueryWrapper
     */
    public static <Q, R> QueryWrapper<R> build(Q query) {
        return build(query, Sort.unsorted());
    }

    /**
     * 构建 QueryWrapper
     *
     * @param query 查询条件
     * @param sort  排序条件
     * @param <Q>   查询条件数据类型
     * @param <R>   查询数据类型
     * @return QueryWrapper
     */
    public static <Q, R> QueryWrapper<R> build(Q query, Sort sort) {
        QueryWrapper<R> queryWrapper = new QueryWrapper<>();
        // 没有查询条件，直接返回
        if (query == null) {
            return queryWrapper;
        }
        // 设置排序条件
        if (sort != null && sort.isSorted()) {
            for (Sort.Order order : sort) {
                String field = CharSequenceUtil.toUnderlineCase(order.getProperty());
                ValidationUtils.throwIf(SqlInjectionUtils.check(field), "排序字段包含无效字符");
                queryWrapper.orderBy(true, order.isAscending(), field);
            }
        }
        // 获取查询条件中所有的字段
        List<Field> fieldList = ReflectUtils.getNonStaticFields(query.getClass());
        return build(query, fieldList, queryWrapper);
    }

    /**
     * 将 Spring Sort 应用到 LambdaQueryWrapper（自动字符串 → SFunction）
     */
    public static <T> void applySort(LambdaQueryWrapper<T> wrapper, Sort sort, Class<T> entityClass) {
        if (sort == null || sort.isUnsorted()) {
            return;
        }
        for (Sort.Order order : sort) {
            String fieldName = order.getProperty();
            boolean asc = order.getDirection().isAscending();

            try {
                // 核心：字符串 → SFunction
                SFunction<T, ?> sFunction = createSFunction(entityClass, fieldName);
                if (asc) {
                    wrapper.orderByAsc(sFunction);
                } else {
                    wrapper.orderByDesc(sFunction);
                }
            } catch (Throwable e) {
                throw new IllegalArgumentException("无效排序字段：" + fieldName, e);
            }
        }
    }

    private static final ConcurrentHashMap<String, SFunction<?, ?>> CACHE = new ConcurrentHashMap<>();

    /**
     * 字段名 createTime → SFunction(User::getCreateTime)
     */
    @SuppressWarnings("unchecked")
    public static <T> SFunction<T, ?> createSFunction(Class<T> entityClass, String fieldName) throws Throwable {
        String key = entityClass.getName() + "#" + fieldName;
        if (CACHE.containsKey(key)) {
            return (SFunction<T, ?>) CACHE.get(key);
        }

        // 获取字段
        Field field = entityClass.getDeclaredField(fieldName);

        // 获取 getter 方法
        String getterName = "get" + StringUtil.capitalize(field.getName());
        Method getter = entityClass.getMethod(getterName);

        // 动态生成 Lambda
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        var func = (SFunction<T, ?>) LambdaMetafactory.metafactory(
                        lookup,
                        "apply",
                        MethodType.methodType(SFunction.class),
                        MethodType.methodType(Object.class, Object.class),
                        lookup.unreflect(getter),
                        MethodType.methodType(getter.getReturnType(), entityClass))
                .getTarget().invokeExact();

        CACHE.put(key, func);
        return func;
    }
}
