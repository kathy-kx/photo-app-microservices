package com.kxzhu.photoapp.api.users.ui.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName CreateUserRequestModel
 * @Description TODO
 * @Author zhukexin
 * @Date 2024-06-13 3:27 PM
 */
@Data
@NoArgsConstructor
public class CreateUserRequestModel {
    @NotNull(message = "First name cannot be missing or empty")
    @Size(min = 2, message = "First name cannot be less than 2 characters")
    private String firstName;

    @NotNull(message = "Last name cannot be missing or empty")
    @Size(min = 2, message = "Last name cannot be less than 2 characters")
    private String lastName;

    @NotNull(message = "Password name cannot be missing or empty")
    @Size(min = 8, max = 16, message="Password must be equal to or greater than 8 characters and less than 16 characters")
    private String password;

    @NotNull(message = "Email name cannot be missing or empty")
    @Email
    private String email;
}
