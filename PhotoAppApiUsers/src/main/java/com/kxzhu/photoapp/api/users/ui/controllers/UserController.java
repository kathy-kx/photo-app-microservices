package com.kxzhu.photoapp.api.users.ui.controllers;

import com.kxzhu.photoapp.api.users.service.UsersService;
import com.kxzhu.photoapp.api.users.shared.UserDto;
import com.kxzhu.photoapp.api.users.ui.model.CreateUserRequestModel;
import com.kxzhu.photoapp.api.users.ui.model.CreateUserResponseModel;
import com.kxzhu.photoapp.api.users.ui.model.UserResponseModel;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName UserController
 * @Description TODO
 * @Author zhukexin
 * @Date 2024-06-03 4:35 PM
 */
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private Environment env;

    @Autowired
    UsersService usersService;
    @RequestMapping(method = RequestMethod.GET, path = "/status/check")
    public String status(){
        return "Working on port " + env.getProperty("local.server.port") + ", with token = " + env.getProperty("token.secret");
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<CreateUserResponseModel> createUser(@Valid @RequestBody CreateUserRequestModel userDetails){
        // 要用service层的createUser()方法，需要userDto对象：
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT); //字段匹配策略
        UserDto userDto = modelMapper.map(userDetails, UserDto.class); // userDto此时用于在Presentation层和service层之间传递数据

        UserDto createdUser = usersService.createUser(userDto);
        CreateUserResponseModel returnValue = modelMapper.map(createdUser, CreateUserResponseModel.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(returnValue);
    }

    /**
     * Get user details
     * 其中包含了albums list。这是user微服务和album微服务的通信
     * @param userId
     * @return
     */
    @GetMapping(value = "/{userId}", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UserResponseModel> getUser(@PathVariable("userId") String userId){
        UserDto userDto = usersService.getUserByUserId(userId);
        UserResponseModel returnValue = new ModelMapper().map(userDto, UserResponseModel.class);
        return ResponseEntity.status(HttpStatus.OK).body(returnValue);
    }
}
