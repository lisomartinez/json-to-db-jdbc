package cloud.liso;

import cloud.liso.jsonToDB.Repository.*;
import cloud.liso.jsonToDB.Repository.mysql.*;
import cloud.liso.jsonToDB.model.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Batch {
    private ShowRepository showRepository;
    private SeasonRepository seasonRepository;
    private EpisodeRepository episodeRepository;
    private ScheduleRepository scheduleRepository;
    private GenreRepository genreRepository;
    private ObjectMapper objectMapper;

    public Batch() {
        showRepository = new MySqlShowRepository();
        seasonRepository = new MySqlSeasonRepository();
        episodeRepository = new MySqlEpisodeRepository();
        scheduleRepository = new MysqlScheduleRepository();
        genreRepository = new MysqlGenreRepository();
        objectMapper = new ObjectMapper();
    }


    public void run() {
        try (Stream<Path> paths = Files.walk(Paths.get("/home/liso/Documents/show/page0.json"))) {
            List<Show> shows = paths.filter(Files::isRegularFile).flatMap(this::parseShow).map(this::saveShow).collect(Collectors.toList());
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private Stream<FullShow> parseShow(Path path) {
        List<FullShow> shows = null;
        try {
            BufferedReader bufferedReader = Files.newBufferedReader(path);

            shows = objectMapper.readValue(bufferedReader, new TypeReference<List<FullShow>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return shows.stream();
    }

    private Show saveShow(FullShow fullShow) {
        Show show = fullShow.getShow();
        show.setLastUpdate(LocalDateTime.now());
        List<Genre> genres = show.getGenres().stream().map(genreRepository::save).collect(Collectors.toList());
        show.setGenres(genres);
        Schedule schedule = scheduleRepository.save(show.getSchedule());
        show.setSchedule(schedule);

        show = showRepository.save(show);

        List<Season> seasons = seasonRepository.saveAll(show.getId(), fullShow.getSeasons());

        for (Season season : seasons) {
            List<Episode> episodes = episodeRepository.saveAll(season.getId(), season.getEpisodes());
            season.setEpisodes(episodes);
        }
        show.setSeasons(seasons);

        return show;
    }

}
