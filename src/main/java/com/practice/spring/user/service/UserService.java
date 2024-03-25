package com.practice.spring.user.service;

import com.practice.spring.user.domain.User;

public interface UserService {

    void add(User user);

    void upgradeLevels();
}
