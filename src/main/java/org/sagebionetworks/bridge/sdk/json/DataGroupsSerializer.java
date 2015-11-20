package org.sagebionetworks.bridge.sdk.json;

import java.io.IOException;

import org.sagebionetworks.bridge.sdk.models.users.DataGroups;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;

@SuppressWarnings("serial")
public class DataGroupsSerializer extends StdScalarSerializer<DataGroups> {

    protected DataGroupsSerializer() {
        super(DataGroups.class);
    }

    @Override
    public void serialize(DataGroups dataGroups, JsonGenerator gen, SerializerProvider provider)
            throws IOException, JsonGenerationException {
        gen.writeStartArray();
        for (String string : dataGroups.getDataGroups()) {
            gen.writeString(string);
        }
        gen.writeEndArray();
    }
    
}
