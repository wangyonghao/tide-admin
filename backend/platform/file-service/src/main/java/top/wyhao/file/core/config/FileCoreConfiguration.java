package top.wyhao.file.core.config;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * 文件核心配置
 *
 * <p>全局 MyBatis {@code mapper-package} 仅扫描 {@code **.mapper}，
 * 本模块 Mapper 位于 {@code repository} 包，需单独注册扫描。
 *
 * @author wyh
 * @since 2026/09/16
 */
@Configuration
@MapperScan(basePackages = "top.wyhao.file.core.repository", annotationClass = Mapper.class)
public class FileCoreConfiguration {
}
