package org.sagebionetworks.bridge.sdk.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.sagebionetworks.bridge.Tests;
import org.sagebionetworks.bridge.sdk.AdminClient;
import org.sagebionetworks.bridge.sdk.ResearcherClient;
import org.sagebionetworks.bridge.sdk.TestUserHelper;
import org.sagebionetworks.bridge.sdk.TestUserHelper.TestUser;
import org.sagebionetworks.bridge.sdk.exceptions.EntityNotFoundException;
import org.sagebionetworks.bridge.sdk.exceptions.UnauthorizedException;
import org.sagebionetworks.bridge.sdk.models.holders.VersionHolder;
import org.sagebionetworks.bridge.sdk.models.studies.Study;

import com.google.common.collect.Lists;

public class StudyTest {

    private static final String BRIDGE_TESTING_CONSENT_EMAIL = "bridge-testing+consent@sagebridge.org";
    
    private static TestUser admin;
    private static TestUser researcher;
    private Study study;

    @BeforeClass
    public static void beforeClass() {
        admin = TestUserHelper.getSignedInAdmin();
        researcher = TestUserHelper.createAndSignInUser(StudyTest.class, false, Tests.TEST_KEY+"_researcher");
    }
    
    @AfterClass
    public static void afterClass() {
        admin.getSession().signOut();
        researcher.signOutAndDeleteUser();
    }
    
    @After
    public void after() {
        if (study != null) {
            admin.getSession().getAdminClient().deleteStudy(study.getIdentifier());
        }
    }

    @Test
    public void crudStudy() throws Exception {
        String identifier = Tests.randomIdentifier();
        study = createStudy(identifier, null);

        AdminClient client = admin.getSession().getAdminClient();
        
        assertNull(study.getVersion());
        VersionHolder holder = client.createStudy(study);
        compareHolderToModelObject(holder, study, null);

        Study newStudy = client.getStudy(study.getIdentifier());
        assertEquals(study, createStudy(identifier, 1L));

        Long oldVersion = study.getVersion();
        alterStudy(study);
        holder = client.updateStudy(study);
        compareHolderToModelObject(holder, study, oldVersion);
        
        newStudy = client.getStudy(study.getIdentifier());
        // These are set on the server, so to compare equality, they must be set.
        study.setResearcherRole(newStudy.getResearcherRole());
        study.setHostname(newStudy.getHostname());
        
        assertEquals(study, newStudy);
        
        client.deleteStudy(identifier);
        try {
            newStudy = client.getStudy(identifier);
            fail("Should have thrown exception");
        } catch(EntityNotFoundException e) {
        }
        study = null;
    }

    @Test
    @Ignore
    public void researcherCannotAccessAnotherStudy() {
        String identifier = Tests.randomIdentifier();
        study = createStudy(identifier, null);

        AdminClient client = admin.getSession().getAdminClient();
        client.createStudy(study);

        try {
            researcher.getSession().getAdminClient().getStudy(identifier);
            fail("Should not have been able to get this other study");
        } catch(UnauthorizedException e) {
            assertEquals("Unauthorized HTTP response code", 403, e.getStatusCode());
        }
    }

    @Test
    @Ignore
    public void researcherCanAccessStudy() {
        ResearcherClient rclient = researcher.getSession().getResearcherClient();
        Study serverStudy = rclient.getStudy();

        assertEquals(Tests.TEST_KEY+"_researcher", serverStudy.getResearcherRole());
    }

    @Test(expected = UnauthorizedException.class)
    @Ignore
    public void butNormalUserCannotAccessStudy() {
        TestUser user = TestUserHelper.createAndSignInUser(StudyTest.class, false);
        try {
            ResearcherClient rclient = user.getSession().getResearcherClient();
            rclient.getStudy();
        } finally {
            user.signOutAndDeleteUser();
        }
    }
    
    @Test
    @Ignore
    public void researcherCanRetrieveConsentedParticipants() {
        // Note that this user has consented, not just signed up.
        TestUser user = TestUserHelper.createAndSignInUser(StudyTest.class, true);
        try {
            ResearcherClient client = researcher.getSession().getResearcherClient();
            
            Study study = client.getStudy();
            if (!BRIDGE_TESTING_CONSENT_EMAIL.equals(study.getConsentNotificationEmail())) {
                study.setConsentNotificationEmail(BRIDGE_TESTING_CONSENT_EMAIL);
                client.updateStudy(study);
            }
            client.sendStudyParticipantsRoster();
            
        } finally {
            user.signOutAndDeleteUser();
        }
    }
    
    @Test(expected = UnauthorizedException.class)
    @Ignore
    public void adminCannotRetrieveParticipants() {
        ResearcherClient client = admin.getSession().getResearcherClient();
        client.sendStudyParticipantsRoster();
    }
    
    private void compareHolderToModelObject(VersionHolder holder, Study study, Long oldVersion) {
        assertNotNull(holder.getVersion());
        assertNotNull(study.getVersion());
        assertEquals(holder.getVersion(), study.getVersion());
        if (oldVersion != null) {
            assertNotEquals(oldVersion, study.getVersion());
        }
    }

    private Study createStudy(String identifier, Long version) {
        Study study = new Study();
        study.setIdentifier(identifier);
        study.setMinAgeOfConsent(18);
        study.setMaxNumOfParticipants(100);
        study.setName("Test Study [SDK]");
        study.setSupportEmail("test@test.com");
        study.setConsentNotificationEmail("test2@test.com");
        study.setTrackers(Lists.newArrayList("sage:A", "sage:B"));
        if (version != null) {
            study.setVersion(version);
        }
        return study;
    }
    
    private void alterStudy(Study study) {
        study.setName("Altered Test Study [SDK]");
        study.setMaxNumOfParticipants(50);
        study.setSupportEmail("test3@test.com");
        study.setConsentNotificationEmail("test4@test.com");
    }

}
