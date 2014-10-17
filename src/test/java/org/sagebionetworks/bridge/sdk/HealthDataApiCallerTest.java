package org.sagebionetworks.bridge.sdk;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sagebionetworks.bridge.sdk.models.HealthDataRecord;
import org.sagebionetworks.bridge.sdk.models.IdVersionHolder;
import org.sagebionetworks.bridge.sdk.models.SignInCredentials;
import org.sagebionetworks.bridge.sdk.models.Tracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class HealthDataApiCallerTest {

    private static ClientProvider provider;
    private static HealthDataApiCaller healthApi;
    private static UserManagementApiCaller userManagementApi;
    private static SignInCredentials adminSignIn;
    private static SignInCredentials testUserSignIn;
    private static ObjectMapper mapper;

    private List<Tracker> trackers;
    private List<HealthDataRecord> records;
    private ObjectNode data;

    @BeforeClass
    public static void initialSetup() {
        mapper = Utilities.getMapper();
        provider = ClientProvider.valueOf();
        healthApi = HealthDataApiCaller.valueOf(provider);
        userManagementApi = UserManagementApiCaller.valueOf(provider);

        Config conf = provider.getConfig();
        adminSignIn = SignInCredentials.valueOf()
                .setUsername(conf.getAdminEmail())
                .setPassword(conf.getAdminPassword());

        provider.signIn(adminSignIn);

        boolean consent = true;
        String testUsername = "testUsername";
        String testEmail = "testingggg@sagebase.org";
        String testPassword = "p4ssw0rd";
        testUserSignIn = SignInCredentials.valueOf().setUsername(testEmail).setPassword(testPassword);
        userManagementApi.createUser(testUserSignIn.getUsername(), testUsername, testUserSignIn.getPassword(), consent);
    }

    @AfterClass
    public static void teardown() {
        userManagementApi.deleteUser(testUserSignIn.getUsername());
    }

    @Before
    public void before() {
        provider.signIn(adminSignIn);

        // Sign out admin user, sign in test user.
        provider.signOut();
        provider.signIn(testUserSignIn);

        TrackerApiCaller trackerApi = TrackerApiCaller.valueOf(provider);
        trackers = trackerApi.getAllTrackers();

        data = mapper.createObjectNode();
        data.put("systolic", 120);
        data.put("diastolic", 80);
    }

    @After
    public void after() {
        provider.signOut();
        provider.signIn(adminSignIn);
    }

    @Test
    public void noMethodShouldSucceedIfNotSignedIn() {
        provider.signOut();


        HealthDataRecord record = HealthDataRecord.valueOf(0, "1111", DateTime.now().minusWeeks(1), DateTime.now(), data);
        records = new ArrayList<HealthDataRecord>();
        records.add(record);

        try {
            healthApi.addHealthDataRecords(trackers.get(0), records);
            fail("If we have reached here, then we did not need to sign in to call this method => test failure.");
        } catch (Throwable t) {}
        try {
            healthApi.getHealthDataRecordsInRange(trackers.get(0), DateTime.now().minusMonths(1), DateTime.now());
            fail("If we have reached here, then we did not need to sign in to call this method => test failure.");
        } catch (Throwable t) {}
        try {
            healthApi.getHealthDataRecord(trackers.get(0), record.getRecordId());
            fail("If we have reached here, then we did not need to sign in to call this method => test failure.");
        } catch (Throwable t) {}
        try {
            healthApi.updateHealthDataRecord(trackers.get(0), record);
            fail("If we have reached here, then we did not need to sign in to call this method => test failure.");
        } catch (Throwable t) {}
        try {
            healthApi.deleteHealthDataRecord(trackers.get(0), record.getRecordId());
            fail("If we have reached here, then we did not need to sign in to call this method => test failure.");
        } catch (Throwable t) {}
    }

    @Test
    public void canAddAndRetrieveAndDeleteRecords() {
        List<HealthDataRecord> recordsToAdd = new ArrayList<HealthDataRecord>();
        recordsToAdd.add(HealthDataRecord.valueOf(0, "1111", DateTime.now().minusWeeks(1), DateTime.now(), data));
        recordsToAdd.add(HealthDataRecord.valueOf(1, "2222", DateTime.now().minusWeeks(2),
                DateTime.now().minusWeeks(1), data));
        recordsToAdd.add(HealthDataRecord.valueOf(0, "3333", DateTime.now().minusWeeks(3),
                DateTime.now().minusWeeks(2), data));

        List<IdVersionHolder> holders = healthApi.addHealthDataRecords(trackers.get(0), recordsToAdd);
        assertTrue("Number of holders = all records added", holders.size() == recordsToAdd.size());

        List<HealthDataRecord> storedRecords = healthApi.getHealthDataRecordsInRange(trackers.get(0), DateTime.now()
                .minusYears(20), DateTime.now());
        for (HealthDataRecord record : storedRecords) {
            healthApi.deleteHealthDataRecord(trackers.get(0), record.getRecordId());
        }
    }

    @Test
    public void canGetandUpdateRecords() {
        // Make sure there's something in Bridge so that we can test get.
        List<HealthDataRecord> add = new ArrayList<HealthDataRecord>();
        add.add(HealthDataRecord.valueOf(0, "5555", DateTime.now().minusWeeks(1), DateTime.now(), data));
        healthApi.addHealthDataRecords(trackers.get(0), add);

        List<HealthDataRecord> records = healthApi.getHealthDataRecordsInRange(trackers.get(0), DateTime.now()
                .minusYears(30), DateTime.now());
        HealthDataRecord record = healthApi.getHealthDataRecord(trackers.get(0), records.get(0).getRecordId());
        assertTrue("retrieved record should be same as one chosen from list.", record.getRecordId().equals(records.get(0).getRecordId()));

        ObjectNode data2 = record.getData().deepCopy();
        data2.put("systolic", 7000);
        record.setData(data2);
        IdVersionHolder holder = healthApi.updateHealthDataRecord(trackers.get(0), record);
        assertTrue("record's version should be increased by 1.", holder.getVersion() == record.getVersion() + 1);
    }
}
