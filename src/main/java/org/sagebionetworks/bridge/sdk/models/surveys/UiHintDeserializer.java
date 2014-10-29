package org.sagebionetworks.bridge.sdk.models.surveys;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

class UiHintDeserializer extends JsonDeserializer<UiHint> {
    @Override
    public UiHint deserialize(JsonParser parser, DeserializationContext context) throws IOException,
            JsonProcessingException {
        String value = parser.getText();
        return UiHint.valueOf(value.toUpperCase());
    }
}
