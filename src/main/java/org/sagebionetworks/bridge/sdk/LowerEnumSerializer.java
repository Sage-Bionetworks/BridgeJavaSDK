package org.sagebionetworks.bridge.sdk;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;

@SuppressWarnings("rawtypes")
class LowerEnumSerializer extends StdScalarSerializer<Enum> {

    public LowerEnumSerializer() {
        super(Enum.class, false);
    }

    @Override
    public void serialize(Enum value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
            JsonGenerationException {
        jgen.writeString(value.name().toLowerCase());
    }

}
