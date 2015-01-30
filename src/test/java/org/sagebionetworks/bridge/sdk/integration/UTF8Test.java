package org.sagebionetworks.bridge.sdk.integration;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.sdk.ClientProvider;
import org.sagebionetworks.bridge.sdk.Session;
import org.sagebionetworks.bridge.sdk.TestUserHelper;
import org.sagebionetworks.bridge.sdk.TestUserHelper.TestUser;
import org.sagebionetworks.bridge.sdk.UserClient;
import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.holders.GuidVersionHolder;
import org.sagebionetworks.bridge.sdk.models.studies.Tracker;
import org.sagebionetworks.bridge.sdk.models.users.HealthDataRecord;
import org.sagebionetworks.bridge.sdk.models.users.SignInCredentials;
import org.sagebionetworks.bridge.sdk.models.users.UserProfile;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;

public class UTF8Test {
    
    private TestUser testUser;
    
    @Before
    public void before() {
        testUser = TestUserHelper.createAndSignInUser(HealthDataTest.class, true);
    }
    
    @After
    public void after() {
        testUser.signOutAndDeleteUser();
    }
    
    @Test
    public void canSaveAndRetrieveUTF8HealthData() {
        UserClient client = testUser.getSession().getUserClient();
        
        ObjectNode data = JsonNodeFactory.instance.objectNode();
        data.put("medication", "☃");
        data.put("dosage", "♘");
        data.put("frequency", "советских военных судов и самолетов была отмечена");
        
        HealthDataRecord record = new HealthDataRecord();
        record.setData(data);
        record.setStartDate(DateTime.now());
        record.setEndDate(DateTime.now());
        
        // Medication tracker, we hope.
        Tracker tracker = testUser.getSession().getUserClient().getAllTrackers().getItems().get(1); 
        
        ResourceList<GuidVersionHolder> guids = client.addHealthDataRecords(tracker, Lists.newArrayList(record));
        
        record = client.getHealthDataRecord(tracker, guids.get(0).getGuid());
        
        assertEquals("☃", record.getData().get("medication").asText());
        assertEquals("♘", record.getData().get("dosage").asText());
        assertEquals("советских военных судов и самолетов была отмечена", record.getData().get("frequency").asText());
    }
    
    @Test
    public void canSaveAndRetrieveDataStoredInRedis() {
        UserClient client = testUser.getSession().getUserClient();
        
        UserProfile profile = client.getProfile();
        profile.setFirstName("☃");
        // I understand from the source of this text that it is actualy UTF-16.
        // It should still work.
        profile.setLastName("지구상의　３대　극지라　불리는");
        client.saveProfile(profile); // should be update
        
        // Force a refresh of the Redis session cache.
        testUser.getSession().signOut();
        Session session = ClientProvider.signIn(new SignInCredentials(testUser.getEmail(), testUser.getPassword()));
        
        profile = session.getUserClient().getProfile();
        assertEquals("☃", profile.getFirstName());
        assertEquals("지구상의　３대　극지라　불리는", profile.getLastName());
    }
    
}
