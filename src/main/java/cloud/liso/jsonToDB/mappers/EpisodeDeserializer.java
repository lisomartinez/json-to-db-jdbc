package cloud.liso.jsonToDB.mappers;

import cloud.liso.jsonToDB.model.Episode;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

public class EpisodeDeserializer {

    private DeserializerUtils utils;

    public EpisodeDeserializer(DeserializerUtils utils) {
        this.utils = utils;
    }

    public List<Episode> getEpisodes(JsonNode node) {
        List<Episode> episodes = new ArrayList<>();
        JsonNode seasonsNode = node.get("episodes");
        if (seasonsNode == null) return episodes;

        for (JsonNode episode : seasonsNode) {
            episodes.add(getEpisodeDto(episode));
        }
        return episodes;
    }

    private Episode getEpisodeDto(JsonNode episode) {
        return Episode.builder()
                .id(utils.getIntOrDefault(episode, "id"))
                .tvMaze(utils.getOrDefault(episode, "url"))
                .name(utils.getOrDefault(episode, "name"))
                .number(utils.getIntOrDefault(episode, "number"))
                .airdate(utils.getDateOrDefault(episode, "airdate"))
                .airtime(utils.getTimeOrDefault(episode, "airtime"))
                .runtime(utils.getIntOrDefault(episode, "runtime"))
                .image(utils.getImageOrDefault(episode))
                .summary(utils.getSummaryJson(episode))
                .build();
    }

}
