package com.kxzhu.photoapp.api.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Set;

/**
 * @ClassName MyPreFilter
 * @Description Spring Cloud会自动识别这是pre-filter逻辑还是post-filter逻辑。自己不需要指定
 * @Author zhukexin
 * @Date 2026-03-14 14:06
 */
@Component //让Spring framework notice this class and place it into application context.
public class MyPreFilter implements GlobalFilter, Ordered {
    final Logger logger = LoggerFactory.getLogger(MyPreFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // ---- Pre Filter 逻辑写在这里（路由之前执行）----
        logger.info("My first pre-filter is executed...");

        // 读取请求 URL 路径
        String requestPath = exchange.getRequest().getPath().toString();
        logger.info("request path = " + requestPath);

        // 遍历并打印所有请求头
        HttpHeaders headers = exchange.getRequest().getHeaders();
        Set<String> headerNames = headers.keySet();
        headerNames.forEach(headerName -> {
            String headerValue = headers.getFirst(headerName);
            logger.info(headerName + " : " + headerValue);
        });

        // 必须调用 chain.filter(exchange) 将请求传递给下一个过滤器
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0; // 最小值，Pre Filter 最先执行；Post Filter 最后执行
    }
}
