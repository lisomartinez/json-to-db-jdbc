package cloud.liso.jsonToDB.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Genre {
    private int id;
    private String name;

    public Genre(String name) {
        this.name = name;
    }

    public static Genre of(String name) {
        return new Genre(name);
    }
}
