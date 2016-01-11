package org.sagebionetworks.bridge.sdk.models.schedules;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class ScheduleCriteriaTest {
    @Test
    public void equalsHashCode() {
        EqualsVerifier.forClass(ScheduleCriteria.class).allFieldsShouldBeUsed().verify();
    }
    @Test
    public void setsAreNeverNull() {
        ScheduleCriteria criteria = new ScheduleCriteria.Builder().withMaxAppVersion(2).withMaxAppVersion(10).build();
        
        assertTrue(criteria.getAllOfGroups().isEmpty());
        assertTrue(criteria.getNoneOfGroups().isEmpty());
    }
}
