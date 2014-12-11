package org.sagebionetworks.bridge.sdk.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
        study = new Study();
        study.setIdentifier(Tests.randomIdentifier());
        study.setMinAgeOfConsent(18);
        study.setMaxNumOfParticipants(100);
        study.setName("Test Study [SDK]");
        study.setTrackers(Lists.newArrayList("sage:A", "sage:B"));
        
        AdminClient client = admin.getSession().getAdminClient();        
        VersionHolder holder = client.createStudy(study);
        assertNotNull(holder.getVersion());
        
        Study newStudy = client.getStudy(study.getIdentifier());
        
        assertEquals(study.getMinAgeOfConsent(), newStudy.getMinAgeOfConsent());
        assertEquals(study.getMaxNumOfParticipants(), newStudy.getMaxNumOfParticipants());
        assertEquals(study.getName(), newStudy.getName());
        assertEquals(2, newStudy.getTrackers().size());
        assertNotNull("Study hostname", newStudy.getHostname());
        assertNotNull("Study researcher role", newStudy.getResearcherRole());
        
        String alteredName = "Altered Test Study [SDK]";
        
        study.setName(alteredName);
        study.setMaxNumOfParticipants(50);
        study.setVersion(holder.getVersion()); // also in newStudy.getVersion(), of course
        client.updateStudy(study);

        newStudy = client.getStudy(study.getIdentifier());
        assertEquals(alteredName, newStudy.getName());
        assertEquals(50, newStudy.getMaxNumOfParticipants());

        client.deleteStudy(newStudy.getIdentifier());
        
        String identifier = study.getIdentifier();
        study = null;
        
        try {
            newStudy = client.getStudy(identifier);
            fail("Should have thrown exception");
        } catch(EntityNotFoundException e) {
        }
    }

    @Test
    public void researcherCannotAccessAnotherStudy() {
        study = new Study();
        study.setIdentifier(Tests.randomIdentifier());
        study.setMinAgeOfConsent(18);
        study.setMaxNumOfParticipants(100);
        study.setName("Test Study [SDK]");
        study.setTrackers(Lists.newArrayList("sage:A", "sage:B"));
        
        AdminClient client = admin.getSession().getAdminClient();        
        client.createStudy(study);
        
        TestUser researcher = TestUserHelper.createAndSignInUser(StudyTest.class, false, Tests.TEST_KEY+"_researcher");
        try {
            researcher.getSession().getAdminClient().getStudy(study.getIdentifier());
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
        
        ResearcherClient rclient = researcher.getSession().getResearcherClient();
        Study serverStudy = rclient.getStudy();
        
        assertEquals(Tests.TEST_KEY+"_researcher", serverStudy.getResearcherRole());
    }
    
    @Test(expected = UnauthorizedException.class)
    public void butNormalUserCannotAccessStudy() {
        TestUser user = TestUserHelper.createAndSignInUser(StudyTest.class, false);
        
        ResearcherClient rclient = user.getSession().getResearcherClient();
        rclient.getStudy();
    }
    
}
