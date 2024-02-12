package com.practice.spring.common.config;

//import com.practice.spring.common.database.DataSource;
//import com.practice.spring.common.database.CountingDataSource;
//import com.practice.spring.common.database.SimpleDataSource;
import com.practice.spring.user.dao.UserDao;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;
import java.sql.Driver;

@Configuration
public class DaoFactory {
    @Bean
    public UserDao userDao() {
        UserDao userDao = new UserDao();

        userDao.setDataSource(dataSource());

        return userDao;
//        return new UserDao(connectionMaker());
    }

//    @Bean
//    public ConnectionMaker connectionMaker() {
//        return new CountingConnectionMaker(realConnectionMaker());
//    }

    @Bean
    public DataSource dataSource() {
        var dataSource = new SimpleDriverDataSource();

        dataSource.setDriverClass(com.mysql.jdbc.Driver.class);
        dataSource.setUrl("jdbc:mysql://localhost/spring");
        dataSource.setUsername("root");
        dataSource.setPassword("fightinh");

        return dataSource;
    }

//    @Bean
//    public ConnectionMaker realConnectionMaker() {
//        return new SimpleConnectionMaker();
//    }
}
