package com.practice.spring.common.database;

import java.sql.Connection;
import java.sql.SQLException;

public class CountingConnectionMaker implements ConnectionMaker{

    private int connectionCount;
    private ConnectionMaker realConnectionMaker;

    public CountingConnectionMaker(ConnectionMaker realConnectionMaker) {
        connectionCount = 0;
        this.realConnectionMaker = realConnectionMaker;
    }

    @Override
    public Connection makeConnection() throws ClassNotFoundException, SQLException {
        connectionCount++;

        return realConnectionMaker.makeConnection();
    }

    public int getConnectionCount() {
        return connectionCount;
    }
}
