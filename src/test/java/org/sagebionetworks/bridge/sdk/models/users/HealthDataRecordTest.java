package org.sagebionetworks.bridge.sdk.models.users;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class HealthDataRecordTest {
    
    @Test
    public void equalsContract() {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        ObjectNode node2 = JsonNodeFactory.instance.objectNode();
        node2.put("test", "testValue");
        EqualsVerifier.forClass(HealthDataRecord.class).suppress(Warning.NONFINAL_FIELDS).withPrefabValues(JsonNode.class, node, node2).allFieldsShouldBeUsed().verify();
    }
    
}
