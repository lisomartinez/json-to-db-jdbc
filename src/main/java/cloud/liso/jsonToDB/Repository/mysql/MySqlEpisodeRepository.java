package cloud.liso.jsonToDB.Repository.mysql;

import cloud.liso.jsonToDB.Repository.EpisodeRepository;
import cloud.liso.jsonToDB.database.MysqlConnector;
import cloud.liso.jsonToDB.model.Episode;

import java.sql.*;
import java.util.List;
import java.util.stream.Collectors;

public class MySqlEpisodeRepository implements EpisodeRepository {
    private static final String FIND_BY_SEASONS_AND_EPISODE_NUMBER = "SELECT episode_id FROM episode WHERE season_Id = ? AND number = ?";
    private static final String SAVE_EPISODE = "INSERT INTO " +
            "episode(`season_id`, `number`, `name`, `airdate`, `airtime`, `runtime`, `summary`, `image_url`, `tvmaze_url`) " +
            "VALUES(?,?,?,?,?,?,?,?,?)";

    private final Connection connection;

    public MySqlEpisodeRepository() {
        connection = MysqlConnector.getConnection();
    }

    @Override
    public List<Episode> saveAll(int seasonId, List<Episode> episodes) {
        return episodes.stream().map(episode -> save(seasonId, episode)).collect(Collectors.toList());
    }

    private Episode save(int seasonId, Episode episode) {
        try {
            connection.setAutoCommit(false);
            try {
                PreparedStatement findByShowIdAndEpisodeNumber = connection.prepareStatement(FIND_BY_SEASONS_AND_EPISODE_NUMBER);
                findByShowIdAndEpisodeNumber.setInt(1, seasonId);
                findByShowIdAndEpisodeNumber.setInt(2, episode.getNumber());
                ResultSet result = findByShowIdAndEpisodeNumber.executeQuery();
                if (result.next()) {
                    episode.setId(result.getInt(1));
                    return episode;
                }


                PreparedStatement save = connection.prepareStatement(SAVE_EPISODE, Statement.RETURN_GENERATED_KEYS);
                save.setInt(1, seasonId);
                save.setInt(2, episode.getNumber());
                save.setString(3, episode.getName());
                save.setObject(4, episode.getAirdate());
                save.setObject(5, episode.getAirtime());
                save.setInt(6, episode.getRuntime());
                save.setString(7, episode.getSummary());
                save.setString(8, episode.getImage());
                save.setString(9, episode.getTvMaze());

                int affectedRows = save.executeUpdate();

                if (affectedRows == 0) {
                    connection.rollback();
                    throw new SQLException("Creating Episode failed, no rows affected.");
                }


                try (ResultSet generatedKeys = save.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        episode.setId(generatedKeys.getInt(1));
                    } else {
                        connection.rollback();
                        throw new SQLException("Creating Episode failed, no ID obtained.");
                    }
                }

            } catch (SQLException e) {
                connection.rollback();
                System.out.println("Creating Episode failed ->" + e.getMessage());
            }
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                System.out.println("Creating Episode rollback failed" + e.getMessage());
            }
        }
        return episode;
    }
}
