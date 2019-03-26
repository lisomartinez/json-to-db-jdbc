package cloud.liso.jsonToDB.Repository;

import cloud.liso.jsonToDB.model.Episode;

import java.util.List;

public interface EpisodeRepository {
    List<Episode> saveAll(int seasonId, List<Episode> episodes);
}
