package org.sagebionetworks.bridge.sdk.models.surveys;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class SurveyInfoScreenTest {
    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(SurveyInfoScreen.class).suppress(Warning.NONFINAL_FIELDS).allFieldsShouldBeUsed().verify();
    }

}
