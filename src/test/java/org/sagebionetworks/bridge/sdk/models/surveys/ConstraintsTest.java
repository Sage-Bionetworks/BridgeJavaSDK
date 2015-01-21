package org.sagebionetworks.bridge.sdk.models.surveys;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ConstraintsTest {

    @Test
    public void hashCodeEquals() throws Exception {
        BooleanConstraints c1 = new BooleanConstraints();
        c1.setAllowMultiple(true);
        BooleanConstraints c2 = new BooleanConstraints();
        c2.setAllowMultiple(true);
        
        assertEquals(c1.hashCode(), c2.hashCode());
        assertTrue(c1.equals(c2));
    }
    
    @Test
    public void differentConstraintTypesAreUnequal() {
        BooleanConstraints c1 = new BooleanConstraints();
        IntegerConstraints c2 = new IntegerConstraints();
        
        assertNotEquals(c1.hashCode(), c2.hashCode());
        assertFalse(c1.equals(c2));
    }
    
    @Test
    public void multiValueConstraintsEquality() {
        MultiValueConstraints mc1 = new MultiValueConstraints();
        MultiValueConstraints mc2 = new MultiValueConstraints();
        mc2.setDataType(DataType.INTEGER);
        
        assertNotEquals(mc1.hashCode(), mc2.hashCode());
        assertFalse(mc1.equals(mc2));
    }
    
    @Test
    public void hashCodeEqualsUnequal() throws Exception {
        BooleanConstraints c1 = new BooleanConstraints();
        c1.setAllowMultiple(true);
        BooleanConstraints c2 = new BooleanConstraints();
        c2.setAllowMultiple(false);
        
        assertNotEquals(c1.hashCode(), c2.hashCode());
        assertFalse(c1.equals(c2));
    }
    
}
