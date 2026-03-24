package com.kxzhu.photoapp.api.users.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kxzhu.photoapp.api.users.service.UsersService;
import com.kxzhu.photoapp.api.users.shared.UserDto;
import com.kxzhu.photoapp.api.users.ui.model.LoginRequestModel;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;

/**
 * @ClassName AuthenticationFilter
 * @Description 继承的类UsernamePasswordAuthenticationFilter
 * @Author zhukexin
 * @Date 2024-06-25 3:20 PM
 */
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private UsersService usersService;
    private Environment environment;

    public AuthenticationFilter(AuthenticationManager authenticationManager, UsersService usersService, Environment environment){
        super(authenticationManager);
        this.usersService = usersService;
        this.environment = environment;
    }

    //读入账号密码，并可以调用anthenticate方法
    // 用户login时，Spring框架调用此方法
    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
            throws AuthenticationException {
        try {
            LoginRequestModel creds = new ObjectMapper().readValue(req.getInputStream(), LoginRequestModel.class); //读入账号密码

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(creds.getEmail(), creds.getPassword(), new ArrayList<>()));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 用户login时执行attemptAuthentication方法后，如果成功，则执行successfulAuthentication方法
     * 这个方法的任务是，拿到user detail，生成JWT access token，并加到http响应头，并返回
     * @param req
     * @param res
     * @param chain
     * @param auth 刚登陆成功的user
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res, FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {
        // 1. 从认证对象中获取已登录用户的 username（email）
        String username = ((User) auth.getPrincipal()).getUsername();// 没有直接getId的，所以先getUsername，再查id
        // 2. 通过 email 查询数据库获取用户详情（获取 public userId）
        UserDto userDto = usersService.getUserDetailsByEmail(username);
        // 3. 读取签名密钥并 Base64 解码
        String tokenSecret = environment.getProperty("token.secret");
        byte[] secretKeyBytes = Base64.getEncoder().encode(tokenSecret.getBytes());
        SecretKey secretKey = new SecretKeySpec(secretKeyBytes, SignatureAlgorithm.HS256.getJcaName());

        // 4. 构建 JWT Token
        String token = Jwts.builder()
                .setSubject(userDto.getUserId())
                .setExpiration(Date.from(Instant.now().plusMillis(Long.parseLong(environment.getProperty("token.expiration_time")))))
                .setIssuedAt(Date.from(Instant.now()))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        //5. 将 JWT Token 和 userId 写入 HTTP 响应头
        res.addHeader("token", token);
        res.addHeader("userId", userDto.getUserId());
    }
}
