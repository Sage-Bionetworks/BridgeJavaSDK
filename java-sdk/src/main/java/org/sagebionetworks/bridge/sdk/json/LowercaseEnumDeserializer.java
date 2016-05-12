package org.sagebionetworks.bridge.sdk.json;

import java.io.IOException;

import org.sagebionetworks.bridge.sdk.models.studies.OperatingSystem;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;

@SuppressWarnings("serial")
class LowercaseEnumDeserializer extends StdScalarDeserializer<Enum<?>> {

    protected LowercaseEnumDeserializer(Class<Enum<?>> clazz) {
        super(clazz);
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Enum<?> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String text = jp.getText().toUpperCase();
        // We set this enumeration deserializer as a module for the default ObjectMapper we use, and it is very
        // simple. But in the case of the OperatingSystem enum, we want to use a label that is not the name of the 
        // enumeration, so that requires special handling (Jackson doesn't recognize the @JsonCreator annotation once 
        // we're using our own deserializer).
        if (handledType() == OperatingSystem.class) {
            return OperatingSystem.create(text);
        }
        return (Enum<?>) Enum.valueOf((Class<Enum>)handledType(), text);
    }    
}
