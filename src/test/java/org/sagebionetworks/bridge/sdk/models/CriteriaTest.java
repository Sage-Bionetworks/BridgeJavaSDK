package org.sagebionetworks.bridge.sdk.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashSet;

import org.junit.Test;

import org.sagebionetworks.bridge.Tests;
import org.sagebionetworks.bridge.sdk.utils.Utilities;

import com.fasterxml.jackson.databind.JsonNode;
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
        Criteria criteria = new Criteria();
        criteria.setMinAppVersion(2);
        criteria.setMaxAppVersion(8);
        criteria.setAllOfGroups(SET_A);
        criteria.setNoneOfGroups(SET_B);
        
        JsonNode node = Utilities.getMapper().valueToTree(criteria);
        assertEquals(2, node.get("minAppVersion").asInt());
        assertEquals(8, node.get("maxAppVersion").asInt());
        assertEquals(SET_A, Tests.asStringSet(node, "allOfGroups"));
        assertEquals(SET_B, Tests.asStringSet(node, "noneOfGroups"));
        assertNull(node.get("key"));
        
        String json = makeJson("{'minAppVersion':2,'maxAppVersion':8,'allOfGroups':['a','b'],'noneOfGroups':['c','d']}");
        
        Criteria crit = Utilities.getMapper().readValue(json, Criteria.class);
        assertEquals(new Integer(2), crit.getMinAppVersion());
        assertEquals(new Integer(8), crit.getMaxAppVersion());
        assertEquals(SET_A, crit.getAllOfGroups());
        assertEquals(SET_B, crit.getNoneOfGroups());
    }
    
    private String makeJson(String string) {
        return string.replaceAll("'", "\"");
    }

}
