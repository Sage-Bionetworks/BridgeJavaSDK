package org.sagebionetworks.bridge.sdk.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sagebionetworks.bridge.sdk.ResearcherClient;
import org.sagebionetworks.bridge.sdk.Roles;
import org.sagebionetworks.bridge.sdk.TestUserHelper;
import org.sagebionetworks.bridge.sdk.TestUserHelper.TestUser;
import org.sagebionetworks.bridge.sdk.exceptions.BridgeSDKException;
import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.subpopulations.StudyConsent;

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

    @Test(expected=BridgeSDKException.class)
    public void mustBeResearcherToAdd() {
        TestUser user = TestUserHelper.createAndSignInUser(StudyConsentTest.class, true);
        try {
            StudyConsent consent = new StudyConsent();
            consent.setDocumentContent("<p>Test content.</p>");

            user.getSession().getResearcherClient().createStudyConsent(user.getDefaultSubpopulation(), consent);

        } finally {
            user.signOutAndDeleteUser();
        }
    }

    // This test creates 2 study consents every time it runs. On sign-in, we call getActiveConsents(), which becomes
    // more expensive as we have more study consents. This is causing DDB throttling issues. In the short-term, I have
    // disabled this test. In the long term, we need to add clean-up to this integration test and/or re-think how we do
    // study consents. See https://sagebionetworks.jira.com/browse/BRIDGE-778
    @Test
    @Ignore
    public void addAndActivateConsent() {
        ResearcherClient client = researcher.getSession().getResearcherClient();

        StudyConsent consent = new StudyConsent();
        consent.setDocumentContent("<p>Test content</p>");
        client.createStudyConsent(researcher.getDefaultSubpopulation(), consent);

        ResourceList<StudyConsent> studyConsents = client.getAllStudyConsents(researcher.getDefaultSubpopulation());

        assertNotNull("studyConsents should not be null.", studyConsents);
        assertTrue("studyConsents should have at least one StudyConsent", studyConsents.getTotal() > 0);
        // And btw these should match
        assertEquals("items.size() == total", studyConsents.getTotal(), studyConsents.getItems().size());

        StudyConsent current = client.getStudyConsent(researcher.getDefaultSubpopulation(),
                studyConsents.getItems().get(0).getCreatedOn());
        assertNotNull("studyConsent should not be null.", current);
        
        assertEquals(consent.getDocumentContent(), current.getDocumentContent());
        assertNotNull(current.getCreatedOn());

        client.publishStudyConsent(researcher.getDefaultSubpopulation(), current.getCreatedOn());

        StudyConsent published = client.getPublishedStudyConsent(researcher.getDefaultSubpopulation());
        assertTrue("Published consent is returned.", published.isActive());
        
        client.createStudyConsent(researcher.getDefaultSubpopulation(), current);
        
        StudyConsent newOne = client.getMostRecentStudyConsent(researcher.getDefaultSubpopulation());
        assertTrue(newOne.getCreatedOn().isAfter(published.getCreatedOn()));
        
        ResourceList<StudyConsent> studyConsents2 = client.getAllStudyConsents(researcher.getDefaultSubpopulation());
        assertEquals(studyConsents.getTotal()+1, studyConsents2.getTotal());
    }

}
