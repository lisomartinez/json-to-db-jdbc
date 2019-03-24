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
    private int episodeOrder;
    private LocalDate premiereDate;
    private LocalDate endDate;
    private String image;
    private String tvMaze;
    private String summary;
    private List<Episode> episodes;

}
