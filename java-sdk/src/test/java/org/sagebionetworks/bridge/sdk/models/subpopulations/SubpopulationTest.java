package org.sagebionetworks.bridge.sdk.models.subpopulations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import org.sagebionetworks.bridge.Tests;
import org.sagebionetworks.bridge.sdk.models.Criteria;
import org.sagebionetworks.bridge.sdk.models.studies.OperatingSystem;
import org.sagebionetworks.bridge.sdk.models.studies.Study;
import org.sagebionetworks.bridge.sdk.utils.Utilities;

import com.google.common.collect.Sets;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class SubpopulationTest {

    private static final DateTime TIMESTAMP = DateTime.now().withZone(DateTimeZone.UTC);
    
    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(Study.class).suppress(Warning.NONFINAL_FIELDS).allFieldsShouldBeUsed().verify();
    }
    
    @Test
    public void canSerialize() throws Exception { 
        Criteria criteria = new Criteria();
        criteria.getMinAppVersions().put(OperatingSystem.IOS, 2);
        criteria.getMaxAppVersions().put(OperatingSystem.IOS, 10);
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
        
        String jsonWithPublicationTimestamp = Tests.unescapeJson("{'publishedConsentCreatedOn':'"+TIMESTAMP.toString()+"', "+
                "'name':'Name',"+
                "'description':'Description',"+
                "'guid':'guid',"+
                "'required':true,"+
                "'defaultGroup':true,"+
                "'version':2,"+
                "'criteria':{'minAppVersion':2,"+
                    "'maxAppVersion':10,"+
                    "'allOfGroups':[]"+
                    ",'noneOfGroups':['group2']}}");
        
        Subpopulation subpopulation = Utilities.getMapper().readValue(jsonWithPublicationTimestamp, Subpopulation.class);
        assertEquals(TIMESTAMP, subpopulation.getPublishedConsentCreatedOn());
        assertEquals(new SubpopulationGuid("guid"), subpopulation.getGuid());
        assertEquals("Name", subpop.getName());
        assertEquals("Description", subpop.getDescription());
        assertTrue(subpop.isRequired());
        assertTrue(subpop.isDefaultGroup());
        assertEquals(new Long(2L), subpop.getVersion());
        assertEquals(criteria, subpop.getCriteria());
    }
    
}
