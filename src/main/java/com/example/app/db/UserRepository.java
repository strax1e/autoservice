package com.example.app.db;

import com.example.app.entity.User;
import com.example.app.entity.UserRole;

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
}
