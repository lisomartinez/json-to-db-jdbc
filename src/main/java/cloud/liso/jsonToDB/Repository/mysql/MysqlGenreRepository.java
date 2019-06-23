package cloud.liso.jsonToDB.Repository.mysql;

import cloud.liso.jsonToDB.Repository.GenreRepository;
import cloud.liso.jsonToDB.database.MysqlConnector;
import cloud.liso.jsonToDB.model.Genre;

import java.sql.*;

public class MysqlGenreRepository implements GenreRepository {
    private static final String FIND_BY_NAME = "SELECT * FROM genres WHERE name = ?";
    private static final String SAVE = "INSERT INTO genres(name) VALUES(?)";
    private final Connection connection;

    public MysqlGenreRepository() {
        connection = MysqlConnector.getConnection();
    }

    @Override
    public Genre save(Genre genre) {
        try {
            connection.setAutoCommit(false);
            try {

                PreparedStatement find = connection.prepareStatement(FIND_BY_NAME);
                find.setString(1, genre.getName());
                ResultSet result = find.executeQuery();
                if (result.next()) {
                    genre.setId(result.getInt(1));
                    return genre;
                }

                PreparedStatement statement = connection.prepareStatement(SAVE, Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, genre.getName());
                int affectedRows = statement.executeUpdate();

                if (affectedRows == 0) {
                    connection.rollback();
                    throw new SQLException("Creating genre failed, no rows affected.");
                }

                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        genre.setId(generatedKeys.getInt(1));
                    } else {
                        connection.rollback();
                        throw new SQLException("Creating user failed, no ID obtained.");
                    }
                }
            } catch (SQLException e) {
                connection.rollback();
                System.out.println("Creating genre failed ->" + e.getMessage());
            }
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                System.out.println("Creating genre rollback failed" + e.getMessage());
            }
            System.out.println("Creating genre failed" + e.getMessage());
        }
        return genre;
    }
}
