package com.kxzhu.photoapp.api.gateway;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * @ClassName AuthorizationHeaderFilter
 * @Description TODO
 * @Author zhukexin
 * @Date 2026-03-12 18:14
 */
@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {
    @Autowired
    Environment env;

    public AuthorizationHeaderFilter() {
        super(Config.class);
    }

    // 内部类
    public static class Config{
        // put configuration properties here
    }

    /**
     *
     * @param config
     * @return 返回的是GatewayFilter类型，它是一个接口，且只有一个抽象方法。
     * 我们可以用java lambda来简化，重写filter方法，实现 GatewayFilter 接口
     *
     */
    @Override
    public GatewayFilter apply(Config config) {
        /*
        从exchange对象中可以读取http request对象，从该对象中可读取 header中的authentication http header
        chain是gateway filter chain.可以delegate the flow to the next filter（将流程委托给下一个“filter”。）
         */
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            // 1. 检查 Authorization 头是否存在
            // 如果header里没有Authorization这项，返回error
            if(!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)){
                return onError(exchange, "No authorization header", HttpStatus.UNAUTHORIZED);
            }
            String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);// get authorization header (bearer + jwt token)
            // 2. 读取 Authorization 头的值并去掉 "Bearer " 前缀
            // to validate the jwt token, we need to extract from header
            String jwt = authorizationHeader.replace("Bearer ", "");

            // 3. 验证 JWT Token
            // 还需要validate the jwt token。单独写一个方法
            if(!isJwtValid(jwt)){
                return onError(exchange, "JWT token is not valid", HttpStatus.UNAUTHORIZED);
            }
            // 4. 验证通过，继续执行过滤器链（路由到下游微服务）
            return chain.filter(exchange);
        };
    }

    /*
        validate the jwt token是否合法
        如果签名正确、token没过期、toekn中有用户id（subject），就返回true
     */
    private boolean isJwtValid(String jwt){
        boolean returnValue = true;
        // 从配置文件env 读取 token.secret，并 Base64 编码
        String tokenSecret = env.getProperty("token.secret");
        // use tokenSecret密钥 to create a signingKey
        byte[] secretKeyBytes = Base64.getEncoder().encode(tokenSecret.getBytes());
        SecretKey signingKey = new SecretKeySpec(secretKeyBytes, SignatureAlgorithm.HS256.getJcaName());
        // 用signingKey解析JWT：构建 JWT 解析器并验证签名，提取 subject（用户 ID）
        String subject = null;

        JwtParser jwtParser = Jwts.parserBuilder() // 创建一个JWT parser，并告诉它：用signingKey验证token
                .setSigningKey(signingKey)
                .build();
        /*
            这个JWT parser会负责：
            1 解码 JWT
            2 验证签名
            3 检查过期时间
            4 返回 payload
         */

        // 判断token是否有效
        try {
            Jwt<Header, Claims> parsedToken = jwtParser.parse(jwt); // 解析JWT token
            subject = parsedToken.getBody().getSubject(); // 获取用户id
        }catch (Exception e){
            // 签名不匹配、Token 过期等异常，均视为无效
            returnValue = false;
        }
        // subject 为空也视为无效。系统通常约定 subject = userId
        if(subject == null || subject.isEmpty()){
            returnValue = false;
        }

        return returnValue;
    }


    // 统一错误响应处理
    private Mono<Void> onError(ServerWebExchange exchange,
                              String err,
                              HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }
}
