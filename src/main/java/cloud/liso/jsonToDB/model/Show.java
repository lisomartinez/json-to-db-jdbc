package cloud.liso.jsonToDB.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class Show {
    private int id;
    private String name;
    private String type;
    private String language;
    private List<Genre> genres;
    private String status;
    private int runtime;
    private LocalDate premiered;
    private String officialSite;
    private Schedule schedule;
    private String imdb;
    private String tvMaze;
    private String image;
    private String summary;
    private LocalDateTime lastUpdate;
    private List<Season> seasons;
}
