package com.practice.spring.common.config;

import com.practice.spring.common.database.ConnectionMaker;
import com.practice.spring.common.database.SimpleConnectionMaker;
import com.practice.spring.user.dao.UserDao;

public class DaoFactory {
    public UserDao userDao() {
        return new UserDao(connectionMaker());
    }

    public ConnectionMaker connectionMaker() {
        return new SimpleConnectionMaker();
    }
}
