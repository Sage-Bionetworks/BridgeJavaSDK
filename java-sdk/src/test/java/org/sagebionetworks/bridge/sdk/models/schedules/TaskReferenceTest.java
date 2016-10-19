package org.sagebionetworks.bridge.sdk.models.schedules;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class TaskReferenceTest {
    @Test
    public void equalsHashCode() {
        // Do not want to compare for href, which is computed by the server 1:1 based on the other fields
        EqualsVerifier.forClass(TaskReference.class).suppress(Warning.NONFINAL_FIELDS).verify();
    }

}
