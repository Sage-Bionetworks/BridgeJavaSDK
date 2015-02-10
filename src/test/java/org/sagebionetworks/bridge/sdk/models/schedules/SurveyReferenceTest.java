package org.sagebionetworks.bridge.sdk.models.schedules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;

public class SurveyReferenceTest {

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(SurveyReference.class).suppress(Warning.NONFINAL_FIELDS).allFieldsShouldBeUsed().verify();
    }
    
    @Test
    public void refConstructor() {
        SurveyReference ref = new SurveyReference("/foo/surveys/abc/dateTime");
        
        assertEquals("abc", ref.getGuid());
        assertEquals("dateTime", ref.getCreatedOn());
        
        ref = new SurveyReference("this is just a junk string");
        assertNull(ref.getGuid());
        assertNull(ref.getCreatedOn());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void failsWithBlankRef() {
        new SurveyReference(" ");
    }

    @Test
    public void guidCreatedConstructor() {
        DateTime now = DateTime.now();
        String dateString = now.toString(ISODateTimeFormat.dateTime());
        
        SurveyReference ref = new SurveyReference("abc", now);
        
        assertEquals("abc", ref.getGuid());
        assertEquals(dateString, ref.getCreatedOn());
        
        ref = new SurveyReference("abc", null);
        assertEquals("abc", ref.getGuid());
        assertNull(ref.getCreatedOn());
    }
    
    @Test(expected = IllegalArgumentException.class) 
    public void failsWithNullArgument() {
        new SurveyReference(null, null);
    }
    
    @Test(expected = IllegalArgumentException.class) 
    public void failsWithBlankArgument() {
        new SurveyReference("  ", null);
    }
}
