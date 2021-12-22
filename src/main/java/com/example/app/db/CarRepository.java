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
                String sql = """
                        select car.CAR_REG_NUMBER, CLIENT_NAME, cl.CLIENT_ID
                        from ISSUED_SERVICE iss_ser
                                 left join CAR car on iss_ser.CAR_REG_NUMBER = car.CAR_REG_NUMBER
                                 left join CLIENT cl on car.CLIENT_ID = cl.CLIENT_ID
                            where COMPLETION_DATE is null
                        group by car.CAR_REG_NUMBER, CLIENT_NAME, cl.CLIENT_ID;
                        """;
                System.out.println(sql);
                var resultSet = statement.executeQuery(sql);
                while (resultSet.next()) {
                    list.add(new Car(resultSet.getString("CAR_REG_NUMBER"),
                            resultSet.getInt("CLIENT_ID"),
                            resultSet.getString("CLIENT_NAME")));
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean addCar(String number, int clientId) {
        try (var connection = getConnection()) {
            String sql = """
                    insert into CAR(CAR_REG_NUMBER, CLIENT_ID)
                    values (?, ?);
                    """;
            System.out.println(sql);
            try (var statement = connection.prepareStatement(sql)) {
                statement.setString(1, number);
                statement.setInt(2, clientId);
                return statement.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
