package com.practice.spring.user;

import com.practice.spring.user.dao.UserDao;
import com.practice.spring.user.domain.Level;
import com.practice.spring.user.domain.User;
import com.practice.spring.user.policy.UserLevelUpgradePolicy;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class UserService {

    UserDao userDao;
    UserLevelUpgradePolicy userLevelUpgradePolicy;
    private DataSource dataSource;


    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setUserLevelUpgradePolicy(UserLevelUpgradePolicy userLevelUpgradePolicy) {
        this.userLevelUpgradePolicy = userLevelUpgradePolicy;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void upgradeLevels() throws Exception {
        //동기화 작업 초기화
        TransactionSynchronizationManager.initSynchronization();
        //Connection object 생성, 트랜잭션 저장소에 바인딩(for 트랜잭션 동기화)
        Connection c = DataSourceUtils.getConnection(dataSource);
        c.setAutoCommit(false);

        try {
            List<User> users = userDao.getAll();

            users.forEach(user -> {
                if (userLevelUpgradePolicy.canUpgradeLevel(user)) {
                    userLevelUpgradePolicy.upgradeLevel(user);
                }
            });
            c.commit();
        } catch (Exception e) {
            c.rollback();
            throw e;
        } finally {
            //Connection close, 트랜잭션 저장소에서 언바이딩
            DataSourceUtils.releaseConnection(c, dataSource);
            TransactionSynchronizationManager.unbindResource(dataSource);
            TransactionSynchronizationManager.clearSynchronization();
        }

    }

    public void add(User user) {
        if (user.getLevel() == null) {
            user.setLevel(Level.BASIC);
        }
        userDao.add(user);
    }
}
