package cloud.liso.jsonToDB.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class DayOfWeek {
    private Map<String, Integer> days;

    public DayOfWeek() {
        days = new HashMap<>();
        days.put("Monday", 1);
        days.put("Tuesday", 2);
        days.put("Wednesday", 3);
        days.put("Thursday", 4);
        days.put("Friday", 5);
        days.put("Saturday", 6);
        days.put("Sunday", 7);
        days.put("None", 8);
    }

    public int getDayId(String day) {
        return days.get(day);
    }

    public Map.Entry<String, Integer> getDayKeyAndValue(String day) {
        return new HashMap.SimpleImmutableEntry<>(day, days.get(day));
    }
}
