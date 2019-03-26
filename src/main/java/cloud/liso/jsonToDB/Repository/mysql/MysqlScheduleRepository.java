package cloud.liso.jsonToDB.Repository.mysql;

import cloud.liso.jsonToDB.Repository.ScheduleRepository;
import cloud.liso.jsonToDB.database.MysqlConnector;
import cloud.liso.jsonToDB.model.Schedule;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class MysqlScheduleRepository implements ScheduleRepository {
    private static final String SAVE_SCHEDULE = "INSERT INTO schedule(time_of_show) VALUES(?)";
    private static final String GET_DAY_OF_WEEK = "SELECT * FROM days_of_week WHERE day = ?";
    private static final String SAVE_SCHEDULE_DAYS_TIME = "INSERT INTO schedule_days_of_week(schedule_id, days_of_week_id) VALUEs(?, ?)";
    private final Connection connection;

    public MysqlScheduleRepository() {
        connection = MysqlConnector.getConnection();
    }

    @Override
    public Schedule save(Schedule schedule) {
        try {
            connection.setAutoCommit(false);
            try {

                PreparedStatement findDay = connection.prepareStatement(GET_DAY_OF_WEEK);
                int[] daysIndices = new int[schedule.getDays().size()];
                List<DayOfWeek> days = schedule.getDays();
                int daysSize = days.size() - 1;
                for (int i = 0; i <= daysSize; i++) {
                    String dayOfWeek = days.get(i).getDisplayName(TextStyle.FULL, Locale.US);
                    findDay.setString(1, dayOfWeek);
                    ResultSet result = findDay.executeQuery();
                    if (result.next()) {
                        daysIndices[i] = result.getInt(1);
                    } else {
                        throw new SQLException("Day " + days.get(i).getDisplayName(TextStyle.FULL, Locale.US) + "With ID = " + days.get(i).getValue() + " Not Found");
                    }

                }

                PreparedStatement saveSchedule = connection.prepareStatement(SAVE_SCHEDULE, Statement.RETURN_GENERATED_KEYS);
                saveSchedule.setObject(1, schedule.getTime());
                int affectedRows = saveSchedule.executeUpdate();

                if (affectedRows == 0) {
                    connection.rollback();
                    throw new SQLException("Creating schedule failed, no rows affected.");
                }


                try (ResultSet generatedKeys = saveSchedule.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        schedule.setId(generatedKeys.getInt(1));
                    } else {
                        connection.rollback();
                        throw new SQLException("Creating Schedule failed, no ID obtained.");
                    }
                }

                PreparedStatement saveScheduleWithDays = connection.prepareStatement(SAVE_SCHEDULE_DAYS_TIME);
                saveScheduleWithDays.setInt(1, schedule.getId());
                for (int day : daysIndices) {
                    saveScheduleWithDays.setInt(2, day);
                    saveScheduleWithDays.executeUpdate();
                }
            } catch (SQLException e) {
                connection.rollback();
                System.out.println("Creating schedule failed ->" + e.getMessage());
            }
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                System.out.println("Creating schedule rollback failed" + e.getMessage());
            }
            System.out.println("Creating schedule failed" + e.getMessage());
        }
        return schedule;
    }
}

