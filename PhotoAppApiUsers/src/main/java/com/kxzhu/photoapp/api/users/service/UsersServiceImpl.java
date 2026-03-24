package com.kxzhu.photoapp.api.users.service;

import com.kxzhu.photoapp.api.users.data.AlbumsServiceClient;
import com.kxzhu.photoapp.api.users.data.UserEntity;
import com.kxzhu.photoapp.api.users.data.UsersRepository;
import com.kxzhu.photoapp.api.users.shared.UserDto;
import com.kxzhu.photoapp.api.users.ui.model.AlbumResponseModel;
import feign.FeignException;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MatchingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @ClassName UsersServiceImpl
 * @Description TODO
 * @Author zhukexin
 * @Date 2024-06-13 4:07 PM
 */
@Service
public class UsersServiceImpl implements UsersService{

    UsersRepository usersRepository;

    BCryptPasswordEncoder bCryptPasswordEncoder;
    //RestTemplate restTemplate;
    AlbumsServiceClient albumsServiceClient;

    Environment env;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired //构造器注入
    public UsersServiceImpl(UsersRepository usersRepository, BCryptPasswordEncoder bCryptPasswordEncoder, AlbumsServiceClient albumsServiceClient, Environment env){
        this.usersRepository = usersRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        //this.restTemplate = restTemplate;
        this.albumsServiceClient = albumsServiceClient;
        this.env = env;
    }

    @Override
    public UserDto createUser(UserDto userDetails) {
        userDetails.setUserId(UUID.randomUUID().toString());
        // 将明文密码"test"转为加密密码，并存入userDetails对象中
        userDetails.setEncryptedPassword(bCryptPasswordEncoder.encode(userDetails.getPassword()));

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT); //字段匹配策略
        UserEntity userEntity = modelMapper.map(userDetails, UserEntity.class);//这个方法类似BeanUtils.copyProperties()
        // userEntity用于将数据保存到表中（repository层）
        // 新造一个UserEntity对象，主键id会自动生成并自增。所以此时userEntity对象中的id已经有值了

        usersRepository.save(userEntity);

        UserDto returnValue = modelMapper.map(userEntity, UserDto.class);

        return returnValue;
    }

    @Override
    public UserDto getUserDetailsByEmail(String email) {
        UserEntity userEntity = usersRepository.findByEmail(email);
        if(userEntity == null){
            throw new UsernameNotFoundException(email);
        }
        // map UserEntity -> UserDto
        return new ModelMapper().map(userEntity, UserDto.class);
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        UserEntity userEntity = usersRepository.findByUserId(userId);
        if(userEntity == null){
            throw new UsernameNotFoundException("User not found");
        }
        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);

        //Use REST template
        // send request to albums 微服务的users/{id}/albums 端点
        //String albumsUrl = String.format(env.getProperty("albums.url"), userId);
        //ResponseEntity<List<AlbumResponseModel>> albumsListResponse = restTemplate.exchange(albumsUrl, HttpMethod.GET, null, new ParameterizedTypeReference<List<AlbumResponseModel>>() {
        //});//将响应接收到的json（含url、method、Header/Body、参数等），映射到albumsListResponse
        //List<AlbumResponseModel> albumsList = albumsListResponse.getBody();
        //userDto.setAlbums(albumsList);

        // Use Feign Client
        //List<AlbumResponseModel> albumList = null;
        //try {
        //    albumList = albumsServiceClient.getAlbums(userId);
        //} catch (FeignException e) {
        //    //throw new RuntimeException(e);
        //    logger.error(e.getLocalizedMessage());
        //}//如果albumList查询有问题，还可以返回null而不是报FeignException

        logger.debug("Before calling album microservice");
        // 使用FeignErrorDecoder：
        List<AlbumResponseModel> albumList = albumsServiceClient.getAlbums(userId);
        logger.debug("After calling album microservice");
        userDto.setAlbums(albumList);

        return userDto;
    }

    // Spring Security Framework UserDetailsService要求的
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = usersRepository.findByEmail(username);// 本项目中 username就是email address
        if(userEntity == null){
            throw new UsernameNotFoundException(username);
        }
        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), true, true, true, true, new ArrayList<>());
    }


}
