package com.practice.spring.common.config;

import com.practice.spring.common.database.ConnectionMaker;
import com.practice.spring.common.database.CountingConnectionMaker;
import com.practice.spring.common.database.SimpleConnectionMaker;
import com.practice.spring.user.dao.UserDao;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DaoFactory {
    @Bean
    public UserDao userDao() {
        return new UserDao(connectionMaker());
    }

    @Bean
    public ConnectionMaker connectionMaker() {
        return new CountingConnectionMaker(realConnectionMaker());
    }

    @Bean
    public ConnectionMaker realConnectionMaker() {
        return new SimpleConnectionMaker();
    }
}
