package org.sagebionetworks.bridge.sdk.models.accounts;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.sagebionetworks.bridge.sdk.utils.BridgeUtils;

import nl.jqno.equalsverifier.EqualsVerifier;

public class WithdrawalTest {
    
    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(Withdrawal.class).allFieldsShouldBeUsed().verify();
    }

    @Test
    public void serializesCorrectly() throws Exception {
        Withdrawal withdrawal = new Withdrawal(null);
        String json = BridgeUtils.getMapper().writeValueAsString(withdrawal);
        assertEquals("{}", json);
        
        withdrawal = new Withdrawal("A reason.");
        json = BridgeUtils.getMapper().writeValueAsString(withdrawal);
        assertEquals("{\"reason\":\"A reason.\"}", json);
    }
    
}
