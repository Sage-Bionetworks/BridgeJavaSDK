package org.sagebionetworks.bridge.sdk.models.subpopulations;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.sagebionetworks.bridge.sdk.models.Criteria;
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
        Criteria criteria = new Criteria();
        criteria.setMinAppVersion(2);
        criteria.setMaxAppVersion(10);
        criteria.setNoneOfGroups(Sets.newHashSet("group2"));
        
        Subpopulation subpop = new Subpopulation();
        subpop.setGuid(new SubpopulationGuid("guid"));
        subpop.setName("Name");
        subpop.setDescription("Description");
        subpop.setRequired(true);
        subpop.setDefaultGroup(true);
        subpop.setVersion(2L);
        subpop.setCriteria(criteria);
        
        String json = Utilities.getMapper().writeValueAsString(subpop);
        Subpopulation subpop2 = Utilities.getMapper().readValue(json, Subpopulation.class);
        assertEquals(subpop, subpop2);
    }
    
}
