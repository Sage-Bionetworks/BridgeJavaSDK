package org.sagebionetworks.bridge.sdk.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.sagebionetworks.bridge.sdk.DeveloperClient;
import org.sagebionetworks.bridge.sdk.Roles;
import org.sagebionetworks.bridge.sdk.TestUserHelper;
import org.sagebionetworks.bridge.sdk.TestUserHelper.TestUser;
import org.sagebionetworks.bridge.sdk.exceptions.BadRequestException;
import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.holders.GuidVersionHolder;
import org.sagebionetworks.bridge.sdk.models.subpopulations.Subpopulation;
import org.sagebionetworks.bridge.sdk.models.subpopulations.SubpopulationGuid;

public class SubpopulationTest {

    private TestUser admin;
    private TestUser developer;
    private SubpopulationGuid guid;
    
    @Before
    public void before() {
        admin = TestUserHelper.getSignedInAdmin();
        developer = TestUserHelper.createAndSignInUser(SubpopulationTest.class, false, Roles.DEVELOPER);
    }
    
    @After
    public void after() {
        if (guid != null) {
            admin.getSession().getAdminClient().deleteSubpopulationPermanently(guid);    
        }
        developer.signOutAndDeleteUser();
    }
    
    @Test
    public void canCRUD() {
        DeveloperClient client = developer.getSession().getDeveloperClient();
        
        // Study has a default subpopulation
        ResourceList<Subpopulation> subpops = client.getAllSubpopulations();
        int initialCount = subpops.getTotal();
        assertNotNull(findByName(subpops.getItems(), "Default Consent Group"));
        
        // Create a new one
        Subpopulation subpop = new Subpopulation();
        subpop.setName("Later Consent Group");
        subpop.setMinAppVersion(10);
        GuidVersionHolder keys = client.createSubpopulation(subpop);
        subpop.setHolder(keys);
        
        guid = subpop.getGuid();
        
        // Read it back
        Subpopulation retrieved = client.getSubpopulation(subpop.getGuid());
        assertEquals(subpop, retrieved);
        
        // Update it
        retrieved.setDescription("Adding a description");
        retrieved.setMinAppVersion(8);
        keys = client.updateSubpopulation(retrieved);
        retrieved.setHolder(keys);
        
        // Verify it is available in the list
        subpops = client.getAllSubpopulations();
        assertEquals(initialCount+1, subpops.getTotal());
        assertNotNull(findByName(subpops.getItems(), "Default Consent Group"));
        assertNotNull(findByName(subpops.getItems(), "Later Consent Group"));

        // Delete it
        client.deleteSubpopulation(retrieved.getGuid());
        assertEquals(initialCount, client.getAllSubpopulations().getTotal());
        
        // Cannot delete the default, however:
        try {
            Subpopulation defaultSubpop = findByName(subpops.getItems(), "Default Consent Group");
            client.deleteSubpopulation(defaultSubpop.getGuid());
            fail("Should have thrown an exception.");
        } catch(BadRequestException e) {
            assertEquals("Cannot delete the default subpopulation for a study.", e.getMessage());
        }
    }
    
    private Subpopulation findByName(List<Subpopulation> subpops, String name) {
        for (Subpopulation subpop : subpops) {
            if (subpop.getName().equals(name)) {
                return subpop;
            }
        }
        return null;
    }
    
}
