package be.phw.gedclient.client.document.config;

import com.google.gson.*;
import feign.gson.GsonDecoder;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.TimeZone;

public class CustomGsonDecoder extends GsonDecoder {

    public CustomGsonDecoder() {
        super(new GsonBuilder().registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            @Override
            public Date deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext)
                    throws JsonParseException {
                String date = json.getAsJsonPrimitive().getAsString();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                format.setTimeZone(TimeZone.getTimeZone("GMT"));

                try {
                    return format.parse(date);
                } catch (ParseException exp) {
                    System.err.println("Failed to parse Date:" + exp);
                    return null;
                }
            }
        }).registerTypeAdapter(ZonedDateTime.class, new JsonDeserializer<ZonedDateTime>() {
            @Override
            public ZonedDateTime deserialize(JsonElement json, Type type,
                    JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                return ZonedDateTime.parse(json.getAsJsonPrimitive().getAsString());
            }
        }).create());
    }

}
