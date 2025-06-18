package com.tourismwebsite.dao.impl;

import com.tourismwebsite.constant.Constant;
import com.tourismwebsite.dao.UserDao;
import com.tourismwebsite.domain.User;
import com.tourismwebsite.utils.JDBCUtil;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @Author j
 * @Date 2023/8/24 17:27
 * @Version 1.0
 */
public class UserDaoImpl implements UserDao {
    private JdbcTemplate template = new JdbcTemplate(JDBCUtil.getDataSource());
    @Override
    public int register(User user) {

        String sql = "insert into tab_user values(null,?,?,?,?,?,?,?,?,?)";
        int i = 0;
        try {
            i = template.update(sql, user.getUsername(), user.getPassword(), user.getName(), user.getBirthday(), user.getSex(), user.getTelephone(), user.getEmail(), user.getStatus(), user.getCode());
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return i;
    }

    @Override
    public User findUsername(String username) {
        String sql = "select * from tab_user where username = ?";
        User user;
        try {
            user = template.queryForObject(sql, new BeanPropertyRowMapper<>(User.class), username);
        } catch (DataAccessException e) {
            return null;
        }

        return user;
    }

    @Override
    public int active(String code) {
        String sql = "update tab_user set status = ? where code = ?";
        int i = 0;
        try {
            i = template.update(sql,Constant.ACTIVED,code);
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return i;
    }

    @Override
    public User findCodeByUser(String code) {

        String sql = "select * from tab_user where code = ?";
        User user = null;
        try {
            user = template.queryForObject(sql, new BeanPropertyRowMapper<>(User.class), code);
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public void saveToken(String token,User findUser) {
        String sql = "update tab_user set token = ? where uid = ?";
        template.update(sql,token,findUser.getUid());

    }

    @Override
    public User findByUsernameAndToken(String username, String token) {


        String sql = "select * from tab_user where username = ? and token = ?";
        User user = null;
        try {
            user = template.queryForObject(sql, new BeanPropertyRowMapper<>(User.class), username,token);
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return user;
        
    }


}
