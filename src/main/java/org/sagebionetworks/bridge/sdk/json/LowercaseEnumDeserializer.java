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
        // This is hacky but the value we're using is not the enum name, it's a string os name in the enumeration,
        // so adjusting here.
        if (handledType() == OperatingSystem.class) {
            return OperatingSystem.create(text);
        }
        return (Enum<?>) Enum.valueOf((Class<Enum>)handledType(), text);
    }    
}
