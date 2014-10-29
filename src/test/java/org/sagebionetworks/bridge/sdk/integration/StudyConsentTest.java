package org.sagebionetworks.bridge.sdk.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sagebionetworks.bridge.sdk.AdminClient;
import org.sagebionetworks.bridge.sdk.ClientProvider;
import org.sagebionetworks.bridge.sdk.Config;
import org.sagebionetworks.bridge.sdk.Session;
import org.sagebionetworks.bridge.sdk.models.StudyConsent;

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
    @Ignore
    public void test() {
        StudyConsent current = StudyConsent.valueOf("/path/to", 18);
        admin.addConsentDocument(current); // if fail, will throw exception here.

        List<StudyConsent> studyConsents = admin.getAllConsentDocuments();
        assertNotNull("studyConsents should not be null.", studyConsents);
        assertTrue("studyConsents should have at least one StudyConsent", studyConsents.size() > 0);

        current = admin.getConsentDocument(studyConsents.get(0).getCreatedOn());
        assertNotNull("studyConsent should not be null.", current);
        assertEquals("Retrieved study consent should equal one asked for.", current, studyConsents.get(0));

        admin.activateConsentDocument(current.getCreatedOn());

        assertFalse("Active consent is returned.",
                current.isActive() == admin.getMostRecentlyActivatedConsentDocument().isActive());
    }
}
