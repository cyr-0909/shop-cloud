package com.fh.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableZuulProxy
public class ShopGatewayApp {

    public static void main(String[] args) {
        SpringApplication.run(ShopGatewayApp.class,args);
    }

    @Component
    @Primary
    class SwaggerDocumentationConfig implements SwaggerResourcesProvider {
        private final RouteLocator routeLocator;

        public SwaggerDocumentationConfig(RouteLocator routeLocator) {
            this.routeLocator = routeLocator;
        }
        @Override
        public List<SwaggerResource> get() {
            List resources = new ArrayList<>();

            List<Route> routes= routeLocator.getRoutes();
            routes.forEach(route -> {
            //从各个服务里获取文档
                resources.add(swaggerResource(route.getId(), route.getFullPath().replace("**", "v2/api-docs"), "1.0"));
            });
//            resources.add(swaggerResource("会员服务接口", "/shop-member-api/v2/api-docs", "2.0"));// /api/search/是网关路由，/v2/api-docs是swagger中的
//            resources.add(swaggerResource("分类服务接口", "/shop-cate-api/v2/api-docs", "1.0"));
            return resources;
        }

        private SwaggerResource swaggerResource(String name, String location, String version) {
            SwaggerResource swaggerResource = new SwaggerResource();
            swaggerResource.setName(name);
            swaggerResource.setLocation(location);
            swaggerResource.setSwaggerVersion(version);
            return swaggerResource;
        }
}
}
