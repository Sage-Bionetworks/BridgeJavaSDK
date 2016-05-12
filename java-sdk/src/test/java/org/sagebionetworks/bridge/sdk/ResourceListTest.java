package org.sagebionetworks.bridge.sdk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import org.junit.Ignore;
import org.junit.Test;
import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.accounts.StudyParticipant;

import com.google.common.collect.Lists;

public class ResourceListTest {
    
    @Test
    @Ignore // This actually throws an internal error in EqualsVerifier, we hit a bug
    public void equalsContract() {
        EqualsVerifier.forClass(ResourceList.class).suppress(Warning.NONFINAL_FIELDS).allFieldsShouldBeUsed().verify();
    }
    
    @Test
    public void hashCodeEquals() {
        ResourceList<StudyParticipant> c1 = createParticipantList("test");
        ResourceList<StudyParticipant> c2 = createParticipantList("test");
        
        assertEquals(c1.hashCode(), c2.hashCode());
        assertTrue(c1.equals(c2));
    }
    
    @Test
    public void hashCodeEqualsUnequal() {
        ResourceList<StudyParticipant> c1 = createParticipantList("test");
        ResourceList<StudyParticipant> c2 = createParticipantList("test2");
        
        
        assertNotEquals(c1.hashCode(), c2.hashCode());
        assertFalse(c1.equals(c2));
    }
    
    private ResourceList<StudyParticipant> createParticipantList(String email) {
        StudyParticipant participant = new StudyParticipant.Builder().withEmail(email).build();
        List<StudyParticipant> list = Lists.newArrayList(participant);
        return new ResourceListImpl<StudyParticipant>(list, list.size());
    }

}
