package com.example.app.db;

import com.example.app.entity.CarService;
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
                    list.add(new Service(resultSet.getInt("service_id"),
                            resultSet.getString("service_name"), resultSet.getDouble("price")));
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<CarService> getServicesOfCar(String carNumber) {
        List<CarService> list = new LinkedList<>();
        try (var connection = getConnection()) {
            String sql = """
                    select SPECIALIST_NAME, SERVICE_NAME, PRICE
                    from ISSUED_SERVICE iss_ser
                             left join SPECIALIST_SERVICE spec_ser on iss_ser.SPECIALIST_SERVICE_ID = spec_ser.SPECIALIST_SERVICE_ID
                             left join SPECIALIST spec on spec_ser.SPECIALIST_ID = spec.specialist_id
                             left join SERVICE ser on spec_ser.SERVICE_ID = ser.service_id
                    where CAR_REG_NUMBER = ? AND COMPLETION_DATE IS NULL;
                    """;
            System.out.println(sql);
            try (var statement = connection.prepareStatement(sql)) {
                statement.setString(1, carNumber);
                var resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    list.add(new CarService(resultSet.getString("SPECIALIST_NAME"),
                            resultSet.getString("SERVICE_NAME"), resultSet.getDouble("PRICE")));
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Optional<Double> calculateCostByClient(String name) {
        try (var connection = getConnection()) {
            String sql = """
                    select sum(PRICE)
                    from ISSUED_SERVICE iss_ser
                             left join SPECIALIST_SERVICE spec_ser on iss_ser.SPECIALIST_SERVICE_ID = spec_ser.SPECIALIST_SERVICE_ID
                             left join SERVICE ser on spec_ser.SERVICE_ID = ser.service_id
                             left join CAR c on iss_ser.CAR_REG_NUMBER = c.car_reg_number
                             left join USER_CLIENT UC on c.CLIENT_ID = UC.CLIENT_ID
                         where USERNAME = ?
                    group by c.CLIENT_ID;
                    """;
            System.out.println(sql);
            try (var statement = connection.prepareStatement(sql)) {
                statement.setString(1, name);
                var resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return Optional.of(resultSet.getDouble("sum"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
