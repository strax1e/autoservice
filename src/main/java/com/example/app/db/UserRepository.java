package com.example.app.db;

import com.example.app.entity.User;
import com.example.app.entity.UserRole;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Properties;

public class UserRepository extends AbstractRepository {

    public UserRepository(Properties properties) {
        super(properties);
    }

    public Optional<User> getUserByUsername(String username) {
        try (var connection = getConnection()) {
            String sql = "select * from \"user\" where username = ?;";
            System.out.println(sql);
            try (var statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                var resultSet = statement.executeQuery();
                return (resultSet.next())
                        ? Optional.of(new User(resultSet.getString("username"), resultSet.getString("password"), UserRole.valueOf(resultSet.getString("role"))))
                        : Optional.empty();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public boolean addNewClient(String clientName, String username, String password,
                                String phone, String carRegNumber, String bank) {
        try (var connection = getConnection()) {
            connection.setAutoCommit(false);
            String sql = """
                    insert into CLIENT(CLIENT_NAME, BANK, PHONE_NUMBER)
                    values (?, ?, ?);
                    """;
            System.out.println(sql);
            try (var statement = connection.prepareStatement(sql)) {
                statement.setString(1, clientName);
                statement.setString(2, bank);
                statement.setString(3, phone);
                statement.executeUpdate();
            }

            sql = "select CLIENT_ID from CLIENT where CLIENT_NAME = ?;";
            System.out.println(sql);
            int clientId = -1;
            try (var statement = connection.prepareStatement(sql)) {
                statement.setString(1, clientName);
                var resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    clientId = resultSet.getInt("CLIENT_ID");
                }
            }
            if (clientId == -1) {
                throw new SQLException();
            }

            sql = """
                    insert into CAR
                    values (?, ?);
                    insert into "user" (USERNAME, PASSWORD, ROLE)
                    values (?, ?, 'CLIENT');
                    insert into USER_CLIENT (CLIENT_ID, USERNAME)
                    values (?, ?);
                    """;
            System.out.println(sql);
            try (var statement = connection.prepareStatement(sql)) {
                statement.setString(1, carRegNumber);
                statement.setInt(2, clientId);
                statement.setString(3, username);
                statement.setString(4, password);
                statement.setInt(5, clientId);
                statement.setString(6, username);
                statement.executeUpdate();
            }

            connection.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
