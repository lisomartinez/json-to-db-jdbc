package cloud.liso.jsonToDB.Repository.mysql;

import cloud.liso.jsonToDB.Repository.ShowRepository;
import cloud.liso.jsonToDB.database.MysqlConnector;
import cloud.liso.jsonToDB.model.Genre;
import cloud.liso.jsonToDB.model.Show;

import java.sql.*;
import java.util.List;

public class MySqlShowRepository implements ShowRepository {

    private static final String FIND_BY_NAME = "SELECT * FROM `show` WHERE `show`.name = ?";

    private static final String SAVE = "INSERT INTO " +
            "`show`(tvmaze_id, name, type, status, language, premiered, runtime, " +
            "schedule_id, image_url, imdb_url, official_site_url, tv_maze_url, summary, last_update, rating)" +
            " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SAVE_SHOW_GENRE = "INSERT INTO show_genre(show_id, genre_id) VALUES(?,?)";

    private final Connection connection;

    public MySqlShowRepository() {
        this.connection = MysqlConnector.getConnection();
    }

    @Override
    public Show save(Show show) {
        try {
                PreparedStatement save = connection.prepareStatement(SAVE, Statement.RETURN_GENERATED_KEYS);
            save.setInt(1, show.getTvmazeId());
            save.setString(2, show.getName());
            save.setString(3, show.getType());
            save.setString(4, show.getStatus());
            save.setString(5, show.getLanguage());
            save.setObject(6, show.getPremiered());
            save.setInt(7, show.getRuntime());
            save.setInt(8, show.getSchedule().getId());
            save.setString(9, show.getImage());
            save.setString(10, show.getImdb());
            save.setString(11, show.getOfficialSite());
            save.setString(12, show.getTvMaze());
            save.setString(13, show.getSummary());
            save.setObject(14, show.getLastUpdate());
            save.setDouble(15, show.getRating());

            save.executeUpdate();

                try (ResultSet generatedKeys = save.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        show.setId(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Creating Show failed, no ID obtained.");
                    }
                }

                List<Genre> genres = show.getGenres();
                PreparedStatement saveShowGenres = connection.prepareStatement(SAVE_SHOW_GENRE);
                saveShowGenres.setInt(1, show.getId());
                for (Genre genre : genres) {
                    saveShowGenres.setInt(2, genre.getId());
                    saveShowGenres.executeUpdate();
                }

            } catch (SQLException e) {
                System.out.println("Creating Show failed -> " + e.getMessage());
            }
        return show;
    }
}
