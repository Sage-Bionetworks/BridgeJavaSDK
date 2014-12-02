package org.sagebionetworks.bridge.sdk.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.Tests;
import org.sagebionetworks.bridge.sdk.AdminClient;
import org.sagebionetworks.bridge.sdk.TestUserHelper;
import org.sagebionetworks.bridge.sdk.TestUserHelper.TestUser;
import org.sagebionetworks.bridge.sdk.exceptions.EntityNotFoundException;
import org.sagebionetworks.bridge.sdk.models.holders.VersionHolder;
import org.sagebionetworks.bridge.sdk.models.studies.Study;

import com.google.common.collect.Lists;

public class StudyTest {
    
    private TestUser admin;
    
    private String identifier;

    @Before
    public void before() {
        identifier = Tests.randomIdentifier();
        admin = TestUserHelper.getSignedInAdmin();
    }

    @After
    public void after() {
        if (identifier != null) {
            admin.getSession().getAdminClient().deleteStudy(identifier);
        }
        admin.getSession().signOut();
    }

    @Test
    public void crudStudy() throws Exception {
        Study study = new Study();
        study.setIdentifier(identifier);
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
        identifier = null;
        
        try {
            newStudy = client.getStudy(study.getIdentifier());
            fail("Should have thrown exception");
        } catch(EntityNotFoundException e) {
        }
    }

    // Testing a researcher requires that the host be hacked so that the researcher matches
    // the host name of this newly created study. That's beyond the debugging setup of the 
    // server and the SDK at the moment. Would need to set a header, I think, with the host
    // that came back in the study.
    
}
