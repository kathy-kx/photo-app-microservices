package com.kxzhu.photoapp.api.users.ui.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName LoginRequestModel
 * @Description TODO
 * @Author zhukexin
 * @Date 2024-06-25 3:17 PM
 */

@Data
@NoArgsConstructor
public class LoginRequestModel {
    @NotNull(message = "Password name cannot be missing or empty")
    @Size(min = 8, max = 16, message="Password must be equal to or greater than 8 characters and less than 16 characters")
    private String password;

    @NotNull(message = "Email name cannot be missing or empty")
    @Email
    private String email;
}
