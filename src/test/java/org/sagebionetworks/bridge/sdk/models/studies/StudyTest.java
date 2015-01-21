package org.sagebionetworks.bridge.sdk.models.studies;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.common.collect.Lists;

public class StudyTest {
    
    @Test
    public void hashCodeEquals() {
        Study study1 = createStudy();
        Study study2 = createStudy();
        assertEquals(study1.hashCode(), study2.hashCode());
        assertTrue(study1.equals(study2));
    }
    
    @Test
    public void hashCodeEqualsUnequal() {
        Study study1 = createStudy();
        Study study2 = createStudy();
        study2.setMaxNumOfParticipants(101);
        
        assertNotEquals(study1.hashCode(), study2.hashCode());
        assertFalse(study1.equals(study2));
    }

    private Study createStudy() {
        Study study = new Study();
        study.setHostname("localhost:9000");
        study.setIdentifier("AAA");
        study.setMaxNumOfParticipants(100);
        study.setMinAgeOfConsent(18);
        study.setName("Test Name");
        study.setResearcherRole("role_researcher");
        study.setTrackers(Lists.newArrayList("AAAtracker", "BBBtracker"));
        study.setVersion(3L);
        return study;
    }
    
}
