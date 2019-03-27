package cloud.liso.jsonToDB.mappers;

import cloud.liso.jsonToDB.model.DayOfWeek;

public class DeserializerFactory {
    private final ShowDeserializer showDeserializer;
    private final SeasonDeserializer seasonsDeserializer;

    public DeserializerFactory() {
        DayOfWeek dayOfWeek = new DayOfWeek();
        DeserializerUtils utils = new DeserializerUtils(dayOfWeek);
        showDeserializer = new ShowDeserializer(utils);
        seasonsDeserializer = new SeasonDeserializer(utils, new EpisodeDeserializer(utils));
    }

    public ShowDeserializer createShowDeserializer() {
        return showDeserializer;
    }

    public SeasonDeserializer createSeasonDeserializer() {
        return seasonsDeserializer;
    }
}
