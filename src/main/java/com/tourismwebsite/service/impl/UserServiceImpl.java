package com.tourismwebsite.service.impl;

import com.tourismwebsite.constant.Constant;
import com.tourismwebsite.dao.UserDao;
import com.tourismwebsite.domain.User;
import com.tourismwebsite.exception.PasswordErrorException;
import com.tourismwebsite.exception.UserNameNotExistsException;
import com.tourismwebsite.factory.BaseFactory;
import com.tourismwebsite.service.UserService;
import com.tourismwebsite.utils.MailUtil;

import java.io.NotActiveException;

/**
 * @Author j
 * @Date 2023/8/24 17:25
 * @Version 1.0
 */
public class UserServiceImpl implements UserService {
    private UserDao userDao = (UserDao) BaseFactory.getInterface("userDao");
    @Override
    public boolean register(User user) throws Exception {
       int i = userDao.register(user);
       if (i > 0){
           MailUtil.sendMail(user.getEmail(),"Welcome "+user.getName()+" Register TourismWebsite，点击以下连接 http://localhost:8080/user?action=active&code="+user.getCode()+"'进行激活");
           return true;
       }else{
           return false;
       }
    }

    @Override
    public User findUsername(String username) {


        return  userDao.findUsername(username);
    }

    @Override
    public boolean active(String code) {

        User user = userDao.findCodeByUser(code);
        if (user != null){
            if (user.getStatus().equals(Constant.UNACTIVE)){
                int i = userDao.active(code);
                if (i == 1) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public User doLogin(String username,String password) throws Exception {
        
        User findUser = userDao.findUsername(username);

        if (findUser == null){
            throw new UserNameNotExistsException("username error !!!");
        }else{
            if (!password.equals(findUser.getPassword())) {
                throw new PasswordErrorException("password error !!!");
            }

        }
        if (findUser.getStatus().equals(Constant.UNACTIVE)){
            throw new NotActiveException("account not active !!!");
        }
        return findUser;
    }

    @Override
    public void saveToken(String token,User findUser) {
        userDao.saveToken(token,findUser);
    }

    @Override
    public User findByUsernameAndToken(String username, String token) {
        
        return userDao.findByUsernameAndToken(username,token);
    }
}
