package com.tourismwebsite.service;


import com.tourismwebsite.domain.User;
import com.tourismwebsite.exception.PasswordErrorException;
import com.tourismwebsite.exception.UserNameNotExistsException;

/**
 * @Author j
 * @Date 2023/8/24 17:24
 * @Version 1.0
 */
public interface UserService {
    boolean register(User user) throws Exception;

    User findUsername(String username);

    boolean active(String code);

    User doLogin(String username,String password) throws Exception;

}
