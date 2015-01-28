package org.sagebionetworks.bridge.sdk.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
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

    private TestUser admin;

    private Study study;

    @Before
    public void before() {
        admin = TestUserHelper.getSignedInAdmin();
    }

    @After
    public void after() {
        if (study != null) {
            admin.getSession().getAdminClient().deleteStudy(study.getIdentifier());
        }
        admin.getSession().signOut();
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
    public void researcherCannotAccessAnotherStudy() {
        String identifier = Tests.randomIdentifier();
        study = createStudy(identifier, null);

        AdminClient client = admin.getSession().getAdminClient();
        client.createStudy(study);

        TestUser researcher = TestUserHelper.createAndSignInUser(StudyTest.class, false, Tests.TEST_KEY+"_researcher");
        try {
            researcher.getSession().getAdminClient().getStudy(identifier);
            fail("Should not have been able to get this other study");
        } catch(UnauthorizedException e) {
            assertEquals("Unauthorized HTTP response code", 403, e.getStatusCode());
        } finally {
            researcher.signOutAndDeleteUser();
        }

    }

    @Test
    public void researcherCanAccessStudy() {
        TestUser researcher = TestUserHelper.createAndSignInUser(StudyTest.class, false, Tests.TEST_KEY+"_researcher");
        try {
            ResearcherClient rclient = researcher.getSession().getResearcherClient();
            Study serverStudy = rclient.getStudy();

            assertEquals(Tests.TEST_KEY+"_researcher", serverStudy.getResearcherRole());
        } finally {
            researcher.signOutAndDeleteUser();
        }
    }

    @Test(expected = UnauthorizedException.class)
    public void butNormalUserCannotAccessStudy() {
        TestUser user = TestUserHelper.createAndSignInUser(StudyTest.class, false);
        try {
            ResearcherClient rclient = user.getSession().getResearcherClient();
            rclient.getStudy();
        } finally {
            user.signOutAndDeleteUser();
        }
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
