package org.sagebionetworks.bridge.sdk.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.sdk.ResearcherClient;
import org.sagebionetworks.bridge.sdk.Roles;
import org.sagebionetworks.bridge.sdk.TestUserHelper;
import org.sagebionetworks.bridge.sdk.TestUserHelper.TestUser;
import org.sagebionetworks.bridge.sdk.exceptions.BridgeServerException;
import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.studies.StudyConsent;

public class StudyConsentTest {

    private TestUser researcher;

    @Before
    public void before() {
        researcher = TestUserHelper.createAndSignInUser(StudyConsentTest.class, true, Roles.RESEARCHER);
    }

    @After
    public void after() {
        researcher.signOutAndDeleteUser();
    }

    @Test(expected=BridgeServerException.class)
    public void mustBeResearcherToAdd() {
        TestUser user = TestUserHelper.createAndSignInUser(StudyConsentTest.class, true);
        try {
            StudyConsent consent = new StudyConsent();
            consent.setDocumentContent("<p>Test content.</p>");

            user.getSession().getResearcherClient().createStudyConsent(consent);

        } finally {
            user.signOutAndDeleteUser();
        }
    }

    @Test
    public void addAndActivateConsent() {
        ResearcherClient client = researcher.getSession().getResearcherClient();

        StudyConsent consent = new StudyConsent();
        consent.setDocumentContent("<p>Test content</p>");
        client.createStudyConsent(consent);

        ResourceList<StudyConsent> studyConsents = client.getAllStudyConsents();

        assertNotNull("studyConsents should not be null.", studyConsents);
        assertTrue("studyConsents should have at least one StudyConsent", studyConsents.getTotal() > 0);
        // And btw these should match
        assertEquals("items.size() == total", studyConsents.getTotal(), studyConsents.getItems().size());

        StudyConsent current = client.getStudyConsent(studyConsents.getItems().get(0).getCreatedOn());
        assertNotNull("studyConsent should not be null.", current);
        
        assertEquals(consent.getDocumentContent(), current.getDocumentContent());
        assertNotNull(current.getCreatedOn());

        client.activateStudyConsent(current.getCreatedOn());

        StudyConsent active = client.getActiveStudyConsent();
        assertTrue("Active consent is returned.", active.isActive());
        
        client.createStudyConsent(current);
        
        StudyConsent newOne = client.getMostRecentStudyConsent();
        assertTrue(newOne.getCreatedOn().isAfter(active.getCreatedOn()));
        
        ResourceList<StudyConsent> studyConsents2 = client.getAllStudyConsents();
        assertEquals(studyConsents.getTotal()+1, studyConsents2.getTotal());
    }

}
