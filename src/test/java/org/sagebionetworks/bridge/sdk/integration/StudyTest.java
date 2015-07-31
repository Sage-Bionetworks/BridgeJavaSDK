package org.sagebionetworks.bridge.sdk.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.Tests;
import org.sagebionetworks.bridge.sdk.AdminClient;
import org.sagebionetworks.bridge.sdk.DeveloperClient;
import org.sagebionetworks.bridge.sdk.ResearcherClient;
import org.sagebionetworks.bridge.sdk.Roles;
import org.sagebionetworks.bridge.sdk.TestUserHelper;
import org.sagebionetworks.bridge.sdk.TestUserHelper.TestUser;
import org.sagebionetworks.bridge.sdk.exceptions.EntityNotFoundException;
import org.sagebionetworks.bridge.sdk.exceptions.UnauthorizedException;
import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.holders.VersionHolder;
import org.sagebionetworks.bridge.sdk.models.studies.Study;

public class StudyTest {
    
    private static TestUser admin;
    private static TestUser researcher;
    private Study study;

    @Before
    public void before() {
        admin = TestUserHelper.getSignedInAdmin();
        researcher = TestUserHelper.createAndSignInUser(StudyTest.class, false, Roles.RESEARCHER);
    }
    
    @After
    public void after() {
        if (study != null) {
            admin.getSession().getAdminClient().deleteStudy(study.getIdentifier());
        }
        researcher.signOutAndDeleteUser();
        admin.getSession().signOut();
    }

    @Test
    public void crudStudy() throws Exception {
        AdminClient client = admin.getSession().getAdminClient();
        
        String identifier = Tests.randomIdentifier();
        study = getStudyObject(identifier, null);
        assertNull(study.getVersion());
        
        VersionHolder holder = client.createStudy(study);
        assertVersionHasUpdated(holder, study, null);

        Study newStudy = client.getStudy(study.getIdentifier());
        // Verify study has been set with default password/email templates
        assertNotNull(newStudy.getPasswordPolicy());
        assertNotNull(newStudy.getVerifyEmailTemplate());
        assertNotNull(newStudy.getResetPasswordTemplate());
        assertEquals(study.getName(), newStudy.getName());
        assertEquals(study.getMinAgeOfConsent(), newStudy.getMinAgeOfConsent());
        assertEquals(study.getMaxNumOfParticipants(), newStudy.getMaxNumOfParticipants());
        assertEquals(study.getSponsorName(), newStudy.getSponsorName());
        assertEquals(study.getSupportEmail(), newStudy.getSupportEmail());
        assertEquals(study.getTechnicalEmail(), newStudy.getTechnicalEmail());
        assertEquals(study.getConsentNotificationEmail(), newStudy.getConsentNotificationEmail());
        
        Long oldVersion = study.getVersion();
        alterStudy(study);
        holder = client.updateStudy(study);
        assertVersionHasUpdated(holder, study, oldVersion);
        
        Study newerStudy = client.getStudy(study.getIdentifier());
        assertEquals("Altered Test Study [SDK]", newerStudy.getName());
        assertEquals(50, newerStudy.getMaxNumOfParticipants());
        assertEquals("test3@test.com", newerStudy.getSupportEmail());
        assertEquals("test4@test.com", newerStudy.getConsentNotificationEmail());
        
        client.deleteStudy(identifier);
        try {
            newStudy = client.getStudy(identifier);
            fail("Should have thrown exception");
        } catch(EntityNotFoundException e) {
        }
        study = null;
    }

    @Test
    public void researcherCannotAccessAnotherStudy() {
        String identifier = Tests.randomIdentifier();
        study = getStudyObject(identifier, null);

        AdminClient client = admin.getSession().getAdminClient();
        client.createStudy(study);

        try {
            researcher.getSession().getAdminClient().getStudy(identifier);
            fail("Should not have been able to get this other study");
        } catch(UnauthorizedException e) {
            assertEquals("Unauthorized HTTP response code", 403, e.getStatusCode());
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void butNormalUserCannotAccessStudy() {
        TestUser user = TestUserHelper.createAndSignInUser(StudyTest.class, false);
        try {
            DeveloperClient rclient = user.getSession().getDeveloperClient();
            rclient.getStudy();
        } finally {
            user.signOutAndDeleteUser();
        }
    }
    
    @Test
    public void researcherCanRetrieveConsentedParticipants() {
        // This should fail... this person is not a researcher...
        TestUser user = TestUserHelper.createAndSignInUser(StudyTest.class, true);
        try {
            ResearcherClient client = researcher.getSession().getResearcherClient();
            client.sendStudyParticipantsRoster();
        } catch(Exception e) {
            fail("Threw exception");
        } finally {
            user.signOutAndDeleteUser();
        }
    }
    
    @Test(expected = UnauthorizedException.class)
    public void adminCannotRetrieveParticipants() {
        ResearcherClient client = admin.getSession().getResearcherClient();
        client.sendStudyParticipantsRoster();
    }
    
    @Test
    public void adminCanGetAllStudies() {
        AdminClient client = admin.getSession().getAdminClient();
        
        ResourceList<Study> studies = client.getAllStudies();
        assertTrue(studies.getTotal() > 0);
    }
    
    private void assertVersionHasUpdated(VersionHolder holder, Study study, Long oldVersion) {
        assertNotNull(holder.getVersion());
        assertNotNull(study.getVersion());
        assertEquals(holder.getVersion(), study.getVersion());
        if (oldVersion != null) {
            assertNotEquals(oldVersion, study.getVersion());
        }
    }

    private Study getStudyObject(String identifier, Long version) {
        Study study = new Study();
        study.setIdentifier(identifier);
        study.setMinAgeOfConsent(18);
        study.setMaxNumOfParticipants(100);
        study.setName("Test Study [SDK]");
        study.setSponsorName("The Test Study Folks [SDK]");
        study.setSupportEmail("test@test.com");
        study.setConsentNotificationEmail("test2@test.com");
        study.setTechnicalEmail("test3@test.com");
        study.setTechnicalEmail("test3@test.com");
        study.getUserProfileAttributes().add("new_profile_attribute");
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
