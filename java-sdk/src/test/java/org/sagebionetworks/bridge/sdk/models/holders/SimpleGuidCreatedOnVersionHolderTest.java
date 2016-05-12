package org.sagebionetworks.bridge.sdk.models.holders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import org.joda.time.DateTime;
import org.junit.Test;

public class SimpleGuidCreatedOnVersionHolderTest {
    @Test
    public void copyConstructor() {
        SimpleGuidCreatedOnVersionHolder original = new SimpleGuidCreatedOnVersionHolder("test-guid",
                DateTime.parse("2015-09-25T17:02-0700"), 42L);
        SimpleGuidCreatedOnVersionHolder copy = new SimpleGuidCreatedOnVersionHolder(original);
        assertEquals(original, copy);
    }

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(SimpleGuidCreatedOnVersionHolder.class).suppress(Warning.NONFINAL_FIELDS).allFieldsShouldBeUsed().verify();
    }

    @Test
    public void testToString() {
        DateTime createdOn = DateTime.parse("2015-09-25T17:02-0700");
        SimpleGuidCreatedOnVersionHolder versionHolder = new SimpleGuidCreatedOnVersionHolder("test-guid", createdOn,
                42L);
        String versionHolderStr = versionHolder.toString();

        // Instead of exact string-matching (which is finnicky), just test that the string includes our expected parts
        assertTrue(versionHolderStr.contains("test-guid"));
        assertTrue(versionHolderStr.contains(createdOn.toString()));
        assertTrue(versionHolderStr.contains("42"));
    }
}
