package com.kxzhu.photoapp.api.users.security;

import com.kxzhu.photoapp.api.users.service.UsersService;
import com.kxzhu.photoapp.api.users.service.UsersServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Map;

/**
 * @ClassName WebSecurity
 * @Description TODO
 * @Author zhukexin
 * @Date 2024-06-14 5:35 PM
 */
@Configuration
@EnableWebSecurity
public class WebSecurity {

    private Environment environment;
    private UsersService usersService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public WebSecurity(Environment environment, UsersService usersService, BCryptPasswordEncoder bCryptPasswordEncoder){
        this.environment = environment;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.usersService = usersService;
    }

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception{
        // 1、获取 AuthenticationManager
        // AuthenticationManager 是 Spring Security的认证核心，负责验证用户名/密码是否正确。
        //  后面 AuthenticationFilter 需要用它来做登录验证，所以先在这里拿到。
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        // Configure AuthenticationManagerBuilder
        authenticationManagerBuilder.userDetailsService(usersService).passwordEncoder(bCryptPasswordEncoder);//Tell Spring Framework which service contains the method that will be used to look up a user in a database
        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        // Create AuthenticationFilter
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager, usersService, environment);
        authenticationFilter.setFilterProcessesUrl(environment.getProperty("login.url.path"));

        http.csrf().disable();

        //支持通过环境变量配置，Docker 里可以传入网段
        String gatewayIp = environment.getProperty("gateway.ip", "127.0.0.1");
        // 2、请求授权规则
        http.authorizeHttpRequests()
                // 规则1：GET /users/status/check 允许访问
                .requestMatchers(HttpMethod.GET, "/users/status/check").permitAll()
                // 规则2：POST /users 只允许来自网关IP的请求 注册用户
                //.requestMatchers(HttpMethod.POST, "/users").access(new WebExpressionAuthorizationManager("hasIpAddress('"+ environment.getProperty("gateway.ip")+"')"))//参数是一个安全表达式security expression。如果hasIpAddress('10.0.0.13')为true，则请求会被允许；否则拒绝请求。填API gateway的ip
                .requestMatchers(HttpMethod.POST, "/users").access(new WebExpressionAuthorizationManager("hasIpAddress('" + gatewayIp + "')"))
                //规则3：H2控制台完全开放
                .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
                //规则4：监控actuator开放
                .requestMatchers(new AntPathRequestMatcher("/actuator/**")).access(new WebExpressionAuthorizationManager("hasIpAddress('"+ environment.getProperty("gateway.ip")+"')"))
                // 允许返回的错误信息。否则Spring Boot 错误处理内部 forward 到 /error 时会被 Security 拦截返回 403
                .requestMatchers(new AntPathRequestMatcher("/error")).permitAll()
                // 允许GET请求/users/**
                .requestMatchers(HttpMethod.GET, "/users/**").access(new WebExpressionAuthorizationManager("hasIpAddress('" + environment.getProperty("gateway.ip") + "')")) // 网关已经验证了 JWT，微服务直接信任网关来的请求
                // 兜底规则：其他所有请求需要认证。不写也是Spring Security默认的，推荐显式写出
                .anyRequest().authenticated()
                .and()
                // 4、添加登录过滤器
                .addFilter(authenticationFilter) // 把自定义的 AuthenticationFilter 加入 Spring Security 过滤器链。其继承 UsernamePasswordAuthenticationFilter，默认监听/login POST。用户登陆时自动触发
                .authenticationManager(authenticationManager)//设置authenticationManager为该HttpSecurity对象默认的authenticationManager
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);//不创建session，并标记为stateless

        http.headers().frameOptions().disable();//H2 控制台页面用了 <iframe>，浏览器默认会被 Spring Security 的 X-Frame-Options头拦截.禁用后, H2 页面才能正常显示。

        return http.build();
    }
}
