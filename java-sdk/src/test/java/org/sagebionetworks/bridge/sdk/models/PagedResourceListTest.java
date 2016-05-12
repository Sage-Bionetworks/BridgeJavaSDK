package org.sagebionetworks.bridge.sdk.models;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.sagebionetworks.bridge.Tests;
import org.sagebionetworks.bridge.sdk.models.accounts.AccountStatus;
import org.sagebionetworks.bridge.sdk.models.accounts.AccountSummary;
import org.sagebionetworks.bridge.sdk.utils.Utilities;

import com.fasterxml.jackson.core.type.TypeReference;

public class PagedResourceListTest {
    @Test
    public void canSerialize() throws Exception {
        String json = "{'items':["+
                "{'firstName':'firstName1','lastName':'lastName1','email':'email1@email.com','status':'enabled'},"+
                "{'firstName':'firstName2','lastName':'lastName2','email':'email2@email.com','status':'disabled'}],"+
                "'offsetBy': 10,'offsetKey':'aKey','pageSize': 10,'total': 100,'emailFilter':'foo'}";

        PagedResourceList<AccountSummary> page = Utilities.getMapper()
                .readValue(Tests.unescapeJson(json), new TypeReference<PagedResourceList<AccountSummary>>(){});
        assertEquals(new Integer(10), page.getOffsetBy());
        assertEquals(10, page.getPageSize());
        assertEquals(100, page.getTotal());
        assertEquals("aKey", page.getOffsetKey());
        assertEquals("foo", page.getFilters().get("emailFilter"));
        
        assertEquals(2, page.getItems().size());
        
        AccountSummary summary1 = page.getItems().get(0);
        assertEquals("firstName1", summary1.getFirstName());
        assertEquals("lastName1", summary1.getLastName());
        assertEquals("email1@email.com", summary1.getEmail());
        assertEquals(AccountStatus.ENABLED, summary1.getStatus());
        
        AccountSummary summary2 = page.getItems().get(1);
        assertEquals("firstName2", summary2.getFirstName());
        assertEquals("lastName2", summary2.getLastName());
        assertEquals("email2@email.com", summary2.getEmail());
        assertEquals(AccountStatus.DISABLED, summary2.getStatus());
    }

}
