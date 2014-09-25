package org.sagebionetworks.bridge.sdk.models.surveys;

import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

class DateTimeDeserializer extends JsonDeserializer<Long> {
    
    private static final DateTimeFormatter dateFmt = ISODateTimeFormat.date();
    private static final DateTimeFormatter dateTimeFmt = ISODateTimeFormat.dateTime();
    
    @Override
    public Long deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
        String date = parser.getText();
        return convertToMillisFromEpoch(date);
    }
    
    private long convertToMillisFromEpoch(String d) {
        DateTime date = null;
        if (d.length() == "yyyy-MM-dd".length()) {
            date = dateFmt.withZone(DateTimeZone.UTC).parseDateTime(d);
        } else {
            date = dateTimeFmt.withZone(DateTimeZone.UTC).parseDateTime(d);
        }
        return date.getMillis();
    }

}
