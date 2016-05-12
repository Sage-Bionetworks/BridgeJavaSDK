package org.sagebionetworks.bridge.sdk.json;

import java.io.IOException;

import org.sagebionetworks.bridge.sdk.models.subpopulations.SubpopulationGuid;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;

public class SubpopulationGuidKeyDeserializer extends KeyDeserializer {

    @Override
    public Object deserializeKey(String string, DeserializationContext context)
            throws IOException, JsonProcessingException {
        return new SubpopulationGuid(string);
    }

}

