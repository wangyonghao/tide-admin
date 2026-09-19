
package top.wyhao.starter.web.core.model;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * 分页信息
 *

 * @since 2025/12/8
 */
@Getter
@Setter
public class PageResult<T> {

    /**
     * 列表数据
     */
    private List<T> list;

    /**
     * 总记录数
     */
    private long total;

    public PageResult() {
    }

    public PageResult(final List<T> list, final long total) {
        this.list= list;
        this.total = total;
    }

    /**
     * 基于 MyBatis Plus 分页数据构建分页信息，并将源数据转换为指定类型数据
     *
     * @param page        MyBatis Plus 分页数据
     * @param targetClass 目标类型 Class 对象
     * @param <T>         源列表数据类型
     * @param <L>         目标列表数据类型
     * @return 分页信息
     */
    public static <T, L> PageResult<L> build(IPage<T> page, Class<L> targetClass) {
        if (page == null) {
            return empty();
        }
        return new PageResult<>(BeanUtil.copyToList(page.getRecords(), targetClass), page.getTotal());
    }

    /**
     * 基于 MyBatis Plus 分页数据构建分页信息
     *
     * @param page MyBatis Plus 分页数据
     * @param <L>  列表数据类型
     * @return 分页信息
     */
    public static <L> PageResult<L> build(IPage<L> page) {
        if (page == null) {
            return empty();
        }
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    /**
     * 基于列表数据构建分页信息
     *
     * @param page 页码
     * @param size 每页条数
     * @param list 列表数据
     * @param <L>  列表数据类型
     * @return 分页信息
     */
    public static <L> PageResult<L> build(int page, int size, List<L> list) {
        if (CollUtil.isEmpty(list)) {
            return empty();
        }
        PageResult<L> pageResult = new PageResult<>();
        pageResult.setTotal(list.size());
        // 对列表数据进行分页
        int fromIndex = (page - 1) * size;
        if (fromIndex >= list.size()) {
            pageResult.setList(new ArrayList<>(0));
        } else {
            int toIndex = Math.min(fromIndex + size, list.size());
            pageResult.setList(list.subList(fromIndex, toIndex));
        }
        return pageResult;
    }

    /**
     * 基于 MyBatis Plus 分页数据构建分页信息，并通过转换函数映射列表元素
     *
     * @param page      MyBatis Plus 分页数据
     * @param converter 列表转换函数，通常传入 MapStruct {@code assembler::toXxxList}
     */
    public static <T, V> PageResult<V> build(IPage<T> page, Function<List<T>, List<V>> converter) {
        if (page == null) {
            return empty();
        }
        List<T> records = page.getRecords();
        List<V> list = CollUtil.isEmpty(records) ? Collections.emptyList() : converter.apply(records);
        return new PageResult<>(list, page.getTotal());
    }

    /**
     * 空分页信息
     *
     * @param <L> 列表数据类型
     * @return 分页信息
     */
    private static <L> PageResult<L> empty() {
        return new PageResult<>(Collections.emptyList(), 0L);
    }
}
