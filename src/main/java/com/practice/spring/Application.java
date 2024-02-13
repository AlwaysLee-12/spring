package com.practice.spring;

import com.practice.spring.common.config.DaoFactory;
import com.practice.spring.common.database.CountingConnectionMaker;
import com.practice.spring.common.database.SimpleConnectionMaker;
import com.practice.spring.user.dao.UserDao;
import com.practice.spring.user.domain.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import java.sql.SQLException;

@SpringBootApplication
public class Application {

	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		SpringApplication.run(Application.class, args);

		ApplicationContext applicationContext = new AnnotationConfigApplicationContext(DaoFactory.class);
		UserDao dao = applicationContext.getBean("userDao", UserDao.class);

		User user = new User();
		user.setId("alwaysLee");
		user.setName("lee");
		user.setPassword("password");

		dao.add(user);

		System.out.println(user.getId() + "등록 성공");

		User user2 = dao.get(user.getId());

		//main 메서드 userDao test code 변경 전
//		System.out.println(user2.getName());
//		System.out.println(user2.getPassword());
//
//		System.out.println(user2.getId() + "조회 성공");

		//main 메서드 userDao test code 변경 후 -> add()와 get()에 대한 테스트 전체적으로 가능. 단, 많은 양의 테스트 main() 만으로는 어려움
		if (user.getName().equals(user2.getName())) {
			System.out.println("테스트 실패 (name)");
		} else if (user.getPassword().equals(user2.getPassword())) {
			System.out.println("테스트 실패 (password)");
		} else {
			System.out.println("조회 테스트 성공");
		}

//		ApplicationContext ac = new AnnotationConfigApplicationContext(DaoFactory.class);

//		GenericXmlApplicationContext ac = new GenericXmlApplicationContext("com/practice/spring/common/config/application-context.xml"); xml을 이용하는 ac 생성

//		var userDao = ac.getBean("userDao", UserDao.class);
//
//		var connectionMaker = ac.getBean("connectionMaker", CountingConnectionMaker.class);
//		System.out.println("Connection count " + connectionMaker.getConnectionCount());
	}

}
