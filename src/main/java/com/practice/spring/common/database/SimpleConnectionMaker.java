package com.practice.spring.common.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SimpleConnectionMaker {
    public Connection openConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.driver");
        Connection c = DriverManager.getConnection("jdbc:mysql://localhost/spring", "root", "fightinh");

        return c;
    }
}
