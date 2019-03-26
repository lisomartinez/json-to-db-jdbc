package cloud.liso.jsonToDB.mappers;

import cloud.liso.jsonToDB.model.Genre;
import cloud.liso.jsonToDB.model.Schedule;
import cloud.liso.jsonToDB.model.Show;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ShowDeserializer {
    private final static Pattern pattern = Pattern.compile("(<.+?>)");

    private static final String DEFAULT = "N/A";

    private DeserializerUtils utils;

    public ShowDeserializer(DeserializerUtils utils) {
        this.utils = utils;
    }

    public Show getShow(JsonNode show) {
        return Show.builder()
                .id(utils.getIntOrDefault(show, "id"))
                .tvMaze(utils.getOrDefault(show, "url"))
                .name(utils.getOrDefault(show, "name"))
                .type(utils.getOrDefault(show, "type"))
                .language(utils.getOrDefault(show, "language"))
                .genres(getGenresOrEmpty(show))
                .status(utils.getOrDefault(show, "status"))
                .runtime(utils.getIntOrDefault(show, "runtime"))
                .premiered(utils.getDateOrDefault(show, "premiered"))
                .officialSite(utils.getOrDefault(show, "officialSite"))
                .schedule(getScheduleDto(show))
                .imdb(utils.getImdbUrl(show))
                .image(utils.getImageOrDefault(show))
                .summary(utils.getSummaryJson(show))
                .build();
    }

    private Schedule getScheduleDto(JsonNode node) {
        JsonNode scheduleJson = node.get("schedule");
        if (scheduleJson == null) return Schedule.of(new ArrayList<>(), LocalTime.of(00, 00));
        List<DayOfWeek> dayOfWeeks = extractDays(scheduleJson);
        LocalTime time = extractTime(scheduleJson);
        return Schedule.of(dayOfWeeks, time);
    }

    private List<DayOfWeek> extractDays(JsonNode scheduleJson) {
        List<DayOfWeek> dayOfWeeks = new ArrayList<>();

        JsonNode daysNode = scheduleJson.get("days");
        if (daysNode != null) {
            for (JsonNode jsonNode : daysNode) {
                dayOfWeeks.add(DayOfWeek.valueOf(jsonNode.asText().toUpperCase()));
            }
        }
        return dayOfWeeks;
    }

    private LocalTime extractTime(JsonNode scheduleJson) {
        JsonNode timeNode = scheduleJson.get("time");

        LocalTime time;

        if (timeNode != null && !timeNode.asText().isEmpty()) {
            time = LocalTime.parse(timeNode.asText(), DateTimeFormatter.ofPattern("HH:mm"));
        } else {
            time = LocalTime.of(0, 0);
        }
        return time;
    }


    private List<Genre> getGenresOrEmpty(JsonNode node) {
        JsonNode genresArray = node.get("genres");

        if (genresArray == null) return new ArrayList<>();

        List<Genre> genres = new ArrayList<>();
        for (JsonNode jsonNode : genresArray) {
            genres.add(Genre.of(jsonNode.asText()));
        }
        return genres;
    }
}
