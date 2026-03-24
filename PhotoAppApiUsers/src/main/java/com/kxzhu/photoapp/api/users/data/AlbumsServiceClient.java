package com.kxzhu.photoapp.api.users.data;

import com.kxzhu.photoapp.api.users.ui.model.AlbumResponseModel;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName AlbumsServiceClient
 * @Description TODO
 * @Author zhukexin
 * @Date 2026-03-18 15:37
 */
@FeignClient (name = "albums-ws") // 想要通信的微服务名
public interface AlbumsServiceClient {
    @GetMapping(value = "/users/{id}/albums")
    @Retry(name = "albums-ws")
    @CircuitBreaker(name = "albums-ws", fallbackMethod = "getAlbumsFallback")
    public List<AlbumResponseModel> getAlbums(@PathVariable String id);

      // ===== Fallback 方法（降级方法）=====
              // 规则：
              //  1. 必须定义在同一接口/类中
              //  2. 方法名与 fallbackMethod 参数一致
              //  3. 方法签名与原方法相同（参数列表）
              //  4. 额外追加一个 Throwable 参数（接收异常信息）
              //  5. 返回类型与原方法相同
    default List<AlbumResponseModel> getAlbumsFallback(String id, Throwable exception){
         System.out.println("Param = " + id);
         System.out.println("Exception took place: " + exception.getMessage());
         return new ArrayList<>();
    }
}
