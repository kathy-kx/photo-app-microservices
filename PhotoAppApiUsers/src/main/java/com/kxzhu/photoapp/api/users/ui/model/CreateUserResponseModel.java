package com.kxzhu.photoapp.api.users.ui.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName CreateUserResponseModel
 * @Description 希望将存在表中的userDetails数据，在响应体中返回给前端。且不返回敏感信息（密码等）
 * @Author zhukexin
 * @Date 2024-06-14 4:31 PM
 */
@Data
@NoArgsConstructor
public class CreateUserResponseModel {
    private String firstName;

    private String lastName;

    private String email;

    private String userId;
}
