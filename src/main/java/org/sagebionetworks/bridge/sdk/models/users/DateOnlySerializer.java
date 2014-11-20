package org.sagebionetworks.bridge.sdk.models.users;

import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

class DateOnlySerializer extends JsonSerializer<DateTime> {

    @Override
    public void serialize(DateTime date, JsonGenerator generator, SerializerProvider provider) throws IOException,
            JsonProcessingException {
        generator.writeString(date.toString(ISODateTimeFormat.date()));        
    }

}
