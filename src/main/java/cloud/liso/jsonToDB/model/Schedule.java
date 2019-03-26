package cloud.liso.jsonToDB.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class Schedule {
    private int id;
    private List<DayOfWeek> days;
    private LocalTime time;

    public Schedule(List<DayOfWeek> days, LocalTime time) {
        this.days = days;
        this.time = time;
    }

    public static Schedule of(List<DayOfWeek> days, LocalTime time) {
        return new Schedule(days, time);
    }
}
