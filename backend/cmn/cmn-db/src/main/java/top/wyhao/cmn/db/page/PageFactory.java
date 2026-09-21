package top.wyhao.cmn.db.page;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import top.wyhao.cmn.db.query.ColumnResolver;

import java.util.ArrayList;
import java.util.List;

/**
 * PageQuery -> MyBatis-Plus Page 的转换工厂。
 *
 * <p>排序规则：</p>
 * <ol>
 *   <li>前端传了 orderBy，用前端的（支持多字段，见 {@link SortParser}）；</li>
 *   <li>否则用代码里声明的默认排序；</li>
 *   <li>最后若排序字段里不含主键且开启了 stableSort，自动追加主键兜底，保证翻页结果稳定。</li>
 * </ol>
 *
 * <p>所有排序字段都经过 {@link ColumnResolver} 校验，非实体字段直接抛异常。</p>
 */
public final class PageFactory {

    private PageFactory() {
    }

    /** 单表分页，无默认排序 */
    public static <T> Page<T> build(PageQuery query, Class<T> entityClass) {
        return create(query, entityClass, null, List.of());
    }

    /** 单表分页 + 单字段默认排序（倒序） */
    public static <T> Page<T> build(PageQuery query, Class<T> entityClass, SFunction<T, ?> defaultOrder) {
        return create(query, entityClass, null, Sorts.desc(defaultOrder).items());
    }

    /**
     * 单表分页 + 多字段默认排序：
     * <pre>Sorts.asc(User::getStatus).thenDesc(User::getCreateTime)</pre>
     */
    public static <T> Page<T> build(PageQuery query, Class<T> entityClass, Sorts defaultOrders) {
        return create(query, entityClass, null, defaultOrders.items());
    }

    /**
     * 关联查询分页：返回泛型是 VO，排序字段仍按主表实体校验，并自动加表别名。
     *
     * @param metaEntity    校验排序字段用的实体类（通常是主表）
     * @param alias         主表别名，如 "u"
     * @param defaultOrders 默认排序，可为 null
     */
    public static <R> Page<R> buildFor(PageQuery query, Class<?> metaEntity,
                                       String alias, Sorts defaultOrders) {
        return create(query, metaEntity, alias, defaultOrders == null ? List.of() : defaultOrders.items());
    }

    private static <R> Page<R> create(PageQuery query, Class<?> metaEntity,
                                      String alias, List<Sort> defaultOrders) {
        Page<R> page = new Page<>(query.getPageNum(), query.getPageSize(), query.isSearchCount());

        List<Sort> sorts = SortParser.parse(query.getOrderBy(), query.isAsc());
        if (sorts.isEmpty()) {
            sorts = defaultOrders;
        }

        List<Sort> finalSorts = new ArrayList<>(sorts);
        appendTieBreaker(query, metaEntity, finalSorts);

        for (Sort sort : finalSorts) {
            String column = ColumnResolver.resolve(metaEntity, sort.property(), alias);
            page.addOrder(sort.asc() ? OrderItem.asc(column) : OrderItem.desc(column));
        }
        return page;
    }

    /**
     * 按非唯一列排序时（比如 status），数据库对同值行的返回顺序是不确定的，
     * 会出现「第 1 页看到的记录第 2 页又出现一次」。追加主键兜底可以消除这个问题。
     */
    private static void appendTieBreaker(PageQuery query, Class<?> metaEntity, List<Sort> sorts) {
        if (!query.isStableSort() || sorts.isEmpty()) {
            return;
        }
        String keyProperty = ColumnResolver.keyProperty(metaEntity);
        if (keyProperty == null) {
            return;
        }
        boolean present = sorts.stream().anyMatch(s -> keyProperty.equals(s.property()));
        if (!present) {
            sorts.add(Sort.desc(keyProperty));
        }
    }
}
