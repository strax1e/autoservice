package com.example.app.db;

import com.example.app.entity.Service;

import java.sql.SQLException;
import java.util.*;

public class ServiceRepository extends AbstractRepository {

    public ServiceRepository(Properties properties) {
        super(properties);
    }

    public Collection<Service> getServices() {
        List<Service> list = new LinkedList<>();
        try (var connection = getConnection()) {
            try (var statement = connection.createStatement()) {
                String sql = "select * from service;";
                System.out.println(sql);
                var resultSet = statement.executeQuery(sql);
                while (resultSet.next()) {
                    list.add(new Service(resultSet.getInt("id"),
                            resultSet.getString("name"), resultSet.getDouble("price")));
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
