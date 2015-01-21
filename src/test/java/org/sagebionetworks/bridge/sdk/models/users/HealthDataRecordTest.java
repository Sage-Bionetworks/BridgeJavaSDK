package org.sagebionetworks.bridge.sdk.models.users;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.joda.time.DateTime;
import org.junit.Test;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;

public class HealthDataRecordTest {
    @Test
    public void hashCodeEquals() {
        DateTime date = DateTime.now();
        HealthDataRecord r1 = createRecord(date);
        HealthDataRecord r2 = createRecord(date);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertTrue(r1.equals(r2));
    }
    
    @Test
    public void hashCodeEqualsUnequal() {
        DateTime date = DateTime.now();
        HealthDataRecord r1 = createRecord(date);
        HealthDataRecord r2 = createRecord(date);
        r2.setEndDate(DateTime.now());
        
        assertNotEquals(r1.hashCode(), r2.hashCode());
        assertFalse(r1.equals(r2));
    }
    
    private HealthDataRecord createRecord(DateTime date) {
        HealthDataRecord record = new HealthDataRecord();
        record.setStartDate(date);
        record.setData(JsonNodeFactory.instance.objectNode());
        record.setVersion(3L);
        return record;
    }

}
