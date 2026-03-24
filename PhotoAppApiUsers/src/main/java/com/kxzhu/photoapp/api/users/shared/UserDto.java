package com.kxzhu.photoapp.api.users.shared;

import com.kxzhu.photoapp.api.users.ui.model.AlbumResponseModel;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName UserDto
 * @Description UserDto用来在Presentation层(controller)、Service层和Repository层之间传递数据
 * @Author zhukexin
 * @Date 2024-06-13 4:07 PM
 */
public class UserDto implements Serializable { //Dto设计模式需要实现Serializable
    private static final long serialVersionUID = -4508212450650592231L;

    private String firstName;

    private String lastName;

    private String password;

    private String email;

    private String userId;

    private String encryptedPassword;

    private List<AlbumResponseModel> albums;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public List<AlbumResponseModel> getAlbums() {
        return albums;
    }

    public void setAlbums(List<AlbumResponseModel> albums) {
        this.albums = albums;
    }
}
