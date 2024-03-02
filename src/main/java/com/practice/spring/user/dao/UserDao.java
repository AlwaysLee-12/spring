package com.practice.spring.user.dao;

import com.practice.spring.common.database.ConnectionMaker;
import com.practice.spring.common.database.SimpleConnectionMaker;
import com.practice.spring.user.domain.User;
import lombok.NoArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

@NoArgsConstructor
public class UserDao {
//    private JdbcContext jdbcContext;

//    public void setJdbcContext(JdbcContext jdbcContext) {
//        this.jdbcContext = jdbcContext;
//    }

    private JdbcTemplate jdbcTemplate;

    //수정자 DI
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void add(User user) throws SQLException {
        //전략 클래스 사용 시
        //StatementStrategy st = new AddStatement(user);
        //jdbcContextWithStatementStrategy(st);

        //전략 클래스를 익명 클래스로 구현
//        jdbcContext.workWithStatementStrategy(
//                new StatementStrategy() {
//                    @Override
//                    public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
//                        PreparedStatement ps = c.prepareStatement(
//                                "insert into users(id, name, password) values(?,?,?)");
//                        ps.setString(1, user.getId());
//                        ps.setString(2, user.getName());
//                        ps.setString(3, user.getPassword());
//
//                        return ps;
//                    }
//                }
//        );

        //변하지 않는 부분과 변하는 부분 분리
//        jdbcContext.executeSql("insert into users(id, name, password) values(?,?,?)", user.getId(), user.getName(), user.getPassword());

        //Jdbc Template 활용
        jdbcTemplate.update("insert into users(id, name, password) values(?,?,?)", user.getId(), user.getName(), user.getPassword());
    }

    public User get(String id) throws SQLException {
//        Connection c = dataSource.getConnection();
//
//        PreparedStatement ps = c.prepareStatement(
//                "select * from users where id = ?"
//        );
//        ps.setString(1, id);
//
//        ResultSet rs = ps.executeQuery();
//
//        User user = null;
//        if (rs.next()) {
//            user = new User();
//            user.setId(rs.getString("id"));
//            user.setName(rs.getString("name"));
//            user.setPassword(rs.getString("password"));
//        }
//
//        rs.close();
//        ps.close();
//        c.close();
//
//        if (user == null) throw new EmptyResultDataAccessException(1);
//
//        return user;

        //Jdbc Template 활용
        //1
        return jdbcTemplate.queryForObject("select * from users where id = ?", new Object[] {id}, new RowMapper<User>(){

            @Override
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                User user = new User();

                user.setId(rs.getString("id"));
                user.setName(rs.getString("name"));
                user.setPassword(rs.getString("password"));

                return user;
            }
        });


    }

    public void deleteAll() throws SQLException {
        //전략 클래스 사용 시
        //StatementStrategy strategy = new DeleteAllStatement();
        //jdbcContextWithStatementStrategy(strategy);

        //전략 클래스 대신 익명 클래스 사용 시
//        jdbcContext.workWithStatementStrategy(
//                new StatementStrategy() {
//                    @Override
//                    public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
//                        PreparedStatement ps = c.prepareStatement("delete from users");
//
//                        return ps;
//                    }
//                }
//        );

        //변하지 않는 부분과 변하는 부분 분리
//        jdbcContext.executeSql("delete from users");

        //JdbcTemplate 사용
        //1
//        jdbcTemplate.update(new PreparedStatementCreator() {
//            @Override
//            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
//                return con.prepareStatement("delete from users");
//            }
//        });
        //2
        jdbcTemplate.update("delete from users");
    }



    public int getCount() throws SQLException {
//        Connection c = null;
//        PreparedStatement ps = null;
//        ResultSet rs = null;
//
//        try {
//            c = dataSource.getConnection();
//            ps = c.prepareStatement(
//                    "select count(*) from users"
//            );
//
//            rs = ps.executeQuery();
//            rs.next();
//            return rs.getInt(1);
//        } catch (SQLException exception) {
//            throw exception;
//        } finally {
//            try {
//                if (rs != null) {
//                    rs.close();
//                }
//            } catch (SQLException exception) {
//
//            }
//            try {
//                if (ps != null) {
//                    ps.close();
//                }
//            } catch (SQLException exception) {
//
//            }
//            try {
//                if (c != null) {
//                    c.close();
//                }
//            } catch (SQLException exception) {
//
//            }
//        }

        //Jdbc Template 활용
        //1
//        return jdbcTemplate.query(new PreparedStatementCreator() {
//            @Override
//            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
//                return con.prepareStatement("select count(*) from users");
//            }
//        }, new ResultSetExtractor<Integer>() {
//            @Override
//            public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
//                rs.next();
//                return rs.getInt(1);
//            }
//        });
        //2
        return jdbcTemplate.queryForObject("select count(*) from users", Integer.class);
    }

    public List<User> getAll() {
        return jdbcTemplate.query("select * from users order by id", new RowMapper<>() {
            @Override
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                User user = new User();

                user.setId(rs.getString("id"));
                user.setName(rs.getString("name"));
                user.setPassword(rs.getString("password"));

                return user;
            }
        });
    }
}
