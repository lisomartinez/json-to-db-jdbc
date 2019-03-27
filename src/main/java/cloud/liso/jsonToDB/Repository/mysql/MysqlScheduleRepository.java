package cloud.liso.jsonToDB.Repository.mysql;

import cloud.liso.jsonToDB.Repository.ScheduleRepository;
import cloud.liso.jsonToDB.database.MysqlConnector;
import cloud.liso.jsonToDB.model.Schedule;

import java.sql.*;

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

                PreparedStatement saveSchedule = connection.prepareStatement(SAVE_SCHEDULE, Statement.RETURN_GENERATED_KEYS);
                saveSchedule.setObject(1, schedule.getTime());
            saveSchedule.executeUpdate();

                try (ResultSet generatedKeys = saveSchedule.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        schedule.setId(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Creating Schedule failed, no ID obtained.");
                    }
                }

                PreparedStatement saveScheduleWithDays = connection.prepareStatement(SAVE_SCHEDULE_DAYS_TIME);
                saveScheduleWithDays.setInt(1, schedule.getId());
            for (int day : schedule.getDays()) {
                    saveScheduleWithDays.setInt(2, day);
                    saveScheduleWithDays.executeUpdate();
                }

        } catch (SQLException e) {
                System.out.println("Creating schedule failed ->" + e.getMessage());
            }
        return schedule;
    }
}

