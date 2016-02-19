package org.sagebionetworks.bridge.sdk.models.schedules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import org.sagebionetworks.bridge.Tests;
import org.sagebionetworks.bridge.sdk.models.Criteria;
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
        
        Criteria criteria1 = new Criteria();
        criteria1.setMinAppVersion(4);
        criteria1.setMaxAppVersion(12);
        
        Criteria criteria2 = new Criteria();
        criteria2.setAllOfGroups(Sets.newHashSet("req1", "req2"));
        criteria2.setNoneOfGroups(Sets.newHashSet("proh1","proh2"));
        
        CriteriaScheduleStrategy strategy = new CriteriaScheduleStrategy();
        strategy.addCriteria(new ScheduleCriteria(schedule, criteria1));
        strategy.addCriteria(new ScheduleCriteria(schedule, criteria2));
        
        String json = MAPPER.writeValueAsString(strategy);
        JsonNode node = MAPPER.readTree(json);

        assertEquals("CriteriaScheduleStrategy", node.get("type").asText());
        assertNotNull(node.get("scheduleCriteria"));
        
        ArrayNode array = (ArrayNode)node.get("scheduleCriteria");
        JsonNode child1 = array.get(0);
        JsonNode crit1 = child1.get("criteria");
        assertEquals(4, crit1.get("minAppVersion").asInt());
        assertEquals(12, crit1.get("maxAppVersion").asInt());
        assertNotNull(child1.get("schedule"));
        
        JsonNode child2 = array.get(1);
        JsonNode crit2 = child2.get("criteria");
        
        Set<String> allOfGroups = Tests.asStringSet(crit2,  "allOfGroups");
        assertTrue(allOfGroups.contains("req1"));
        assertTrue(allOfGroups.contains("req2"));
        Set<String> noneOfGroups = Tests.asStringSet(crit2,  "noneOfGroups");
        assertTrue(noneOfGroups.contains("proh1"));
        assertTrue(noneOfGroups.contains("proh2"));
        
        // But mostly, if this isn't all serialized, and then deserialized, these won't be equal
        CriteriaScheduleStrategy newStrategy = MAPPER.readValue(json, CriteriaScheduleStrategy.class);
        assertEquals(strategy, newStrategy);
    }
    
}
