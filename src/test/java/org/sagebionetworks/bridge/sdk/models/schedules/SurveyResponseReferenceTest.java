package org.sagebionetworks.bridge.sdk.models.schedules;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import org.junit.Test;

public class SurveyResponseReferenceTest {

    @Test
    public void equalsHashCode() {
        // Do not want to compare for href, which is computed by the server 1:1 based on the other fields
        EqualsVerifier.forClass(SurveyResponseReference.class).suppress(Warning.NONFINAL_FIELDS).verify();
    }
}
