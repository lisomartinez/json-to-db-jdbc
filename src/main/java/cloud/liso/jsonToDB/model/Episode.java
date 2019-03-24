package cloud.liso.jsonToDB.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
public class Episode {
    private int id;
    private String name;
    private int number;
    private LocalDate airdate;
    private LocalTime airtime;
    private int runtime;
    private String image;
    private String tvMaze;
    private String summary;
}
