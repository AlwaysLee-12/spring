package com.practice.spring.user.dao;

import com.practice.spring.common.database.ConnectionMaker;
import com.practice.spring.common.database.SimpleConnectionMaker;
import com.practice.spring.user.domain.User;
import lombok.NoArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;

import javax.sql.DataSource;
import java.sql.*;

@NoArgsConstructor
public class UserDao {
    private DataSource dataSource;

    public UserDao(DataSource DataSource) {
        this.dataSource = DataSource;
    }

    public void add(User user) throws SQLException {
        StatementStrategy st = new AddStatement(user);
        jdbcContextWithStatementStrategy(st);
    }

    public User get(String id) throws SQLException {
        Connection c = dataSource.getConnection();

        PreparedStatement ps = c.prepareStatement(
                "select * from users where id = ?"
        );
        ps.setString(1, id);

        ResultSet rs = ps.executeQuery();

        User user = null;
        if (rs.next()) {
            user = new User();
            user.setId(rs.getString("id"));
            user.setName(rs.getString("name"));
            user.setPassword(rs.getString("password"));
        }

        rs.close();
        ps.close();
        c.close();

        if (user == null) throw new EmptyResultDataAccessException(1);

        return user;
    }

    public void deleteAll() throws SQLException {
        StatementStrategy strategy = new DeleteAllStatement();
        jdbcContextWithStatementStrategy(strategy);
    }

    public int getCount() throws SQLException {
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            c = dataSource.getConnection();
            ps = c.prepareStatement(
                    "select count(*) from users"
            );

            rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException exception) {
            throw exception;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException exception) {

            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException exception) {

            }
            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException exception) {

            }
        }
    }

    public void jdbcContextWithStatementStrategy(StatementStrategy stmt) throws SQLException {
        Connection c = null;
        PreparedStatement ps = null;

        try {
            c = dataSource.getConnection();

            ps = stmt.makePreparedStatement(c);

            ps.executeUpdate();
        } catch (SQLException exception) {
            throw exception;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException exception) {

            }

            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException exception) {

            }
        }
    }

    //수정자 DI
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
