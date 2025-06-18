package com.tourismwebsite.dao;


import com.tourismwebsite.domain.User;

/**
 * @Author j
 * @Date 2023/8/24 17:27
 * @Version 1.0
 */
public interface UserDao {
    int register(User user);

    User findUsername(String username);

    int active(String code);

    User findCodeByUser(String code);

    void saveToken(String token,User findUser);

    User findByUsernameAndToken(String username, String token);
}
