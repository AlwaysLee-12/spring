package com.practice.spring;

import com.practice.spring.user.MockMailSender;
import com.practice.spring.user.UserService;
import com.practice.spring.user.UserServiceImpl;
import com.practice.spring.user.UserServiceTx;
import com.practice.spring.user.dao.UserDao;
import com.practice.spring.user.domain.Level;
import com.practice.spring.user.domain.User;
import com.practice.spring.user.policy.UserLevelUpgradeNormal;
import org.apache.logging.log4j.message.SimpleMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.List;

import static com.practice.spring.user.policy.UserLevelUpgradeNormal.MIN_LOGCOUNT_FOR_SILVER;
import static com.practice.spring.user.policy.UserLevelUpgradeNormal.MIN_RECOMMEND_FOR_GOLD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = "/test-application-context.xml")
public class UserServiceTest {

    @Autowired
    UserService userService;
    @Autowired
    UserServiceImpl userServiceImpl;
    @Autowired
    UserDao userDao;
    List<User> users;
    @Autowired
    PlatformTransactionManager transactionManager;
    @Autowired
    MailSender mailSender;
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
    @DirtiesContext
    public void upgradeLevels() throws Exception {
        UserServiceImpl userServiceImpl = new UserServiceImpl();

        MockMailSender mockMailSender = new MockMailSender();
        MockUserDao mockUserDao = new MockUserDao(this.users);
        UserLevelUpgradeNormal userLevelUpgrade = new UserLevelUpgradeNormal();
        userServiceImpl.setUserLevelUpgradePolicy(userLevelUpgrade);
        userLevelUpgrade.setMailSender(mockMailSender);
        userLevelUpgrade.setUserDao(mockUserDao);

        userServiceImpl.upgradeLevels();

        List<User> updated = mockUserDao.getUpdated();
        assertThat(updated.size()).isEqualTo(2);
        checkUserAndLevel(updated.get(0), "joy", Level.SILVER);
        checkUserAndLevel(updated.get(1), "emy", Level.GOLD);

        List<String> requests = mockMailSender.getRequests();
        assertThat(requests.size()).isEqualTo(2);
        assertThat(requests.get(0)).isEqualTo(users.get(1).getEmail());
        assertThat(requests.get(1)).isEqualTo(users.get(3).getEmail());
    }

    private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel) {
        assertThat(updated.getId()).isEqualTo(expectedId);
        assertThat(updated.getLevel()).isEqualTo(expectedLevel);
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
        TestUserLevelUpgradePolicy testUserLevelUpgradePolicy = new TestUserLevelUpgradePolicy(users.get(3).getId(), userDao, mailSender);
        testUserLevelUpgradePolicy.setMailSender(mailSender);
        TestUserService testUserService = new TestUserService();
        testUserService.setUserDao(userDao);
        testUserService.setUserLevelUpgradePolicy(testUserLevelUpgradePolicy);

        UserServiceTx userServiceTx = new UserServiceTx();
        userServiceTx.setTransactionManager(transactionManager);
        userServiceTx.setUserService(testUserService);

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

    static class MockUserDao implements UserDao {

        private List<User> users;
        private List<User> updated = new ArrayList<>();

        public MockUserDao(List<User> users) {
            this.users = users;
        }

        public List<User> getUpdated() {
            return this.updated;
        }

        @Override
        public List<User> getAll() {
            return this.users;
        }

        @Override
        public void update(User user) {
            updated.add(user);
        }

        @Override
        public void add(User user) {
            throw new UnsupportedOperationException();
        }

        @Override
        public User get(String id) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void deleteAll() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getCount() {
            throw new UnsupportedOperationException();
        }
    }

    @Test
    public void mockUpgradeLevels() throws Exception {
        UserServiceImpl userServiceImpl = new UserServiceImpl();
        UserLevelUpgradeNormal userLevelUpgradeNormal = new UserLevelUpgradeNormal();

        UserDao mockUserDao = mock(UserDao.class);
        when(mockUserDao.getAll()).thenReturn(this.users);
        userServiceImpl.setUserDao(mockUserDao);

        MailSender mockMailSender = mock(MailSender.class);
        userLevelUpgradeNormal.setMailSender(mockMailSender);
        userServiceImpl.setUserLevelUpgradePolicy(userLevelUpgradeNormal);

        userServiceImpl.upgradeLevels();

        verify(mockUserDao, times(2)).update(any(User.class));
        verify(mockUserDao).update(users.get(1));
        assertThat(users.get(1).getLevel()).isEqualTo(Level.SILVER);
        verify(mockUserDao).update(users.get(3));
        assertThat(users.get(3).getLevel()).isEqualTo(Level.GOLD);

        ArgumentCaptor<SimpleMailMessage> mailMessageArg = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mockMailSender, times(2)).send(mailMessageArg.capture());
        List<SimpleMailMessage> mailMessages = mailMessageArg.getAllValues();
        assertThat(mailMessages.get(0).getTo()[0]).isEqualTo(users.get(1).getEmail());
        assertThat(mailMessages.get(1).getTo()[0]).isEqualTo(users.get(3).getEmail());
    }
}
