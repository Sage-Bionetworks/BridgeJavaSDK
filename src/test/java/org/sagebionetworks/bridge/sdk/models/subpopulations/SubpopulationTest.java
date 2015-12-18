package org.sagebionetworks.bridge.sdk.models.subpopulations;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.sagebionetworks.bridge.sdk.models.studies.Study;
import org.sagebionetworks.bridge.sdk.models.subpopulations.Subpopulation;
import org.sagebionetworks.bridge.sdk.utils.Utilities;

import com.google.common.collect.Sets;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class SubpopulationTest {

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(Study.class).suppress(Warning.NONFINAL_FIELDS).allFieldsShouldBeUsed().verify();
    }
    
    @Test
    public void canSerialize() throws Exception { 
        Subpopulation subpop = new Subpopulation();
        subpop.setGuid(new SubpopulationGuid("guid"));
        subpop.setName("Name");
        subpop.setDescription("Description");
        subpop.setRequired(true);
        subpop.setDefaultGroup(true);
        subpop.setMinAppVersion(2);
        subpop.setMaxAppVersion(10);
        subpop.setVersion(2L);
        subpop.setAllOfGroups(Sets.newHashSet("group1"));
        subpop.setNoneOfGroups(Sets.newHashSet("group2"));
        
        String json = Utilities.getMapper().writeValueAsString(subpop);
        System.out.println(json);
        Subpopulation subpop2 = Utilities.getMapper().readValue(json, Subpopulation.class);
        assertEquals(subpop, subpop2);
    }
    
}
