package org.sagebionetworks.bridge.sdk.models.surveys;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class ConstraintsDeserializer extends JsonDeserializer<Constraints> {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    
    @Override
    public Constraints deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
        
        Class<? extends Constraints> clazz = null;
        JsonNode node = parser.getCodec().readTree(parser);
        
        ArrayNode array = (ArrayNode)node.get("enumeration");
        if (array != null && array.size() > 0) {
            clazz = MultiValueConstraints.class;
        } else {
            String dataType = node.get("dataType").asText();
            switch(dataType) {
            case "boolean":
                clazz = BooleanConstraints.class; break;
            case "integer":
                clazz = IntegerConstraints.class; break;
            case "decimal":
                clazz = DecimalConstraints.class; break;
            case "string":
                clazz = StringConstraints.class; break;
            case "datetime":
                clazz = DateTimeConstraints.class; break;
            case "date":
                clazz = DateConstraints.class; break;
            case "time":
                clazz = TimeConstraints.class; break;
            case "duration":
                clazz = DurationConstraints.class; break;
            }
        }
        return MAPPER.treeToValue(node, clazz);
    }

}
