package org.sagebionetworks.bridge.sdk.models.holders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SimpleGuidVersionHolderTest {
    @Test
    public void hashCodeEquals() {
        GuidVersionHolder holder = new SimpleGuidVersionHolder("aaa", 1L);
        GuidVersionHolder holder2 = new SimpleGuidVersionHolder("aaa", 1L);
        
        assertEquals(holder.hashCode(), holder2.hashCode());
        assertTrue(holder.equals(holder2));
    }

    @Test
    public void hashCodeEqualsUnequal() {
        GuidVersionHolder holder = new SimpleGuidVersionHolder("aaa", 1L);
        GuidVersionHolder holder2 = new SimpleGuidVersionHolder("aaa", 2L);
        assertNotEquals(holder.hashCode(), holder2.hashCode());
        assertFalse(holder.equals(holder2));
        
        holder = new SimpleGuidVersionHolder("aaa", 1L);
        holder2 = new SimpleGuidVersionHolder("aab", 1L);
        assertNotEquals(holder.hashCode(), holder2.hashCode());
        assertFalse(holder.equals(holder2));
    }

}
