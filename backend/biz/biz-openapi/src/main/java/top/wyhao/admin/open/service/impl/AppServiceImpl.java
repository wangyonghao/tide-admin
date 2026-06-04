
package top.wyhao.admin.open.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.wyhao.admin.open.mapper.SysAppMapper;
import top.wyhao.admin.open.model.entity.SysApp;
import top.wyhao.admin.open.model.query.AppQuery;
import top.wyhao.admin.open.model.req.AppReq;
import top.wyhao.admin.open.model.resp.AppDetailResp;
import top.wyhao.admin.open.model.resp.AppResult;
import top.wyhao.admin.open.model.resp.AppSecretResp;
import top.wyhao.admin.open.service.AppService;
import top.wyhao.cmn.db.util.WrapperUtil;
import top.wyhao.starter.core.constant.StringConstants;
import top.wyhao.starter.excel.util.ExcelUtils;
import top.wyhao.starter.web.core.model.PageQuery;
import top.wyhao.starter.web.core.model.PageResult;

import java.util.List;

/**
 * 应用业务实现


 * @since 2024/10/17 16:03
 */
@Service
public class AppServiceImpl implements AppService {

    private SysAppMapper baseMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(AppReq req) {
        req.setAccessKey(Base64.encode(IdUtil.fastSimpleUUID())
                .replace(StringConstants.SLASH, StringConstants.EMPTY)
                .replace(StringConstants.PLUS, StringConstants.EMPTY)
                .substring(0, 30));
        req.setSecretKey(this.generateSecret());

        SysApp entity = BeanUtil.copyProperties(req, SysApp.class);
        baseMapper.insert(entity);
        return entity.getId();
    }


    @Override
    public AppSecretResp getSecret(Long id) {
        SysApp app = baseMapper.selectById(id);
        AppSecretResp appSecretResp = new AppSecretResp();
        appSecretResp.setAccessKey(app.getAccessKey());
        appSecretResp.setSecretKey(app.getSecretKey());
        return appSecretResp;
    }

    @Override
    public void resetSecret(Long id) {
        SysApp app = new SysApp();
        app.setSecretKey(this.generateSecret());
        baseMapper.update(app, Wrappers.lambdaQuery(SysApp.class).eq(SysApp::getId, id));
    }

    @Override
    public SysApp getByAccessKey(String accessKey) {
        return baseMapper.selectByAccessKey(accessKey);
    }

    /**
     * 生成密钥
     *
     * @return 密钥
     */
    private String generateSecret() {
        return Base64.encode(IdUtil.fastSimpleUUID())
            .replace(StringConstants.SLASH, StringConstants.EMPTY)
            .replace(StringConstants.PLUS, StringConstants.EMPTY);
    }

    // 实现 CrudService 的其他方法
    @Override
    public PageResult<AppResult> page(AppQuery query, PageQuery pageQuery) {
        QueryWrapper<SysApp> queryWrapper = WrapperUtil.build(query);
        WrapperUtil.applySort(queryWrapper, WrapperUtil.parseSort(query.getSort()),SysApp.class);
        IPage<SysApp> page = baseMapper.selectPage(new Page<>(pageQuery.getPage(), pageQuery.getSize()), queryWrapper);
        return PageResult.build(page, AppResult.class);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(AppReq req, Long id) {
        SysApp entity = baseMapper.selectById(id);
        BeanUtil.copyProperties(req, entity, CopyOptions.create().ignoreNullValue());
        baseMapper.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<Long> ids) {
        baseMapper.deleteByIds(ids);
    }

    @Override
    public void export(AppQuery query, HttpServletResponse response) {
        QueryWrapper<SysApp> queryWrapper = WrapperUtil.build(query);
        // 设置排序
        WrapperUtil.applySort(queryWrapper, WrapperUtil.parseSort(query.getSort()), SysApp.class);
        List<SysApp> entityList = baseMapper.selectList(queryWrapper);
        List<AppDetailResp> list = BeanUtil.copyToList(entityList, AppDetailResp.class);
        ExcelUtils.export(list, "导出数据", AppDetailResp.class, response);
    }
}