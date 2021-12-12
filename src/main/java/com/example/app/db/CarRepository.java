package com.example.app.db;

import com.example.app.entity.Car;

import java.sql.SQLException;
import java.util.*;

public class CarRepository extends AbstractRepository {

    public CarRepository(Properties properties) {
        super(properties);
    }

    public Collection<Car> getCars() {
        List<Car> list = new LinkedList<>();
        try (var connection = getConnection()) {
            try (var statement = connection.createStatement()) {
                String sql = "select * from car;";
                System.out.println(sql);
                var resultSet = statement.executeQuery(sql);
                while (resultSet.next()) {
                    list.add(new Car(resultSet.getInt("number"),
                            resultSet.getInt("owner")));
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
