package com.kxzhu.photoapp.api.users.shared;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/**
 * @ClassName FeignErrorDecoder
 * @Description TODO
 * @Author zhukexin
 * @Date 2026-03-18 17:04
 */
@Component
public class FeignErrorDecoder implements ErrorDecoder {
    Environment env;

    @Autowired
    public FeignErrorDecoder(Environment env) {
        this.env = env;
    }

    @Override
    public Exception decode(String methodKey, Response response) {
        switch (response.status()){
            case 400:
                //Do something like return new BadRequestException();
                break;
            case 404:
                if(methodKey.contains("getAlbums")){
                    return new ResponseStatusException(HttpStatusCode.valueOf(response.status()), env.getProperty("albums.exceptions.albums-not-found"));
                }
                break;
            default:
                return new Exception(response.reason());
        }

        return null;
    }
}
