package org.sagebionetworks.bridge.sdk.models.schedules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import org.sagebionetworks.bridge.sdk.utils.Utilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.Sets;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class CriteriaScheduleStrategyTest {
    
    private static final ObjectMapper MAPPER = Utilities.getMapper();

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(CriteriaScheduleStrategy.class).suppress(Warning.NONFINAL_FIELDS).allFieldsShouldBeUsed().verify();
    }
    
    @Test
    public void canSerialize() throws Exception {
        Schedule schedule = new Schedule();
        schedule.setLabel("Test label");
        schedule.setScheduleType(ScheduleType.ONCE);
        schedule.addActivity(new Activity("Label", null, new TaskReference("task:CCC")));
        
        CriteriaScheduleStrategy strategy = new CriteriaScheduleStrategy();
        strategy.addCriteria(new ScheduleCriteria.Builder()
                .withSchedule(schedule)
                .withMinAppVersion(4)
                .withMaxAppVersion(12).build());
        strategy.addCriteria(new ScheduleCriteria.Builder()
                .addRequiredGroup("req1", "req2")
                .addProhibitedGroup("proh1","proh2")
                .withSchedule(schedule).build());
        
        String json = MAPPER.writeValueAsString(strategy);
        JsonNode node = MAPPER.readTree(json);

        assertEquals("CriteriaScheduleStrategy", node.get("type").asText());
        assertNotNull(node.get("scheduleCriteria"));
        
        ArrayNode array = (ArrayNode)node.get("scheduleCriteria");
        JsonNode child1 = array.get(0);
        System.out.println(child1.toString());
        assertEquals(4, child1.get("minAppVersion").asInt());
        assertEquals(12, child1.get("maxAppVersion").asInt());
        assertNotNull(child1.get("schedule"));
        
        JsonNode child2 = array.get(1);
        Set<String> allOfGroups = arrayToSet(child2.get("allOfGroups"));
        assertTrue(allOfGroups.contains("req1"));
        assertTrue(allOfGroups.contains("req2"));
        Set<String> noneOfGroups = arrayToSet(child2.get("noneOfGroups"));
        assertTrue(noneOfGroups.contains("proh1"));
        assertTrue(noneOfGroups.contains("proh2"));
        
        // But mostly, if this isn't all serialized, and then deserialized, these won't be equal
        CriteriaScheduleStrategy newStrategy = MAPPER.readValue(json, CriteriaScheduleStrategy.class);
        assertEquals(strategy, newStrategy);
    }
    
    private Set<String> arrayToSet(JsonNode array) {
        Set<String> set = Sets.newHashSet();
        for (int i=0; i < array.size(); i++) {
            set.add(array.get(i).asText());
        }
        return set;
    }
}
