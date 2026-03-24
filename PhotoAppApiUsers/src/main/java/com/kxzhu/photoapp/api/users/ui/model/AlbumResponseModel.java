package com.kxzhu.photoapp.api.users.ui.model;

/**
 * @ClassName AlbumResponseModel
 * @Description TODO
 * @Author zhukexin
 * @Date 2026-03-17 21:36
 */
public class AlbumResponseModel {
    String albumId;
    String userId;
    String name;
    String description;

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
