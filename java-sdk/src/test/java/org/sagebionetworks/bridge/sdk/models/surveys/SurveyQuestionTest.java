package org.sagebionetworks.bridge.sdk.models.surveys;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class SurveyQuestionTest {
    
    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(SurveyQuestion.class).suppress(Warning.NONFINAL_FIELDS).allFieldsShouldBeUsed().verify();
    }

}
