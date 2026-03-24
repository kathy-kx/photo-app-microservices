package com.kxzhu.photoapp.api.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import reactor.core.publisher.Mono;

/**
 * @ClassName GlobalFiltersConfiguration
 * @Description TODO
 * @Author zhukexin
 * @Date 2026-03-14 14:07
 */

@Configuration
public class GlobalFiltersConfiguration {

    final Logger logger = LoggerFactory.getLogger(GlobalFiltersConfiguration.class);
    @Bean
    @Order(1)
    public GlobalFilter secondPreFilter(){
        return (exchange, chain) -> {
            logger.info("My second global pre-filter is executed..");
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                logger.info("My second global post-filter is executed..");
            }));
        };
    }

    @Bean
    @Order(2)
    public GlobalFilter thirdPreFilter(){
        return (exchange, chain) -> {
            logger.info("My third global pre-filter is executed..");
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                logger.info("My third global post-filter is executed..");
            }));
        };
    }

    @Bean
    @Order(3)
    public GlobalFilter fourthPreFilter(){
        return (exchange, chain) -> {
            logger.info("My fourth global pre-filter is executed..");
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                logger.info("My fourth global post-filter is executed..");
            }));
        };
    }
}
