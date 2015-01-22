package org.sagebionetworks.bridge.sdk.models.surveys;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import org.junit.Test;

public class ConstraintsTest {

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(BooleanConstraints.class).suppress(Warning.NONFINAL_FIELDS).allFieldsShouldBeUsed().verify();
        EqualsVerifier.forClass(DateConstraints.class).suppress(Warning.NONFINAL_FIELDS).allFieldsShouldBeUsed().verify();
        EqualsVerifier.forClass(DateTimeConstraints.class).suppress(Warning.NONFINAL_FIELDS).allFieldsShouldBeUsed().verify();
        EqualsVerifier.forClass(DecimalConstraints.class).suppress(Warning.NONFINAL_FIELDS).allFieldsShouldBeUsed().verify();
        EqualsVerifier.forClass(DurationConstraints.class).suppress(Warning.NONFINAL_FIELDS).allFieldsShouldBeUsed().verify();
        EqualsVerifier.forClass(IntegerConstraints.class).suppress(Warning.NONFINAL_FIELDS).allFieldsShouldBeUsed().verify();
        EqualsVerifier.forClass(MultiValueConstraints.class).suppress(Warning.NONFINAL_FIELDS).allFieldsShouldBeUsed().verify();
        EqualsVerifier.forClass(StringConstraints.class).suppress(Warning.NONFINAL_FIELDS).allFieldsShouldBeUsed().verify();
        EqualsVerifier.forClass(TimeConstraints.class).suppress(Warning.NONFINAL_FIELDS).allFieldsShouldBeUsed().verify();
    }

}
