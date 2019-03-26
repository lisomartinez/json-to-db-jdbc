package cloud.liso.jsonToDB;

import cloud.liso.jsonToDB.Repository.*;
import cloud.liso.jsonToDB.Repository.mysql.*;
import cloud.liso.jsonToDB.database.MysqlConnector;
import cloud.liso.jsonToDB.model.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    private ShowRepository showRepository;
    private SeasonRepository seasonRepository;
    private EpisodeRepository episodeRepository;
    private ScheduleRepository scheduleRepository;
    private GenreRepository genreRepository;

    public Main(ShowRepository showRepository, SeasonRepository seasonRepository, EpisodeRepository episodeRepository, ScheduleRepository scheduleRepository, GenreRepository genreRepository) {
        this.showRepository = showRepository;
        this.seasonRepository = seasonRepository;
        this.episodeRepository = episodeRepository;
        this.scheduleRepository = scheduleRepository;
        this.genreRepository = genreRepository;
    }

    public static void main(String[] args) {
        Main main = new Main(new MySqlShowRepository(), new MySqlSeasonRepository(), new MySqlEpisodeRepository(), new MysqlScheduleRepository(), new MysqlGenreRepository());
        main.getShows(main);

    }

    private void getShows(Main main) {
        Connection connection = MysqlConnector.getConnection();


        try (Stream<Path> paths = Files.walk(Paths.get("/home/liso/Documents/show/page0.json"))) {
            List<Show> shows = paths.filter(Files::isRegularFile).flatMap(this::parseShow).map(this::saveShow).collect(Collectors.toList());
//            System.out.println("shows = " + shows);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

    private Show saveShow(FullShow fullShow) {
        Show show = fullShow.getShow();
        show.setLastUpdate(LocalDateTime.now());
        List<Genre> genres = show.getGenres().stream().map(genreRepository::save).collect(Collectors.toList());
        Schedule schedule = scheduleRepository.save(show.getSchedule());
        show = showRepository.save(show);

        List<Season> season = seasonRepository.saveAll(show.getId(), fullShow.getSeasons());

        for (Season season1 : season) {
            List<Episode> episodes = episodeRepository.saveAll(season1.getId(), season1.getEpisodes());
            season1.setEpisodes(episodes);
        }

        return show;
    }

    private Stream<FullShow> parseShow(Path path) {
        List<FullShow> shows = null;
        try {
            BufferedReader bufferedReader = Files.newBufferedReader(path);
            ObjectMapper objectMapper = new ObjectMapper();
            shows = objectMapper.readValue(bufferedReader, new TypeReference<List<FullShow>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return shows.stream();
    }


//    private void saveShow() {
//        Connection connection = MysqlConnector.getConnection();
//        Genre thriller = new MysqlGenreRepository().save(Genre.of("Thriller"));
//        List<DayOfWeek> days = new ArrayList<>(asList(DayOfWeek.MONDAY, DayOfWeek.FRIDAY));
//        Schedule schedule = new MysqlScheduleRepository().save(Schedule.of(days, LocalTime.of(22, 0)));
//
//        Show show = Show.builder()
//                .name("Under the Dome")
//                .tvMaze("http://www.tvmaze.com/shows/1/under-the-dome")
//                .type("Scripted")
//                .language("English")
//                .runtime(60)
//                .premiered(LocalDate.of(2013, 6, 24))
//                .schedule(schedule)
//                .genres(new ArrayList<>(asList(thriller)))
//                .officialSite("http://www.cbs.com/shows/under-the-dome/")
//                .imdb("https://www.imdb.com/title/tt1553656")
//                .image("http://static.tvmaze.com/uploads/images/original_untouched/81/202627.jpg")
//                .summary("Under the Dome is the story of a small town that is suddenly and inexplicably sealed off from the rest of the world by an enormous transparent dome. The town's inhabitants must deal with surviving the post-apocalyptic conditions while searching for answers about the dome, where it came from and if and when it will go away.")
//                .lastUpdate(LocalDateTime.now())
//                .status("Running")
//                .build();
//
//        show = new MySqlShowRepository().save(show);
//
//        List<Episode> episodes = new ArrayList<>();
//        Episode eleven = Episode.builder()
//                .id(142270)
//                .name("Move On")
//                .number(1)
//                .airdate(LocalDate.of(2015, 6, 25))
//                .airtime(LocalTime.of(22, 0))
//                .runtime(60)
//                .image("http://static.tvmaze.com/uploads/images/original_untouched/12/31233.jpg")
//                .tvMaze("http://www.tvmaze.com/episodes/142270/under-the-dome-3x01-move-on")
//                .summary("Season 3 begins with Chester's Mill residents appearing inside and outside the Dome following an evacuation into the tunnels beneath the town. Meanwhile, the Dome begins to reveal its ultimate agenda; and surprising alliances form as new residents emerge.")
//                .build();
//        episodes.add(eleven);
//
//        List<Season> seasons = new ArrayList<>();
//
//        Season seasonThree = Season.builder()
//                .id(3)
//                .number(3)
//                .name("name")
//                .episodeOrder(13)
//                .premiereDate(LocalDate.of(2015, 6, 25))
//                .endDate(LocalDate.of(2015, 9, 10))
//                .image("http://static.tvmaze.com/uploads/images/original_untouched/24/60942.jpg")
//                .tvMaze("http://www.tvmaze.com/seasons/2/under-the-dome-season-2")
//                .summary("N/A")
//                .build();
//
//        seasons.add(seasonThree);
//
//        seasons = new MySqlSeasonRepository().saveAll(show.getId(), seasons);
//
//        episodes = new MySqlEpisodeRepository().saveAll(seasons.get(0).getId(), episodes);
//
//        seasonThree.setEpisodes(episodes);
//    }


}

