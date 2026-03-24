package com.kxzhu.photoapp.api.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @ClassName MyPostFilter
 * @Description TODO
 * @Author zhukexin
 * @Date 2026-03-14 14:06
 */
@Component //让Spring framework notice this class and place it into application context.
public class MyPostFilter implements GlobalFilter, Ordered {
    final Logger logger = LoggerFactory.getLogger(MyPostFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            // ---- Post Filter 逻辑写在这里（路由之后执行）----
            logger.info("Global post filter is executed..");
     }));
    }

    @Override
    public int getOrder() {
        return 0; // 最小值，Pre Filter 最先执行；Post Filter 最后执行
    }
}
