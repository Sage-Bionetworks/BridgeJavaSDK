package org.sagebionetworks.bridge.rest.gson;

import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class DateTimeTypeAdapter extends TypeAdapter<DateTime> {
    
    private static final DateTimeFormatter FORMATTER = ISODateTimeFormat.dateTime().withOffsetParsed();

    @Override
    public DateTime read(JsonReader reader) throws IOException {
        String src = reader.nextString();
        return FORMATTER.parseDateTime(src);
    }

    @Override
    public void write(JsonWriter writer, DateTime dateTime) throws IOException {
        if (dateTime != null) {
            writer.value(FORMATTER.print(dateTime));    
        } else {
            writer.nullValue();
        }
    }
}
