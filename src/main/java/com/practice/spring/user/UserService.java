package com.practice.spring.user;

import com.practice.spring.user.domain.User;

public interface UserService {

    void add(User user);
    void upgradeLevels();
}
