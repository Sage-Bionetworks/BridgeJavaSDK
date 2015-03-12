package org.sagebionetworks.bridge.sdk;

import java.io.IOException;
import java.lang.reflect.Method;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;

@SuppressWarnings("serial")
class LowerEnumDeserializer extends StdScalarDeserializer<Enum<?>> {

    protected LowerEnumDeserializer(Class<Enum<?>> clazz) {
        super(clazz);
    }

    @Override
    public Enum<?> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String text = jp.getText().toUpperCase();
        try {
            Method valueOfMethod = getValueClass().getDeclaredMethod("valueOf", String.class);
            return (Enum<?>) valueOfMethod.invoke(null, text);
        } catch (Exception e) {
            throw new RuntimeException("Cannot deserialize enum " + getValueClass().getName() + " from " + text, e);
        }
    }    
    
}
