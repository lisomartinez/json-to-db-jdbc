package cloud.liso.jsonToDB.mappers;

import cloud.liso.jsonToDB.model.Episode;
import cloud.liso.jsonToDB.model.Season;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

public class SeasonDeserializer {
    private DeserializerUtils utils;
    private EpisodeDeserializer episodeDeserializer;

    public SeasonDeserializer(DeserializerUtils utils, EpisodeDeserializer episodeDeserializer) {
        this.utils = utils;
        this.episodeDeserializer = episodeDeserializer;
    }

    public List<Season> getSeasons(JsonNode node) {
        List<Season> seasons = new ArrayList<>();
        JsonNode seasonsNode = node.get("seasons");
        if (seasonsNode == null) return seasons;

        for (JsonNode season : seasonsNode) {
            seasons.add(getSeasonDto(season));
        }

        return seasons;
    }

    private Season getSeasonDto(JsonNode season) {
        List<Episode> episodes = episodeDeserializer.getEpisodes(season);
        return Season.builder()
                .tvmazeId(utils.getIntOrDefault(season, "id"))
                .tvMaze(utils.getOrDefault(season, "url"))
                .number(utils.getIntOrDefault(season, "number"))
                .episodeOrder(utils.getIntOrDefault(season, "episodeOrder"))
                .premiereDate(utils.getDateOrDefault(season, "premiereDate"))
                .endDate(utils.getDateOrDefault(season, "endDate"))
                .image(utils.getImageOrDefault(season))
                .summary(utils.getSummaryJson(season))
                .episodes(episodes)
                .build();
    }

}
