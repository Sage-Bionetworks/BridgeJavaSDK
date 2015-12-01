package org.sagebionetworks.bridge.sdk.json;

import java.io.IOException;
import java.util.Set;

import org.sagebionetworks.bridge.sdk.models.users.DataGroups;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.Sets;

@SuppressWarnings("serial")
public class DataGroupsDeserializer extends StdScalarDeserializer<DataGroups> {

    protected DataGroupsDeserializer() {
        super(DataGroups.class);
    }
    
    @Override
    public DataGroups deserialize(JsonParser parser, DeserializationContext context) 
            throws IOException, JsonProcessingException {
        
        ArrayNode node = (ArrayNode)parser.getCodec().readTree(parser);
        Set<String> set = Sets.newHashSet();
        for (int i=0; i < node.size(); i++) {
            set.add(node.get(i).asText());
        }
        DataGroups dataGroups = new DataGroups();
        dataGroups.setDataGroups(set);
        return dataGroups;
    }

}
