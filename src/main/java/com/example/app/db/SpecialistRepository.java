package com.example.app.db;

import com.example.app.entity.Report;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class SpecialistRepository extends AbstractRepository {

    private final String reportSql = """
                            select SERVICE_NAME, PRICE, CAR_REG_NUMBER, COMPLETION_DATE
                            from ISSUED_SERVICE iss_ser
                             left join SPECIALIST_SERVICE spec_ser on iss_ser.SPECIALIST_SERVICE_ID=spec_ser.SPECIALIST_SERVICE_ID
                             left join SPECIALIST spec on spec_ser.SPECIALIST_ID = spec.specialist_id
                             left join SERVICE ser on spec_ser.SERVICE_ID = ser.service_id
            """;

    public SpecialistRepository(Properties properties) {
        super(properties);
    }

    public List<Report> getSpecialistYearReport(String name, int year) {
        List<Report> list = new LinkedList<>();
        try (var connection = getConnection()) {
            String sql = reportSql + """
                    where date_part('year', COMPLETION_DATE) = ? and SPECIALIST_NAME = ?;
                    """;
            System.out.println(sql);
            try (var statement = connection.prepareStatement(sql)) {
                statement.setInt(1, year);
                statement.setString(2, name);
                var resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    list.add(new Report(resultSet.getString("SERVICE_NAME"),
                            resultSet.getDouble("PRICE"),
                            resultSet.getString("CAR_REG_NUMBER"),
                            resultSet.getString("COMPLETION_DATE")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Report> getSpecialistMonthReport(String name, int year, int month) {
        List<Report> list = new LinkedList<>();
        try (var connection = getConnection()) {
            String sql = reportSql + """
                    where date_part('year', COMPLETION_DATE) = ? and date_part('month', COMPLETION_DATE) = ?
                        and SPECIALIST_NAME = ?;
                    """;
            System.out.println(sql);
            try (var statement = connection.prepareStatement(sql)) {
                statement.setInt(1, year);
                statement.setInt(2, month);
                statement.setString(3, name);
                var resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    list.add(new Report(resultSet.getString("SERVICE_NAME"),
                            resultSet.getDouble("PRICE"),
                            resultSet.getString("CAR_REG_NUMBER"),
                            resultSet.getString("COMPLETION_DATE")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Report> getSpecialistDayReport(String name, int year, int month, int day) {
        List<Report> list = new LinkedList<>();
        try (var connection = getConnection()) {
            String sql = reportSql + """
                    where date_part('year', COMPLETION_DATE) = ? and date_part('month', COMPLETION_DATE) = ?
                        and date_part('day', COMPLETION_DATE) = ? and SPECIALIST_NAME = ?;
                    """;
            System.out.println(sql);
            try (var statement = connection.prepareStatement(sql)) {
                statement.setInt(1, year);
                statement.setInt(2, month);
                statement.setInt(3, day);
                statement.setString(4, name);
                var resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    list.add(new Report(resultSet.getString("SERVICE_NAME"),
                            resultSet.getDouble("PRICE"),
                            resultSet.getString("CAR_REG_NUMBER"),
                            resultSet.getString("COMPLETION_DATE")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Report> getSpecialistQuarterReport(String name, int year, int quarter) {
        List<Report> list = new LinkedList<>();
        try (var connection = getConnection()) {
            String sql = reportSql + """
                    where (date_part('month', COMPLETION_DATE) = 3 * (? - 1) + 1
                        or date_part('month', COMPLETION_DATE) = 3 * (? - 1) + 2
                        or date_part('month', COMPLETION_DATE) = 3 * (? - 1) + 3)
                        and date_part('year', COMPLETION_DATE) = ?
                        and SPECIALIST_NAME = ?;
                    """;
            System.out.println(sql);
            try (var statement = connection.prepareStatement(sql)) {
                statement.setInt(1, quarter);
                statement.setInt(2, quarter);
                statement.setInt(3, quarter);
                statement.setInt(4, year);
                statement.setString(5, name);
                var resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    list.add(new Report(resultSet.getString("SERVICE_NAME"),
                            resultSet.getDouble("PRICE"),
                            resultSet.getString("CAR_REG_NUMBER"),
                            resultSet.getString("COMPLETION_DATE")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
