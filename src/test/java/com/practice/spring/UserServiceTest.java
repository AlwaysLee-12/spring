package com.practice.spring;

import com.practice.spring.user.UserService;
import com.practice.spring.user.dao.UserDao;
import com.practice.spring.user.domain.Level;
import com.practice.spring.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = "/test-application-context.xml")
public class UserServiceTest {

    @Autowired
    UserService userService;
    @Autowired
    UserDao userDao;
    List<User> users;

    //DI 잘 됐는지 확인
    @Test
    public void bean() {
        assertThat(userService).isNotNull();
    }

    @BeforeEach
    public void setUp() {
        users = List.of(
                new User("id1", "name1", "pwd1", Level.BASIC, 49, 0),
                new User("id2", "name2", "pwd2", Level.BASIC, 50, 0),
                new User("id3", "name3", "pwd3", Level.SILVER, 60, 29),
                new User("id4", "name4", "pwd4", Level.SILVER, 70, 30),
                new User("id5", "name5", "pwd5", Level.GOLD, 80, 40)
        );
    }

    @Test
    public void upgradeLevels() {
        userDao.deleteAll();

        users.forEach(user -> userDao.add(user));

        userService.upgradeLevels();

        checkLevel(users.get(0), Level.BASIC);
        checkLevel(users.get(1), Level.SILVER);
        checkLevel(users.get(2), Level.SILVER);
        checkLevel(users.get(3), Level.GOLD);
        checkLevel(users.get(4), Level.GOLD);
    }

    private void checkLevel(User user, Level expectedLevel) {
        User getUserResult = userDao.get(users.get(0).getId());
        assertThat(getUserResult.getLevel()).isEqualTo(expectedLevel);
    }

    @Test
    public void add() {
        userDao.deleteAll();

        User userWithLevel = users.get(4);
        User userWithoutLevel = users.get(0);
        userWithoutLevel.setLevel(Level.BASIC);

        userService.add(userWithLevel);
        userService.add(userWithoutLevel);

        User getUserWithLevelResult = userDao.get(userWithLevel.getId());
        User getUserWithoutLevelResult = userDao.get(userWithoutLevel.getId());
        assertThat(getUserWithLevelResult.getLevel()).isEqualTo(userWithLevel.getLevel());
        assertThat(getUserWithoutLevelResult.getLevel()).isEqualTo(Level.BASIC);
    }
}
