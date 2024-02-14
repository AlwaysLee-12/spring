package com.practice.spring;

import com.practice.spring.user.dao.UserDao;
import com.practice.spring.user.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserDaoTest {
    //픽스처(fixture) - 테스트를 수행하는데 필요한 객체(정보)
    private UserDao userDao;
    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    public void setUp() {
        ApplicationContext context = new GenericXmlApplicationContext("application-context.xml");

        userDao = context.getBean("userDao", UserDao.class);

        user1 = new User("id1", "name1", "pwd1");
        user2 = new User("id2", "name2", "pwd2");
        user3 = new User("id3", "name3", "pwd3");
    }

    @Test
    public void addAndGet() throws SQLException, ClassNotFoundException {
        userDao.deleteAll();
        assertEquals(userDao.getCount(), 0);

        userDao.add(user1);
        userDao.add(user2);

        assertEquals(userDao.getCount(), 1);
        assertEquals(userDao.getCount(), 2);

        User userGet1 = userDao.get(user1.getId());
        assertEquals(userGet1.getName(), user1.getName());
        assertEquals(userGet1.getPassword(), user1.getPassword());

        User userGet2 = userDao.get(user2.getId());
        assertEquals(userGet2.getName(), user2.getName());
        assertEquals(userGet2.getPassword(), user2.getPassword());
    }

    @Test
    public void count() throws SQLException, ClassNotFoundException {
        userDao.deleteAll();
        assertEquals(userDao.getCount(), 0);

        userDao.add(user1);
        assertEquals(userDao.getCount(), 1);

        userDao.add(user2);
        assertEquals(userDao.getCount(), 2);

        userDao.add(user3);
        assertEquals(userDao.getCount(), 3);
    }

    @Test
    public void getUserFailure() throws SQLException {
        userDao.deleteAll();
        assertEquals(userDao.getCount(), 0);
        
        assertThrows(EmptyResultDataAccessException.class, () -> {
            userDao.get("unknown_id");
        });
    }
}
