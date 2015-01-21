package org.sagebionetworks.bridge.sdk.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.joda.time.DateTime;
import org.junit.Test;

public class UploadSessionTest {
    @Test
    public void hashCodeEquals() {
        DateTime date = DateTime.now();
        UploadSession c1 = createUploadSession("id", date);
        UploadSession c2 = createUploadSession("id", date);
        
        assertEquals(c1.hashCode(), c2.hashCode());
        assertTrue(c1.equals(c2));
    }
    
    @Test
    public void hashCodeEqualsUnequal() {
        DateTime date = DateTime.now();
        UploadSession c1 = createUploadSession("id", date);
        UploadSession c2 = createUploadSession("id2", date);
        
        assertNotEquals(c1.hashCode(), c2.hashCode());
        assertFalse(c1.equals(c2));
    }
    
    private UploadSession createUploadSession(String id, DateTime date) {
        return new UploadSession(id, "url", date);
    }

}
