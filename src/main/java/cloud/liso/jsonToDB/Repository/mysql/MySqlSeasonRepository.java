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
            "(show_id, tvmaze_id, number, episode_order, premiere_date, end_date, summary, image_url, tvmaze_url)" +
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
                PreparedStatement save = connection.prepareStatement(SAVE, Statement.RETURN_GENERATED_KEYS);
                save.setInt(1, showId);
            save.setInt(2, season.getTvmazeId());
            save.setInt(3, season.getNumber());
                save.setInt(4, season.getEpisodeOrder());
                save.setObject(5, season.getPremiereDate());
                save.setObject(6, season.getEndDate());
                save.setString(7, season.getSummary());
                save.setString(8, season.getImage());
                save.setString(9, season.getTvMaze());

            save.executeUpdate();

                try (ResultSet generatedKeys = save.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        season.setId(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Creating Season failed, no ID obtained.");
                    }
                }

            } catch (SQLException e) {
                System.out.println("Creating Season failed ->" + e.getMessage());
            }
        return season;
    }
}
