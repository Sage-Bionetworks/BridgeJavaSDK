package org.sagebionetworks.bridge.sdk.json;

import java.io.IOException;

import org.sagebionetworks.bridge.sdk.models.subpopulations.SubpopulationGuid;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class SubpopulationGuidDeserializer extends JsonDeserializer<SubpopulationGuid> {

    @Override
    public SubpopulationGuid deserialize(JsonParser jp, DeserializationContext context)
            throws IOException, JsonProcessingException {
        String guid = jp.getText();
        return new SubpopulationGuid(guid);
    }

}
