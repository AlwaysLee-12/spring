package com.practice.spring.user.dao;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JdbcContext {

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void workWithStatementStrategy(StatementStrategy stmt) throws SQLException {
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

    public void executeSql(final String sql, final String... params) throws SQLException {
        workWithStatementStrategy(
                new StatementStrategy() {
                    @Override
                    public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
                        PreparedStatement ps = c.prepareStatement(sql);

                        if (params.length > 0) {
                            setParams(ps, params);
                        }

                        return ps;
                    }
                }
        );
    }

    private void setParams(PreparedStatement ps, final String... params) throws SQLException {
        for (int i = 1; i < params.length; i++) {
            ps.setString(i, params[i - 1]);
        }
    }
}
