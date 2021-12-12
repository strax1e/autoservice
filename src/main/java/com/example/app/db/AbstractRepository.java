package com.example.app.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public abstract class AbstractRepository {

    private final Properties properties;

    protected AbstractRepository(Properties properties) {
        this.properties = properties;
    }

    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(properties.getProperty("url"), properties.getProperty("username"),
                properties.getProperty("password"));
    }
}
