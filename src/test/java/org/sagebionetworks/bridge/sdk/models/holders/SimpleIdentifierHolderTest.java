package org.sagebionetworks.bridge.sdk.models.holders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SimpleIdentifierHolderTest {
    @Test
    public void hashCodeEquals() {
        IdentifierHolder holder = new SimpleIdentifierHolder("aaa");
        IdentifierHolder holder2 = new SimpleIdentifierHolder("aaa");
        
        assertEquals(holder.hashCode(), holder2.hashCode());
        assertTrue(holder.equals(holder2));
    }

    @Test
    public void hashCodeEqualsUnequal() {
        IdentifierHolder holder = new SimpleIdentifierHolder("aaa");
        IdentifierHolder holder2 = new SimpleIdentifierHolder("aab");
        assertNotEquals(holder.hashCode(), holder2.hashCode());
        assertFalse(holder.equals(holder2));
    }

}
