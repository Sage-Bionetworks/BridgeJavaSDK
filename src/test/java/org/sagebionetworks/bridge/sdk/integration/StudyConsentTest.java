package org.sagebionetworks.bridge.sdk.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.sdk.AdminClient;
import org.sagebionetworks.bridge.sdk.ClientProvider;
import org.sagebionetworks.bridge.sdk.Config;
import org.sagebionetworks.bridge.sdk.Session;
import org.sagebionetworks.bridge.sdk.models.ConsentDocument;

public class StudyConsentTest {

    private Session session;
    private AdminClient admin;

    @Before
    public void before() {
        Config config = ClientProvider.getConfig();
        session = ClientProvider.signIn(config.getAdminCredentials());
        admin = session.getAdminClient();
    }

    @After
    public void after() {
        session.signOut();
    }

    @Test
    public void test() {
        ConsentDocument consent = ConsentDocument.valueOf("/path/to", 22);
        admin.addConsentDocument(consent);
        
        List<ConsentDocument> studyConsents = admin.getAllConsentDocuments();
        assertNotNull("studyConsents should not be null.", studyConsents);
        assertTrue("studyConsents should have at least one StudyConsent", studyConsents.size() > 0);

        ConsentDocument current = admin.getConsentDocument(studyConsents.get(0).getCreatedOn());
        assertNotNull("studyConsent should not be null.", current);
        assertEquals("Retrieved study consent should equal one asked for.", current, studyConsents.get(0));

        admin.activateConsentDocument(current.getCreatedOn());

        assertFalse("Active consent is returned.", current.isActive() == admin
                .getMostRecentlyActivatedConsentDocument().isActive());
    }
    
}
