package com.practice.spring;

import com.practice.spring.user.UserService;
import com.practice.spring.user.dao.UserDao;
import com.practice.spring.user.domain.User;
import com.practice.spring.user.policy.UserLevelUpgradeNormal;

public class TestUserLevelUpgradePolicy extends UserLevelUpgradeNormal {

    private String id;

    public TestUserLevelUpgradePolicy(String id, UserDao userDao) {
        this.id = id;
        super.setUserDao(userDao);
    }

    @Override
    public void upgradeLevel(User user) {
        if (user.getId().equals(this.id)) {
            throw new TestUserServiceException();
        }
        super.upgradeLevel(user);
    }
}
