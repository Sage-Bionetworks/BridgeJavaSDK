package org.sagebionetworks.bridge.sdk.models.users;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class DataGroupsTest {
    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(DataGroups.class).suppress(Warning.NONFINAL_FIELDS).allFieldsShouldBeUsed().verify();
    }

}
