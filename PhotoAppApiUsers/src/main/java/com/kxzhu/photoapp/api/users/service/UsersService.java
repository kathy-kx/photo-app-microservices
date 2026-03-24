package com.kxzhu.photoapp.api.users.service;

import com.kxzhu.photoapp.api.users.shared.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 * @ClassName UsersService
 * @Description UserDetailsService comes from Spring Security framework
 * @Author zhukexin
 * @Date 2024-06-13 4:04 PM
 */

public interface UsersService extends UserDetailsService {
    // UserDto用来在Presentation层(controller)、Service层和Repository层之间传递数据
    UserDto createUser(UserDto userDetails);
    UserDto getUserDetailsByEmail(String email);

    UserDto getUserByUserId(String userId);

}
