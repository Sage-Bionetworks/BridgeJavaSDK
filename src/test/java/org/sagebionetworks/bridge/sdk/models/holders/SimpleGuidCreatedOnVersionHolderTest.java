package org.sagebionetworks.bridge.sdk.models.holders;

import static org.junit.Assert.*;

import org.joda.time.DateTime;
import org.junit.Test;
import org.sagebionetworks.bridge.sdk.models.holders.GuidCreatedOnVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.SimpleGuidCreatedOnVersionHolder;

public class SimpleGuidCreatedOnVersionHolderTest {

    @Test
    public void hashCodeEquals() {
        DateTime time = DateTime.now();
        GuidCreatedOnVersionHolder holder = new SimpleGuidCreatedOnVersionHolder("aaa", time, 1L);
        
        GuidCreatedOnVersionHolder holder2 = new SimpleGuidCreatedOnVersionHolder("aaa", time, 1L);
        
        assertEquals(holder.hashCode(), holder2.hashCode());
        assertTrue(holder.equals(holder2));
    }

    @Test
    public void hashCodeEqualsUnequal() {
        DateTime time = DateTime.now();
        GuidCreatedOnVersionHolder holder = new SimpleGuidCreatedOnVersionHolder("aaa", time, 1L);
        GuidCreatedOnVersionHolder holder2 = new SimpleGuidCreatedOnVersionHolder("aaa", time, 2L);
        assertNotEquals(holder.hashCode(), holder2.hashCode());
        assertFalse(holder.equals(holder2));
        
        holder = new SimpleGuidCreatedOnVersionHolder("aaa", time, 1L);
        holder2 = new SimpleGuidCreatedOnVersionHolder("aaa", DateTime.now(), 1L);
        assertNotEquals(holder.hashCode(), holder2.hashCode());
        assertFalse(holder.equals(holder2));
        
        holder = new SimpleGuidCreatedOnVersionHolder("aaa", time, 1L);
        holder2 = new SimpleGuidCreatedOnVersionHolder("aab", time, 1L);
        assertNotEquals(holder.hashCode(), holder2.hashCode());
        assertFalse(holder.equals(holder2));
    }
}
