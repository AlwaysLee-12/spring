package com.practice.spring.user.policy;

import com.practice.spring.user.dao.UserDao;
import com.practice.spring.user.domain.Level;
import com.practice.spring.user.domain.User;

public class UserLevelUpgradeNormal implements UserLevelUpgradePolicy {

    UserDao userDao;

    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECOMMEND_FOR_GOLD = 30;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public boolean canUpgradeLevel(User user) {
        Level currentLevel = user.getLevel();

        switch (currentLevel) {
            case BASIC -> {
                return user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER;
            }
            case SILVER -> {
                return user.getRecommend() >= MIN_RECOMMEND_FOR_GOLD;
            }
            default -> throw new IllegalArgumentException("no such level : " + currentLevel);
        }
    }

    @Override
    public void upgradeLevel(User user) {
        user.upgradeLevel();
        userDao.update(user);
    }
}
