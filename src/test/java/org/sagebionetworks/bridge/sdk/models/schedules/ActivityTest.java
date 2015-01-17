package org.sagebionetworks.bridge.sdk.models.schedules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ActivityTest {
    
    @Test
    public void hashCodeEquals() {
        Activity act1 = new Activity("Label", "ref://");
        Activity act2 = new Activity("Label", "ref://");
        assertEquals(act1.hashCode(), act2.hashCode());
        assertTrue(act1.equals(act2));
    }
    
    @Test
    public void hashCodeEqualsUnequal() {
        Activity act1 = new Activity("Label", "ref://asdf");
        Activity act2 = new Activity("Label", "ref://");
        
        assertNotEquals(act1.hashCode(), act2.hashCode());
        assertFalse(act1.equals(act2));
    }

}
