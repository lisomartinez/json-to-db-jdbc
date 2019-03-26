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
            "`show`(name, type, status, language, premiered, runtime, " +
            "schedule_id, image_url, imdb_url, official_site_url, tv_maze_url, summary, last_update)" +
            " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SAVE_SHOW_GENRE = "INSERT INTO show_genre(show_id, genre_id) VALUES(?,?)";

    private final Connection connection;

    public MySqlShowRepository() {
        this.connection = MysqlConnector.getConnection();
    }

    @Override
    public Show save(Show show) {
        try {
            connection.setAutoCommit(false);
            try {
                PreparedStatement find = connection.prepareStatement(FIND_BY_NAME);
                find.setString(1, show.getName());
                ResultSet result = find.executeQuery();
                if (result.next()) {
                    show.setId(result.getInt(1));
                    return show;
                }


                PreparedStatement save = connection.prepareStatement(SAVE, Statement.RETURN_GENERATED_KEYS);
                save.setString(1, show.getName());
                save.setString(2, show.getType());
                save.setString(3, show.getStatus());
                save.setString(4, show.getLanguage());
                save.setDate(5, Date.valueOf(show.getPremiered()));
                save.setInt(6, show.getRuntime());
                save.setInt(7, show.getSchedule().getId());
                save.setString(8, show.getImage());
                save.setString(9, show.getImdb());
                save.setString(10, show.getOfficialSite());
                save.setString(11, show.getTvMaze());
                save.setString(12, show.getSummary());
                save.setTimestamp(13, Timestamp.valueOf(show.getLastUpdate()));

                int affectedRows = save.executeUpdate();

                if (affectedRows == 0) {
                    connection.rollback();
                    throw new SQLException("Creating Show failed, no rows affected.");
                }


                try (ResultSet generatedKeys = save.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        show.setId(generatedKeys.getInt(1));
                    } else {
                        connection.rollback();
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
                connection.rollback();
                System.out.println("Creating Show failed -> " + e.getMessage());
            }
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                System.out.println("Creating Show rollback failed" + e.getMessage());
            }
        }
        return show;
    }
}
