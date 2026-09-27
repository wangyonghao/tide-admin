package top.wyhao.file.infrastructure.config;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * 文件核心配置
 *
 * <p>全局 MyBatis {@code mapper-package} 仅扫描 {@code **.mapper}，
 * 显式扫描本模块 Mapper 包。
 *
 * @author wyh
 * @since 2026/09/16
 */
@Configuration
@MapperScan(basePackages = "top.wyhao.file.infrastructure.persistence.mapper", annotationClass = Mapper.class)
public class FileCoreConfiguration {
}
