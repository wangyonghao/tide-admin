package top.wyhao.cmn.db.query;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 统一分页响应体，避免把 MyBatis-Plus 的 Page 直接暴露给前端
 */
@Data
public class PageResult<T> {

    private List<T> records = Collections.emptyList();
    private long total;
    private long pageNum;
    private long pageSize;
    private long pages;

    public static <T> PageResult<T> of(IPage<T> page) {
        PageResult<T> r = new PageResult<>();
        r.setRecords(page.getRecords());
        r.setTotal(page.getTotal());
        r.setPageNum(page.getCurrent());
        r.setPageSize(page.getSize());
        r.setPages(page.getPages());
        return r;
    }

    public static <T> PageResult<T> empty(PageParam query) {
        PageResult<T> r = new PageResult<>();
        r.setPageNum(query.getPageNum());
        r.setPageSize(query.getPageSize());
        return r;
    }

    /**
     * 实体 -> VO 转换，省去每个 Service 手动拷贝分页元信息
     */
    public <R> PageResult<R> map(Function<T, R> converter) {
        PageResult<R> r = new PageResult<>();
        r.setRecords(records.stream().map(converter).collect(Collectors.toList()));
        r.setTotal(total);
        r.setPageNum(pageNum);
        r.setPageSize(pageSize);
        r.setPages(pages);
        return r;
    }
}
