package org.sagebionetworks.bridge.sdk.models.reports;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.sagebionetworks.bridge.Tests;
import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.utils.BridgeUtils;

import com.fasterxml.jackson.core.type.TypeReference;

import nl.jqno.equalsverifier.EqualsVerifier;

public class ReportIndexTest {
    private static final TypeReference<ResourceList<ReportIndex>> REPORT_INDEX_LIST_TYPE = 
            new TypeReference<ResourceList<ReportIndex>>() {};

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(ReportIndex.class).allFieldsShouldBeUsed().verify();
    }
            
    @Test
    public void canDeserialize() throws Exception {
        String json = Tests.unescapeJson("{'items':[{'identifier':'fofo',"+
                "'type':'ReportIndex'}, {'identifier':'bar','type':'ReportIndex'}],"+
                "'total':1,'type':'ResourceList'}");
       
        ResourceList<ReportIndex> results = BridgeUtils.getMapper().readValue(json, REPORT_INDEX_LIST_TYPE);
        assertEquals(2, results.getItems().size());
        assertEquals("fofo", results.getItems().get(0).getIdentifier());
        assertEquals("bar", results.getItems().get(1).getIdentifier());
    }
}
