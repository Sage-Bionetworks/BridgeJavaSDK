package org.sagebionetworks.bridge.sdk.models.holders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SimpleVersionHolderTest {
    @Test
    public void hashCodeEquals() {
        VersionHolder holder = new SimpleVersionHolder(1L);
        VersionHolder holder2 = new SimpleVersionHolder(1L);
        
        assertEquals(holder.hashCode(), holder2.hashCode());
        assertTrue(holder.equals(holder2));
    }

    @Test
    public void hashCodeEqualsUnequal() {
        VersionHolder holder = new SimpleVersionHolder(1L);
        VersionHolder holder2 = new SimpleVersionHolder(2L);
        assertNotEquals(holder.hashCode(), holder2.hashCode());
        assertFalse(holder.equals(holder2));
    }

}
