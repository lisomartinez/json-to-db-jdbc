package cloud.liso.jsonToDB.mappers;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeserializerUtils {
    private final static Pattern pattern = Pattern.compile("(<.+?>)");

    private static final String DEFAULT = "N/A";

    public int getIntOrDefault(JsonNode node, String name) {
        JsonNode field = node.get(name);
        if (field == null) return 0;
        return field.asInt();
    }


    public String getOrDefault(JsonNode node, String name) {
        String field = node.get(name).asText();
        return field == null ? "N/A" : field;
    }

    public LocalDate getDateOrDefault(JsonNode node, String name) {
        if (node.get(name) == null) return LocalDate.of(1900, 1, 1);
        String field = node.get(name).asText();
        return field.equals("null") || field.isEmpty() ? LocalDate.of(1900, 1, 1) : LocalDate.parse(field, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public LocalTime getTimeOrDefault(JsonNode node, String name) {
        if (node.get(name) == null) return LocalTime.of(0, 0);

        String field = node.get(name).asText();
        LocalTime time;
        if (field.equals("null") || field.isEmpty()) {
            time = LocalTime.of(0, 0);
        } else {
            time = LocalTime.parse(field, DateTimeFormatter.ISO_TIME.withLocale(Locale.US));
        }
        return time;
    }


    public String getImageOrDefault(JsonNode node) {
        JsonNode imageNode = node.get("image");

        String image;
        if (imageNode == null) {
            image = "N/A";
        } else {
            JsonNode originalImage = imageNode.get("original");

            if (originalImage != null) {
                image = originalImage.asText();
            } else {
                image = "N/A";
            }
        }
        return image;
    }

    public String getImdbUrl(JsonNode node) {
        JsonNode externals = node.get("externals");

        if (externals == null) return "N/A";

        JsonNode imdb = externals.get("imdb");

        if (imdb == null) return "N/A";

        return "https://www.imdb.com/title/" + imdb.asText();
    }

    public String getSummaryJson(JsonNode node) {
        String summaryJson = node.get("summary").asText();
        if (summaryJson == null || summaryJson.isEmpty()) {
            return "N/A";
        } else {
            Matcher matcher = pattern.matcher(summaryJson);
            return matcher.replaceAll("");
        }
    }
}
