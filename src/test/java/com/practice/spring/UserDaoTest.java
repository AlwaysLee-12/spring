package com.practice.spring;

import com.practice.spring.user.dao.UserDao;
import com.practice.spring.user.domain.Level;
import com.practice.spring.user.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 복잡한 의존 관계를 갖고 있는 객체에 대한 테스트가 아니라면, 수동 DI를 통해 테스트를 실행하자
 * 복잡한 의존 관계를 갖고 있는 객체에 대한 테스트는 스프링 DI를 사용하고, 각 환경 별 설정 파일을 활용하자
 * 이 때, 예외적인 의존 관계를 강제로 구성해서(강제 재구성) 테스트를 수행할 경우에는 테스트 메서드나 클래스에 @DirtiesContext를 붙이자
 */

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = "/test-application-context.xml")
//@DirtiesContext //test class에서 application context의 구성이나 상태 강제 변경(해당 컨텍스트는 공유되지 않음)
public class UserDaoTest {

//    @Autowired
//    private ApplicationContext context;

    //픽스처(fixture) - 테스트를 수행하는데 필요한 객체(정보)
    @Autowired
    private UserDao userDao;
    @Autowired
    private DataSource dataSource;
    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    public void setUp() {
        /**
         *@DirtiesContext 사용 시
         *         DataSource dataSource = new SingleConnectionDataSource(
         *                 "jdbc:mysql://localhost/testdb", "root", "1234", true);
         *         userDao.setDataSource(dataSource);
         */

        //수동 DI(POJO. 단, UserDao를 테스트 객체 생성 시마다 생성)
//        userDao = new UserDao();
//        DataSource dataSource = new SingleConnectionDataSource(
//                "jdbc:mysql://localhost/testdb", "root", "1234", true);
//        userDao.setDataSource(dataSource);

        user1 = new User("cson", "name1", "pwd1", Level.BASIC, 1, 0);
        user2 = new User("byson", "name2", "pwd2", Level.SILVER, 55, 10);
        user3 = new User("aon", "name3", "pwd3", Level.GOLD, 100, 40);
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
        checkSameUser(userGet1, user1);

        User userGet2 = userDao.get(user2.getId());
        checkSameUser(userGet2, user2);
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

    @Test
    public void getAll() throws SQLException {
        userDao.deleteAll();

        List<User> users0 = userDao.getAll();
        assertThat(users0.size()).isEqualTo(0);

        userDao.add(user1);
        List<User> users1 = userDao.getAll();
        assertThat(users1.size()).isEqualTo(1);
        checkSameUser(user1, users1.get(0));

        userDao.add(user2);
        List<User> users2 = userDao.getAll();
        assertThat(users2.size()).isEqualTo(2);
        checkSameUser(user2, users2.get(0));
        checkSameUser(user1, users2.get(1));

        userDao.add(user3);
        List<User> users3 = userDao.getAll();
        assertThat(users3.size()).isEqualTo(3);
        checkSameUser(user3, users3.get(0));
        checkSameUser(user2, users3.get(1));
        checkSameUser(user1, users3.get(2));
    }

    public void checkSameUser(User user1, User user2) {
        assertThat(user1.getId()).isEqualTo(user2.getId());
        assertThat(user1.getName()).isEqualTo(user2.getName());
        assertThat(user1.getPassword()).isEqualTo(user2.getPassword());
        assertThat(user1.getLevel()).isEqualTo(user2.getLevel());
        assertThat(user1.getLogin()).isEqualTo(user2.getLogin());
        assertThat(user1.getRecommend()).isEqualTo(user2.getRecommend());
    }

    @Test
    public void duplicateKey() {
        userDao.deleteAll();

        try {
            userDao.add(user1);
            userDao.add(user1);
        } catch (DuplicateKeyException e) {
            SQLException sqlE = (SQLException) e.getRootCause();
            SQLErrorCodeSQLExceptionTranslator set = new SQLErrorCodeSQLExceptionTranslator(dataSource);

            assertThat(set.translate(null, null, sqlE)).isEqualTo(DuplicateKeyException.class);
        }

    }

    @Test
    public void update() {
        userDao.deleteAll();

        userDao.add(user1);

        user1.setName("update user");
        user1.setPassword("update pawd");
        user1.setLevel(Level.GOLD);
        user1.setLogin(1000);
        user1.setRecommend(999);

        userDao.update(user1);

        User updatedUser = userDao.get(user1.getId());
        checkSameUser(user1, updatedUser);
        //update 쿼리에서 조건문을 빼 먹었을 때 아래 케이스 실패
        User getUser2 = userDao.get(user2.getId());
        checkSameUser(user2, getUser2);
    }
}
