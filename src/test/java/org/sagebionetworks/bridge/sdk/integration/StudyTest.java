package org.sagebionetworks.bridge.sdk.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.Tests;
import org.sagebionetworks.bridge.sdk.AdminClient;
import org.sagebionetworks.bridge.sdk.ClientInfo;
import org.sagebionetworks.bridge.sdk.ClientProvider;
import org.sagebionetworks.bridge.sdk.DeveloperClient;
import org.sagebionetworks.bridge.sdk.ResearcherClient;
import org.sagebionetworks.bridge.sdk.Roles;
import org.sagebionetworks.bridge.sdk.TestUserHelper;
import org.sagebionetworks.bridge.sdk.TestUserHelper.TestUser;
import org.sagebionetworks.bridge.sdk.exceptions.EntityNotFoundException;
import org.sagebionetworks.bridge.sdk.exceptions.UnauthorizedException;
import org.sagebionetworks.bridge.sdk.exceptions.UnsupportedVersionException;
import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.holders.VersionHolder;
import org.sagebionetworks.bridge.sdk.models.studies.OperatingSystem;
import org.sagebionetworks.bridge.sdk.models.studies.Study;

import com.google.common.collect.Sets;

public class StudyTest {
    
    private TestUser admin;
    private boolean createdStudy;
    private Study study;

    @Before
    public void before() {
        admin = TestUserHelper.getSignedInAdmin();
        createdStudy = false;
        study = null;
    }
    
    @After
    public void after() {
        ClientProvider.setClientInfo(new ClientInfo.Builder().build());
        if (createdStudy && study != null) {
            admin.getSession().getAdminClient().deleteStudy(study.getIdentifier());
        }
        admin.getSession().signOut();
    }

    @Test
    public void crudStudy() throws Exception {
        AdminClient client = admin.getSession().getAdminClient();
        
        String identifier = Tests.randomIdentifier(StudyTest.class);
        study = getStudyObject(identifier, null);
        assertNull(study.getVersion());
        
        VersionHolder holder = client.createStudy(study);
        createdStudy = true;
        assertVersionHasUpdated(holder, study, null);

        Study newStudy = client.getStudy(study.getIdentifier());
        // Verify study has password/email templates
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
        assertEquals(study.getUserProfileAttributes(), newStudy.getUserProfileAttributes());
        assertEquals(study.getTaskIdentifiers(), newStudy.getTaskIdentifiers());
        assertEquals(study.getDataGroups(), newStudy.getDataGroups());
        assertEquals(study.getMinSupportedAppVersions().get(OperatingSystem.ANDROID),
                newStudy.getMinSupportedAppVersions().get(OperatingSystem.ANDROID));
        assertEquals(study.getMinSupportedAppVersions().get(OperatingSystem.IOS),
                newStudy.getMinSupportedAppVersions().get(OperatingSystem.IOS));
        // This was set to true even though we didn't set it.
        assertTrue(newStudy.isStrictUploadValidationEnabled());
        // And this is true because admins can set it to true. 
        assertTrue(newStudy.isHealthCodeExportEnabled());
        
        Long oldVersion = newStudy.getVersion();
        alterStudy(newStudy);
        holder = client.updateStudy(newStudy);
        assertVersionHasUpdated(holder, newStudy, oldVersion);
        
        Study newerStudy = client.getStudy(newStudy.getIdentifier());
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
        TestUser researcher = TestUserHelper.createAndSignInUser(StudyTest.class, false, Roles.RESEARCHER);
        try {
            String identifier = Tests.randomIdentifier(StudyTest.class);
            study = getStudyObject(identifier, null);

            AdminClient client = admin.getSession().getAdminClient();
            client.createStudy(study);
            createdStudy = true;

            try {
                researcher.getSession().getAdminClient().getStudy(identifier);
                fail("Should not have been able to get this other study");
            } catch(UnauthorizedException e) {
                assertEquals("Unauthorized HTTP response code", 403, e.getStatusCode());
            }
        } finally {
            researcher.signOutAndDeleteUser();
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
    public void developerCannotSetHealthCodeToExport() {
        TestUser developer = TestUserHelper.createAndSignInUser(StudyTest.class, false, Roles.DEVELOPER);
        try {
            DeveloperClient devClient = developer.getSession().getDeveloperClient();
            
            Study study = devClient.getStudy();
            study.setHealthCodeExportEnabled(true);
            devClient.updateStudy(study);
            
            study = devClient.getStudy();
            assertFalse(study.isHealthCodeExportEnabled());
        } finally {
            developer.signOutAndDeleteUser();
        }
    }
    
    @Test
    public void researcherCanRetrieveConsentedParticipants() {
        TestUser researcher = TestUserHelper.createAndSignInUser(StudyTest.class, false, Roles.RESEARCHER);
        TestUser user = TestUserHelper.createAndSignInUser(StudyTest.class, true);
        try {
            ResearcherClient client = researcher.getSession().getResearcherClient();
            client.sendStudyParticipantsRoster();
        } catch(Exception e) {
            fail("Threw exception");
        } finally {
            user.signOutAndDeleteUser();
            researcher.signOutAndDeleteUser();
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
    
    @Test
    public void userCannotAccessApisWithDeprecatedClient() {
        AdminClient adminClient = admin.getSession().getAdminClient();
        Study study = adminClient.getStudy(Tests.TEST_KEY);
        // Set a minimum value that should not any other tests
        if (study.getMinSupportedAppVersions().get(OperatingSystem.ANDROID) == null) {
            study.getMinSupportedAppVersions().put(OperatingSystem.ANDROID, 1);
            adminClient.updateStudy(study);
        }
        TestUser user = TestUserHelper.createAndSignInUser(StudyTest.class, true);
        try {
            
            // This is a version zero client, it should not be accepted
            ClientProvider.setClientInfo(new ClientInfo.Builder().withDevice("Unknown").withOsName("Android")
                    .withOsVersion("1").withAppName(Tests.APP_NAME).withAppVersion(0).build());
            user.getSession().getUserClient().getScheduledActivities(3, DateTimeZone.UTC);
            fail("Should have thrown exception");
            
        } catch(UnsupportedVersionException e) {
            // This is good.
        } finally {
            user.signOutAndDeleteUser();
        }
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
        study.getUserProfileAttributes().add("new_profile_attribute");
        study.setTaskIdentifiers(Sets.newHashSet("taskA")); // setting it differently just for the heck of it 
        study.setDataGroups(Sets.newHashSet("beta_users", "production_users"));
        study.setResetPasswordTemplate(Tests.TEST_RESET_PASSWORD_TEMPLATE);
        study.setVerifyEmailTemplate(Tests.TEST_VERIFY_EMAIL_TEMPLATE);
        study.setHealthCodeExportEnabled(true);
        study.getMinSupportedAppVersions().put(OperatingSystem.ANDROID, 10);
        study.getMinSupportedAppVersions().put(OperatingSystem.IOS, 14);
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
