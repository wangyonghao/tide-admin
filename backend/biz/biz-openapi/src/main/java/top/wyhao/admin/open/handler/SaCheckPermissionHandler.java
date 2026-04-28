
package top.wyhao.admin.open.handler;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.handler.SaAnnotationHandlerInterface;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.sign.template.SaSignTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.AnnotatedElement;
import java.util.Collection;

import static cn.dev33.satoken.annotation.handler.SaCheckPermissionHandler._checkMethod;

/**
 * 重定义注解 SaCheckPermission 的处理器
 */
@Component
public class SaCheckPermissionHandler implements SaAnnotationHandlerInterface<SaCheckPermission> {

    @Override
    public Class<SaCheckPermission> getHandlerAnnotationClass() {
        return SaCheckPermission.class;
    }

    @Override
    public void checkMethod(SaCheckPermission saCheckPermission, AnnotatedElement annotatedElement) {
        if (!isSignParamExists()) {
            _checkMethod(saCheckPermission.type(), saCheckPermission.value(), saCheckPermission
                .mode(), saCheckPermission.orRole());
        }
    }

    /**
     * 判断请求是否包含sign参数
     *
     * @return 是否包含sign参数（true：包含；false：不包含）
     */
    public static boolean isSignParamExists() {
        SaRequest saRequest = SaHolder.getRequest();
        Collection<String> paramNames = saRequest.getParamNames();
        return paramNames.stream().anyMatch(SaSignTemplate.sign::equals);
    }
}
