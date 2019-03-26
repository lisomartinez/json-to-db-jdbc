package cloud.liso.jsonToDB.mappers;

public class DeserializerFactory {

    private static final ShowDeserializer showDeserializer = new ShowDeserializer(new DeserializerUtils());
    private static final SeasonDeserializer seasonsDeserializer = new SeasonDeserializer(new DeserializerUtils(), new EpisodeDeserializer(new DeserializerUtils()));

    public static ShowDeserializer createShowDeserializer() {
        return showDeserializer;
    }

    public static SeasonDeserializer createSeasonDeserializer() {
        return seasonsDeserializer;
    }
}
