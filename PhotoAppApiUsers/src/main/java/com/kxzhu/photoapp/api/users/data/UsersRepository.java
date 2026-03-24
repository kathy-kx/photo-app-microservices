package com.kxzhu.photoapp.api.users.data;

import org.springframework.data.repository.CrudRepository;

/**
 * @ClassName UsersRepository
 * @Description TODO
 * @Author zhukexin
 * @Date 2024-06-13 5:36 PM
 */
public interface UsersRepository extends CrudRepository<UserEntity, Long> {
    // CrudRepository 已提供 save、findById、delete 等方法，无需手写

    // own method: find by xxx
    UserEntity findByEmail(String email);

    UserEntity findByUserId(String userId);
}
