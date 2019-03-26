package cloud.liso.jsonToDB.Repository.mysql;

import cloud.liso.jsonToDB.Repository.SeasonRepository;
import cloud.liso.jsonToDB.database.MysqlConnector;
import cloud.liso.jsonToDB.model.Season;

import java.sql.*;
import java.util.List;
import java.util.stream.Collectors;

public class MySqlSeasonRepository implements SeasonRepository {

    private static final String FIND_BY_SHOW_SEASON_NUMBER = "SELECT * FROM season WHERE show_id = ? AND number = ?";

    private static final String SAVE = "INSERT INTO season" +
            "(show_id, number, name, episode_order, premiere_date, end_date, summary, image_url, tvmaze_url)" +
            " VALUES(?,?,?,?,?,?,?,?,?)";

    private final Connection connection;

    public MySqlSeasonRepository() {
        connection = MysqlConnector.getConnection();
    }

    public List<Season> saveAll(int showId, List<Season> seasons) {
        return seasons.stream().map(season -> save(season, showId)).collect(Collectors.toList());
    }

    private Season save(Season season, int showId) {
        try {
            connection.setAutoCommit(false);
            try {
                PreparedStatement findByShowIdAndSeasonNumber = connection.prepareStatement(FIND_BY_SHOW_SEASON_NUMBER);
                findByShowIdAndSeasonNumber.setInt(1, showId);
                findByShowIdAndSeasonNumber.setInt(2, season.getNumber());
                ResultSet result = findByShowIdAndSeasonNumber.executeQuery();

                if (result.next()) {
                    season.setId(result.getInt(2));
                    return season;
                }


                PreparedStatement save = connection.prepareStatement(SAVE, Statement.RETURN_GENERATED_KEYS);
                save.setInt(1, showId);
                save.setInt(2, season.getNumber());
                save.setString(3, season.getName());
                save.setInt(4, season.getEpisodeOrder());
                save.setObject(5, season.getPremiereDate());
                save.setObject(6, season.getEndDate());
                save.setString(7, season.getSummary());
                save.setString(8, season.getImage());
                save.setString(9, season.getTvMaze());

                int affectedRows = save.executeUpdate();

                if (affectedRows == 0) {
                    connection.rollback();
                    throw new SQLException("Creating Season failed, no rows affected.");
                }


                try (ResultSet generatedKeys = save.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        season.setId(generatedKeys.getInt(1));
                    } else {
                        connection.rollback();
                        throw new SQLException("Creating Season failed, no ID obtained.");
                    }
                }

            } catch (SQLException e) {
                connection.rollback();
                System.out.println("Creating Season failed ->" + e.getMessage());
            }
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                System.out.println("Creating Season rollback failed" + e.getMessage());
            }
        }
        return season;
    }
}
