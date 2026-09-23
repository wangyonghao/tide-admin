package top.wyhao.starter.web.core.model;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * 分页结果（响应）
 * <p>统一字段：{@code records} / {@code total} / {@code page} / {@code pageSize} / {@code pages}</p>
 */
@Getter
@Setter
@Schema(description = "分页结果")
public class PageResult<T> {

    /**
     * 列表数据
     */
    @Schema(description = "列表数据")
    private List<T> records;

    /**
     * 总记录数
     */
    @Schema(description = "总记录数", example = "100")
    private long total;

    /**
     * 页码
     */
    @Schema(description = "页码", example = "1")
    private long page;

    /**
     * 每页条数
     */
    @Schema(description = "每页条数", example = "10")
    private long pageSize;

    /**
     * 总页数
     */
    @Schema(description = "总页数", example = "10")
    private long pages;

    public PageResult() {
    }

    public PageResult(List<T> records, long total) {
        this(records, total, 1, records == null ? 0 : records.size());
    }

    public PageResult(List<T> records, long total, long page, long pageSize) {
        this.records = records == null ? Collections.emptyList() : records;
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
        this.pages = calcPages(total, pageSize);
    }

    /**
     * 基于 MyBatis Plus 分页数据构建分页信息，并将源数据转换为指定类型数据
     */
    public static <T, L> PageResult<L> build(IPage<T> page, Class<L> targetClass) {
        if (page == null) {
            return empty();
        }
        return of(BeanUtil.copyToList(page.getRecords(), targetClass), page);
    }

    /**
     * 基于 MyBatis Plus 分页数据构建分页信息
     */
    public static <L> PageResult<L> build(IPage<L> page) {
        if (page == null) {
            return empty();
        }
        return of(page.getRecords(), page);
    }

    /**
     * 基于内存列表做假分页
     */
    public static <L> PageResult<L> build(int page, int pageSize, List<L> list) {
        if (CollUtil.isEmpty(list)) {
            return empty(page, pageSize);
        }
        long total = list.size();
        int fromIndex = (page - 1) * pageSize;
        List<L> pageList;
        if (fromIndex >= list.size()) {
            pageList = new ArrayList<>(0);
        } else {
            int toIndex = Math.min(fromIndex + pageSize, list.size());
            pageList = list.subList(fromIndex, toIndex);
        }
        return new PageResult<>(pageList, total, page, pageSize);
    }

    /**
     * 基于 MyBatis Plus 分页数据构建，并通过转换函数映射列表元素
     */
    public static <T, V> PageResult<V> build(IPage<T> page, Function<List<T>, List<V>> converter) {
        if (page == null) {
            return empty();
        }
        List<T> source = page.getRecords();
        List<V> records = CollUtil.isEmpty(source) ? Collections.emptyList() : converter.apply(source);
        return of(records, page);
    }

    private static <L> PageResult<L> of(List<L> records, IPage<?> page) {
        return new PageResult<>(records, page.getTotal(), page.getCurrent(), page.getSize());
    }

    private static <L> PageResult<L> empty() {
        return empty(1, 10);
    }

    private static <L> PageResult<L> empty(long page, long pageSize) {
        return new PageResult<>(Collections.emptyList(), 0L, page, pageSize);
    }

    private static long calcPages(long total, long pageSize) {
        if (pageSize <= 0) {
            return 0;
        }
        return (total + pageSize - 1) / pageSize;
    }
}
