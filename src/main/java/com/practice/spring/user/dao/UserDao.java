package com.practice.spring.user.dao;

import com.practice.spring.user.domain.User;

import java.util.List;

public interface UserDao {

    void add(User user);

    User get(String id);

    List<User> getAll();

    void deleteAll();

    int getCount();
}
