package com.graduationprojectordermanagementsystem.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableKnife4j
public class SwaggerConfig {
//    @Bean
//    public OpenAPI openAPI() {
//        return new OpenAPI()
//                .info(new Info().title("API文档").version("1.0"))
//                // 添加安全协议（若使用JWT）
//                .addSecurityItem(new SecurityRequirement().addList("JWT"))
//                .components(new Components()
//                        .addSecuritySchemes("JWT",
//                                new SecurityScheme()
//                                        .type(SecurityScheme.Type.HTTP)
//                                        .scheme("bearer")
//                                        .bearerFormat("JWT")
//                        )
//                );
//    }
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("毕设接单平台API文档标题")
                        .version("1.0")
                        .description("具体接口文档描述")
                        .license(new License().name("Apache 2.0")));
    }
    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("user")
                .pathsToMatch("/api/user/**") // 匹配控制器中定义的接口路径
                .packagesToScan("com.graduationprojectordermanagementsystem.controller.user")
                .build();
    }
    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("admin")
                .pathsToMatch("/api/admin/**") // 匹配控制器中定义的接口路径
                .packagesToScan("com.graduationprojectordermanagementsystem.controller.admin")
                .build();
    }
}
