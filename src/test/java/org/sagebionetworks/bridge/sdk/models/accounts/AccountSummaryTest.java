package org.sagebionetworks.bridge.sdk.models.accounts;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import org.sagebionetworks.bridge.Tests;
import org.sagebionetworks.bridge.sdk.utils.Utilities;

import com.fasterxml.jackson.databind.JsonNode;

import nl.jqno.equalsverifier.EqualsVerifier;

public class AccountSummaryTest {
    @Test
    public void hashCodeEquals() {
        EqualsVerifier.forClass(AccountSummary.class).allFieldsShouldBeUsed().verify();
    }
    
    @Test
    public void canSerialize() throws Exception {
        DateTime createdOn = DateTime.now().withZone(DateTimeZone.UTC);
        AccountSummary summary = new AccountSummary("firstName", "lastName", "email@email.com", createdOn, AccountStatus.UNVERIFIED);
        
        JsonNode node = Utilities.getMapper().valueToTree(summary);
        assertEquals("firstName", node.get("firstName").asText());
        assertEquals("lastName", node.get("lastName").asText());
        assertEquals("email@email.com", node.get("email").asText());
        assertEquals(createdOn.toString(), node.get("createdOn").asText());
        assertEquals("unverified", node.get("status").asText());
        
        AccountSummary newSummary = Utilities.getMapper().treeToValue(node, AccountSummary.class);
        assertEquals(summary, newSummary);
    }
    
    @Test
    public void deserializesCorrectly() throws Exception {
        String json = Tests.unescapeJson("{'firstName':'firstName','lastName':'lastName','email':'email@email.com','createdOn':'2016-04-07T18:47:00.375Z','status':'unverified'}");

        AccountSummary summary = Utilities.getMapper().readValue(json, AccountSummary.class);
        assertEquals("firstName", summary.getFirstName());
        assertEquals("lastName", summary.getLastName());
        assertEquals("email@email.com", summary.getEmail());
        assertEquals(DateTime.parse("2016-04-07T18:47:00.375Z"), summary.getCreatedOn());
        assertEquals(AccountStatus.UNVERIFIED, summary.getStatus());
    }
}
