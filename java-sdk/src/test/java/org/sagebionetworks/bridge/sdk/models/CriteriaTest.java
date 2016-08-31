package org.sagebionetworks.bridge.sdk.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashSet;
import java.util.Map;

import org.junit.Test;

import org.sagebionetworks.bridge.Tests;
import org.sagebionetworks.bridge.sdk.models.studies.OperatingSystem;
import org.sagebionetworks.bridge.sdk.utils.Utilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class CriteriaTest {
    private static final HashSet<String> SET_B = Sets.newHashSet("c","d");
    private static final HashSet<String> SET_A = Sets.newHashSet("a","b");
    
    @Test
    public void hashCodeEquals() {
        EqualsVerifier.forClass(Criteria.class).suppress(Warning.NONFINAL_FIELDS)
            .allFieldsShouldBeUsed().verify();
    }
    
    @Test
    public void setsAreNeverNull() {
        Criteria criteria = new Criteria();
        criteria.setAllOfGroups(null);
        
        // Whether after construction, or after being set with a null, these are both empty sets.
        assertNotNull(criteria.getAllOfGroups());
        assertNotNull(criteria.getNoneOfGroups());
    }

    @Test
    public void canSerialize() throws Exception {
        Map<OperatingSystem,Integer> minMap = Maps.newHashMap();
        minMap.put(OperatingSystem.IOS, 2);
        minMap.put(OperatingSystem.ANDROID, 10);
        
        Map<OperatingSystem,Integer> maxMap = Maps.newHashMap();
        maxMap.put(OperatingSystem.IOS, 8);
        
        Criteria criteria = new Criteria();
        criteria.setMinAppVersions(minMap);
        criteria.setMaxAppVersions(maxMap);
        criteria.setAllOfGroups(SET_A);
        criteria.setNoneOfGroups(SET_B);
        
        JsonNode node = Utilities.getMapper().valueToTree(criteria);
        assertEquals(2, node.get("minAppVersions").get("iPhone OS").asInt());
        assertEquals(8, node.get("maxAppVersions").get("iPhone OS").asInt());
        assertEquals(10, node.get("minAppVersions").get("Android").asInt());
        assertNull(node.get("maxAppVersions").get("Android"));
        assertEquals(SET_A, Tests.asStringSet(node, "allOfGroups"));
        assertEquals(SET_B, Tests.asStringSet(node, "noneOfGroups"));
        assertNull(node.get("key"));
        
        String json = makeJson("{'minAppVersions':{'iPhone OS':2,'Android':10},'maxAppVersions':{'iPhone OS':8},'allOfGroups':['a','b'],'noneOfGroups':['c','d']}");
        
        Criteria crit = Utilities.getMapper().readValue(json, Criteria.class);
        assertEquals(new Integer(2), crit.getMinAppVersions().get(OperatingSystem.IOS));
        assertEquals(new Integer(8), crit.getMaxAppVersions().get(OperatingSystem.IOS));
        assertEquals(new Integer(10), crit.getMinAppVersions().get(OperatingSystem.ANDROID));
        assertNull(crit.getMaxAppVersions().get(OperatingSystem.ANDROID));
        assertEquals(SET_A, crit.getAllOfGroups());
        assertEquals(SET_B, crit.getNoneOfGroups());
    }
    
    @Test
    public void cannotNullifyPlatformVersionMaps() { 
        Criteria criteria = new Criteria();
        criteria.setMinAppVersions(null);
        criteria.setMaxAppVersions(null);
        
        assertNotNull(criteria.getMinAppVersions());
        assertNotNull(criteria.getMaxAppVersions());
    }
    
    private String makeJson(String string) {
        return string.replaceAll("'", "\"");
    }

}
