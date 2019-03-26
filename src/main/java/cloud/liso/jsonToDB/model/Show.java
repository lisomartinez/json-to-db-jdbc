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

    public Show(String name, String type, String language, List<Genre> genres, String status, int runtime, LocalDate premiered, String officialSite, Schedule schedule, String imdb, String tvMaze, String image, String summary, LocalDateTime lastUpdate, List<Season> seasons) {
        this.name = name;
        this.type = type;
        this.language = language;
        this.genres = genres;
        this.status = status;
        this.runtime = runtime;
        this.premiered = premiered;
        this.officialSite = officialSite;
        this.schedule = schedule;
        this.imdb = imdb;
        this.tvMaze = tvMaze;
        this.image = image;
        this.summary = summary;
        this.lastUpdate = lastUpdate;
        this.seasons = seasons;
    }
}
