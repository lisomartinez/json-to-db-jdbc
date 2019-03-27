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
            "episode(`season_id`, tvmaze_id, `number`, `name`, `airdate`, `airtime`, `runtime`, `summary`, `image_url`, `tvmaze_url`) " +
            "VALUES(?,?,?,?,?,?,?,?,?,?)";

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
                PreparedStatement save = connection.prepareStatement(SAVE_EPISODE, Statement.RETURN_GENERATED_KEYS);
                save.setInt(1, seasonId);
            save.setInt(2, episode.getTvmazeId());
            save.setInt(3, episode.getNumber());
            save.setString(4, episode.getName());
            save.setObject(5, episode.getAirdate());
            save.setObject(6, episode.getAirtime());
            save.setInt(7, episode.getRuntime());
            save.setString(8, episode.getSummary());
            save.setString(9, episode.getImage());
            save.setString(10, episode.getTvMaze());

            save.executeUpdate();

                try (ResultSet generatedKeys = save.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        episode.setId(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Creating Episode failed, no ID obtained.");
                    }
                }

            } catch (SQLException e) {
                System.out.println("Creating Episode failed ->" + e.getMessage());
            }

        return episode;
    }
}
