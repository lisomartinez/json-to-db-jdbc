package cloud.liso.jsonToDB.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class Season {
    private int id;
    private int number;
    private String name;
    private int episodeOrder;
    private LocalDate premiereDate;
    private LocalDate endDate;
    private String image;
    private String tvMaze;
    private String summary;
    private List<Episode> episodes;

    public Season(int number, String name, int episodeOrder, LocalDate premiereDate, LocalDate endDate, String image, String tvMaze, String summary, List<Episode> episodes) {
        this.number = number;
        this.name = name;
        this.episodeOrder = episodeOrder;
        this.premiereDate = premiereDate;
        this.endDate = endDate;
        this.image = image;
        this.tvMaze = tvMaze;
        this.summary = summary;
        this.episodes = episodes;
    }
}
