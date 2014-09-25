package org.sagebionetworks.bridge.sdk.models.surveys;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

class UIHintDeserializer extends JsonDeserializer<UIHint> {
    @Override
    public UIHint deserialize(JsonParser parser, DeserializationContext context) throws IOException,
            JsonProcessingException {
        String value = parser.getText();
        return UIHint.valueOf(value.toUpperCase());
    }
}
