
package top.wyhao.starter.tenant.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import top.wyhao.starter.tenant.config.TenantProperties;
import top.wyhao.starter.tenant.config.TenantProvider;
import top.wyhao.starter.tenant.context.TenantContextHolder;

/**
 * 租户拦截器
 */
public class TenantInterceptor implements HandlerInterceptor {

    private final TenantProperties tenantProperties;
    private final TenantProvider tenantProvider;

    public TenantInterceptor(TenantProperties tenantProperties, TenantProvider tenantProvider) {
        this.tenantProperties = tenantProperties;
        this.tenantProvider = tenantProvider;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 设置上下文
        String tenantId = request.getHeader(tenantProperties.getTenantIdHeader());
        if(tenantId != null){
            TenantContextHolder.setContext(tenantProvider.getByTenantId(tenantId, true));
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e) {
        // 清除上下文
        TenantContextHolder.clear();
    }
}