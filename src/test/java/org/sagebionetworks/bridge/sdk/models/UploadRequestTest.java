package org.sagebionetworks.bridge.sdk.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class UploadRequestTest {
    @Test
    public void hashCodeEquals() {
        UploadRequest c1 = createUploadRequest();
        UploadRequest c2 = createUploadRequest();
        
        assertEquals(c1.hashCode(), c2.hashCode());
        assertTrue(c1.equals(c2));
    }
    
    @Test
    public void hashCodeEqualsUnequal() {
        UploadRequest c1 = createUploadRequest();
        UploadRequest c2 = createUploadRequest();
        c2.setContentLength(10);
        
        assertNotEquals(c1.hashCode(), c2.hashCode());
        assertFalse(c1.equals(c2));
    }
    
    private UploadRequest createUploadRequest() {
        return new UploadRequest();
    }

}
