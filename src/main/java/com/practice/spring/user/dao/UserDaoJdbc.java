package com.practice.spring.user.dao;

import com.practice.spring.user.domain.Level;
import com.practice.spring.user.domain.User;
import com.practice.spring.user.exception.DuplicateUserIdException;
import lombok.NoArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

@NoArgsConstructor
public class UserDaoJdbc implements UserDao {
//    private JdbcContext jdbcContext;

//    public void setJdbcContext(JdbcContext jdbcContext) {
//        this.jdbcContext = jdbcContext;
//    }

    private JdbcTemplate jdbcTemplate;
    //    private JdbcContext jdbcContext;
    private RowMapper<User> userRowMapper = new RowMapper<>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();

            user.setId(rs.getString("id"));
            user.setName(rs.getString("name"));
            user.setPassword(rs.getString("password"));
            user.setLevel(Level.valueOf(rs.getInt("level")));
            user.setLogin(rs.getInt("login"));
            user.setRecommend(rs.getInt("recommend"));
            user.setEmail(rs.getString("email"));

            return user;
        }
    };

    //수정자 DI
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void add(User user) {
        //전략 클래스 사용 시
//        StatementStrategy st = new AddStatement(user);
//        jdbcContextWithStatementStrategy(st);

//        try {
//            //전략 클래스를 익명 클래스로 구현
//            jdbcContext.workWithStatementStrategy(
//                    new StatementStrategy() {
//                        @Override
//                        public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
//                            PreparedStatement ps = c.prepareStatement(
//                                    "insert into users(id, name, password) values(?,?,?)");
//                            ps.setString(1, user.getId());
//                            ps.setString(2, user.getName());
//                            ps.setString(3, user.getPassword());
//
//                            return ps;
//                        }
//                    }
//            );
//        } catch (SQLException e) {
//            if (e.getErrorCode() == MysqlErrorNumbers.ER_DUP_ENTRY) {
//                throw new DuplicateUserIdException(e);
//            } else {
//                throw new RuntimeException(e);
//            }
//        }


        //변하지 않는 부분과 변하는 부분 분리
//        jdbcContext.executeSql("insert into users(id, name, password) values(?,?,?)", user.getId(), user.getName(), user.getPassword());


        //Jdbc Template 활용
        jdbcTemplate.update("insert into users(id, name, password, level, login, recommend, email) values(?,?,?,?,?,?,?)",
                user.getId(), user.getName(), user.getPassword(), user.getLevel().intValue(), user.getLogin(), user.getRecommend(), user.getEmail());
        //예외 전환
//        try {
//            jdbcTemplate.update("insert into users(id, name, password) values(?,?,?)", user.getId(), user.getName(), user.getPassword());
//        } catch (DuplicateKeyException e) {
//            e.printStackTrace();
//            throw new DuplicateUserIdException(e);
//        }
    }

    public User get(String id) {
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
        return jdbcTemplate.queryForObject("select * from users where id = ?", new Object[]{id}, userRowMapper);


    }

    public void deleteAll() {
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


    public int getCount() {
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

    @Override
    public void update(User user) {
        jdbcTemplate.update(
                "update users set name = ?, password = ?, level = ?, login = ?, recommend = ?, email = ? where id = ?, ",
                user.getName(), user.getPassword(), user.getLevel().intValue(), user.getLogin(), user.getRecommend(), user.getEmail(), user.getId()
        );
    }

    public List<User> getAll() {
        return jdbcTemplate.query("select * from users order by id", userRowMapper);
    }
}
