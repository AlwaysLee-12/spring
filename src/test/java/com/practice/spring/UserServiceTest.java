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
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.List;

import static com.practice.spring.user.policy.UserLevelUpgradeNormal.MIN_LOGCOUNT_FOR_SILVER;
import static com.practice.spring.user.policy.UserLevelUpgradeNormal.MIN_RECOMMEND_FOR_GOLD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = "/test-application-context.xml")
public class UserServiceTest {

    @Autowired
    UserService userService;
    @Autowired
    UserDao userDao;
    List<User> users;
    @Autowired
    PlatformTransactionManager transactionManager;
//    @Autowired
//    DataSource dataSource;

    //DI 잘 됐는지 확인
    @Test
    public void bean() {
        assertThat(userService).isNotNull();
    }

    @BeforeEach
    public void setUp() {
        users = List.of(
                new User("id1", "name1", "pwd1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER - 1, 0, "a@naver,com"),
                new User("id2", "name2", "pwd2", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0, "b@naver,com"),
                new User("id3", "name3", "pwd3", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD - 1, "c@naver,com"),
                new User("id4", "name4", "pwd4", Level.SILVER, 70, MIN_RECOMMEND_FOR_GOLD, "d@naver,com"),
                new User("id5", "name5", "pwd5", Level.GOLD, 80, Integer.MAX_VALUE, "e@naver,com")
        );
    }

    @Test
    public void upgradeLevels() throws Exception {
        userDao.deleteAll();

        users.forEach(user -> userDao.add(user));

        userService.upgradeLevels();

        checkLevel(users.get(0), false);
        checkLevel(users.get(1), true);
        checkLevel(users.get(2), false);
        checkLevel(users.get(3), true);
        checkLevel(users.get(4), false);
    }

    private void checkLevel(User user, boolean upgraded) {
        User getUserResult = userDao.get(users.get(0).getId());
        if (upgraded) {
            assertThat(getUserResult.getLevel()).isEqualTo(user.getLevel().nextLevel());
        } else {
            assertThat(getUserResult.getLevel()).isEqualTo(user.getLevel());
        }
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

    @Test
    public void upgradeAllOrNothing() throws Exception {
        TestUserLevelUpgradePolicy testUserLevelUpgradePolicy = new TestUserLevelUpgradePolicy(users.get(3).getId(), userDao);
        userService.setUserLevelUpgradePolicy(testUserLevelUpgradePolicy);
        userService.setUserDao(userDao);
//        userService.setDataSource(dataSource);
        userService.setTransactionManager(transactionManager);

        userDao.deleteAll();

        for (User user : users) {
            userDao.add(user);
        }

        try {
            userService.upgradeLevels();
            fail("TestUserServiceException expected");
        } catch (TestUserServiceException e) {
        }

        checkLevel(users.get(1), false);
    }
}
