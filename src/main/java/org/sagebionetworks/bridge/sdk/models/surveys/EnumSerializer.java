package org.sagebionetworks.bridge.sdk.models.surveys;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

class EnumSerializer extends JsonSerializer<Enum<?>> {

    public void serialize(Enum<?> value, JsonGenerator generator, SerializerProvider provider) throws IOException,
            JsonProcessingException {
        generator.writeString(value.name().toLowerCase());
    }
}
