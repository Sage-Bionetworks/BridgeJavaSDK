package org.sagebionetworks.bridge.rest.gson;

import java.io.IOException;

import org.joda.time.LocalDate;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class LocalDateTypeAdapter extends TypeAdapter<LocalDate> {

    @Override
    public LocalDate read(JsonReader reader) throws IOException {
        String src = reader.nextString();
        return LocalDate.parse(src);
    }

    @Override
    public void write(JsonWriter writer, LocalDate localDate) throws IOException {
        if (localDate != null) {
            writer.value(localDate.toString());    
        } else {
            writer.nullValue();
        }
    }

}
