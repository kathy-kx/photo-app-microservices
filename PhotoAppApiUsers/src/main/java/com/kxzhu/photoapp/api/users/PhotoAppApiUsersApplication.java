package com.kxzhu.photoapp.api.users;

import com.kxzhu.photoapp.api.users.shared.FeignErrorDecoder;
import feign.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients   // 新增：启用 Feign Client 支持
public class PhotoAppApiUsersApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhotoAppApiUsersApplication.class, args);
    }

    /**
     * @Bean 注解可以让Spring框架在执行启动服务时，执行本方法。
     * 并将返回值放在Application context中。
     * 后续可以将放在Application context中的对象注入到service类中
     * @return
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // 新增：为 httpexchanges 端点（actuator监控用）注册存储 Bean
    @Bean
    public HttpExchangeRepository httpExchangeRepository() {
        return new InMemoryHttpExchangeRepository();
    }

    // 新增：为RestTemplate用
    @Bean
    @LoadBalanced // enable client side load balancing for rest template
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;   // 日志级别
    }

    //@Bean
    //public FeignErrorDecoder getFeignErrorDecoder(){
    //    return new FeignErrorDecoder();
    //}

}
