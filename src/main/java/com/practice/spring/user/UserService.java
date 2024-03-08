package com.practice.spring.user;

import com.practice.spring.user.dao.UserDao;
import com.practice.spring.user.domain.Level;
import com.practice.spring.user.domain.User;

import java.util.List;

public class UserService {

    UserDao userDao;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void upgradeLevels() {
        List<User> users = userDao.getAll();

        users.forEach(e -> {
            Boolean changed = null;

            if (e.getLevel().equals(Level.BASIC) && e.getLogin() >= 50) {
                e.setLevel(Level.SILVER);
                changed = true;
            } else if (e.getLevel().equals(Level.SILVER) && e.getRecommend() >= 30) {
                e.setLevel(Level.GOLD);
                changed = true;
            } else if (e.getLevel().equals(Level.GOLD)) {
                changed = false;
            } else {
                changed = false;
            }

            if (changed) {
                userDao.update(e);
            }
        });
    }

    public void add(User user) {
        if (user.getLevel() == null) {
            user.setLevel(Level.BASIC);
        }
        userDao.add(user);
    }
}
