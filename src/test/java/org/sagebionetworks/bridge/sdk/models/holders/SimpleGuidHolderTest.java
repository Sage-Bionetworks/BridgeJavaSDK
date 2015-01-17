package org.sagebionetworks.bridge.sdk.models.holders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SimpleGuidHolderTest {
    @Test
    public void hashCodeEquals() {
        GuidHolder holder = new SimpleGuidHolder("aaa");
        GuidHolder holder2 = new SimpleGuidHolder("aaa");
        
        assertEquals(holder.hashCode(), holder2.hashCode());
        assertTrue(holder.equals(holder2));
    }

    @Test
    public void hashCodeEqualsUnequal() {
        GuidHolder holder = new SimpleGuidHolder("aaa");
        GuidHolder holder2 = new SimpleGuidHolder("aab");
        assertNotEquals(holder.hashCode(), holder2.hashCode());
        assertFalse(holder.equals(holder2));
    }

}
