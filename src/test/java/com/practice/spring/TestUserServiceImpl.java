package com.practice.spring;

import com.practice.spring.user.domain.User;
import com.practice.spring.user.service.UserServiceImpl;

public class TestUserServiceImpl extends UserServiceImpl {

    private String id = "madnite1";

    protected void upgradeLevel(User user) {
        if (user.getId().equals(this.id)) throw new TestUserServiceException();
        super.upgradeLevels();
    }
}
