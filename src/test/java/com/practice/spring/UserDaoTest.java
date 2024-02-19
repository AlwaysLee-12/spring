package com.practice.spring;

import com.practice.spring.user.dao.UserDao;
import com.practice.spring.user.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.sql.SQLException;

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
//    @Autowired
    private UserDao userDao;
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
        userDao = new UserDao();
        DataSource dataSource = new SingleConnectionDataSource(
                "jdbc:mysql://localhost/testdb", "root", "1234", true);
        userDao.setDataSource(dataSource);

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
