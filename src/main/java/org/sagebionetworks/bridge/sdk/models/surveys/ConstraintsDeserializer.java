package org.sagebionetworks.bridge.sdk.models.surveys;

import java.io.IOException;
import java.util.Map;

import org.sagebionetworks.bridge.sdk.utils.Utilities;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.Maps;

class ConstraintsDeserializer extends JsonDeserializer<Constraints> {

    Map<String,Class<? extends Constraints>> map = Maps.newHashMap();
    
    public ConstraintsDeserializer() {
        map.put("boolean", BooleanConstraints.class);
        map.put("integer", IntegerConstraints.class);
        map.put("decimal", DecimalConstraints.class);
        map.put("string", StringConstraints.class);
        map.put("datetime", DateTimeConstraints.class);
        map.put("date", DateConstraints.class);
        map.put("time", TimeConstraints.class);
        map.put("duration", DurationConstraints.class);
    }
    
    @Override
    public Constraints deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
        
        Class<? extends Constraints> clazz = null;
        JsonNode node = parser.getCodec().readTree(parser);
        
        ArrayNode array = (ArrayNode)node.get("enumeration");
        
        if (array != null && array.size() > 0) {
            clazz = MultiValueConstraints.class;
        } else {
            String dataType = node.get("dataType").asText();
            clazz = map.get(dataType);
            if (clazz == null) {
                throw new RuntimeException("Invalid constraint data type: " + dataType);
            }
        }
        return Utilities.getMapper().treeToValue(node, clazz);
    }

}
