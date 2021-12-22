package com.example.app.db;

import com.example.app.entity.Issue;
import com.example.app.entity.MyIssue;

import java.sql.Date;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class IssueRepository extends AbstractRepository {

    public IssueRepository(Properties properties) {
        super(properties);
    }

    public boolean complete(int issueId, String username) {
        var result = false;
        try (var connection = getConnection()) {
            String sql = """
                    update ISSUED_SERVICE set COMPLETION_DATE = ?
                    where issued_service_id = ? and SPECIALIST_SERVICE_ID in (
                        select SPECIALIST_SERVICE_ID
                        from SPECIALIST_SERVICE
                        where SPECIALIST_ID in (
                            select SPECIALIST_ID
                            from USER_SPECIALIST
                            where USERNAME = ?));
                    """;
            System.out.println(sql);
            try (var statement = connection.prepareStatement(sql)) {
                statement.setDate(1, new Date(System.currentTimeMillis()));
                statement.setInt(2, issueId);
                statement.setString(3, username);
                result = statement.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<MyIssue> getIssues(String username) {
        List<MyIssue> list = new LinkedList<>();
        try (var connection = getConnection()) {
            String sql = """
                    select ISSUED_SERVICE_ID, CAR_REG_NUMBER, COMPLETION_DATE
                    from ISSUED_SERVICE
                    where SPECIALIST_SERVICE_ID in (
                        select SPECIALIST_SERVICE_ID
                        from SPECIALIST_SERVICE
                        where SPECIALIST_ID in (
                            select SPECIALIST_ID
                            from USER_SPECIALIST
                            where USERNAME = ?));
                        """;
            System.out.println(sql);
            try (var statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                var resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    list.add(new MyIssue(resultSet.getInt("ISSUED_SERVICE_ID"),
                            resultSet.getString("CAR_REG_NUMBER"),
                            resultSet.getString("COMPLETION_DATE")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Issue> getIssues() {
        List<Issue> list = new LinkedList<>();
        try (var connection = getConnection()) {
            String sql = """
                    select ISSUED_SERVICE_ID, CAR.CAR_REG_NUMBER, CLIENT_NAME, SPECIALIST_NAME, SERVICE_NAME, COMPLETION_DATE
                    from ISSUED_SERVICE
                    left join CAR on ISSUED_SERVICE.CAR_REG_NUMBER = CAR.CAR_REG_NUMBER
                    left join CLIENT on CLIENT.CLIENT_ID = CAR.CLIENT_ID
                    left join SPECIALIST_SERVICE on SPECIALIST_SERVICE.SPECIALIST_SERVICE_ID = ISSUED_SERVICE.SPECIALIST_SERVICE_ID
                    left join SPECIALIST on SPECIALIST.SPECIALIST_ID = SPECIALIST_SERVICE.SPECIALIST_ID
                    left join SERVICE on SERVICE.SERVICE_ID = SPECIALIST_SERVICE.SERVICE_ID;
                    """;
            System.out.println(sql);
            try (var statement = connection.prepareStatement(sql)) {
                var resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    list.add(new Issue(resultSet.getInt("ISSUED_SERVICE_ID"),
                            resultSet.getString("CAR_REG_NUMBER"),
                            resultSet.getString("CLIENT_NAME"),
                            resultSet.getString("SPECIALIST_NAME"),
                            resultSet.getString("SERVICE_NAME"),
                            resultSet.getString("COMPLETION_DATE")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
