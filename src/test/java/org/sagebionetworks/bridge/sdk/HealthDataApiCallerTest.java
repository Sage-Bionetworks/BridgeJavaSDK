package org.sagebionetworks.bridge.sdk;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
import com.google.common.collect.Lists;

public class HealthDataApiCallerTest {

    private static ClientProvider provider;
    private static HealthDataApiCaller healthApi;
    private static UserManagementApiCaller userManagementApi;
    private static SignInCredentials adminSignIn;
    private static SignInCredentials testUserSignIn;
    private static ObjectMapper mapper;

    private Tracker tracker;
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
        tracker = trackerApi.getAllTrackers().get(0);

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
            healthApi.addHealthDataRecords(tracker, records);
            fail("If we have reached here, then we did not need to sign in to call this method => test failure.");
        } catch (Throwable t) {}
        try {
            healthApi.getHealthDataRecordsInRange(tracker, DateTime.now().minusMonths(1), DateTime.now());
            fail("If we have reached here, then we did not need to sign in to call this method => test failure.");
        } catch (Throwable t) {}
        try {
            healthApi.getHealthDataRecord(tracker, record.getRecordId());
            fail("If we have reached here, then we did not need to sign in to call this method => test failure.");
        } catch (Throwable t) {}
        try {
            healthApi.updateHealthDataRecord(tracker, record);
            fail("If we have reached here, then we did not need to sign in to call this method => test failure.");
        } catch (Throwable t) {}
        try {
            healthApi.deleteHealthDataRecord(tracker, record.getRecordId());
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

        List<IdVersionHolder> holders = healthApi.addHealthDataRecords(tracker, recordsToAdd);
        assertTrue("Number of holders = all records added", holders.size() == recordsToAdd.size());

        List<HealthDataRecord> storedRecords = healthApi.getHealthDataRecordsInRange(tracker, DateTime.now()
                .minusYears(20), DateTime.now());
        for (HealthDataRecord record : storedRecords) {
            healthApi.deleteHealthDataRecord(tracker, record.getRecordId());
        }
    }

    @Test
    public void canGetandUpdateRecords() {
        // Make sure there's something in Bridge so that we can test get.
        List<HealthDataRecord> add = new ArrayList<HealthDataRecord>();
        add.add(HealthDataRecord.valueOf(0, "5555", DateTime.now().minusWeeks(1), DateTime.now(), data));
        healthApi.addHealthDataRecords(tracker, add);

        List<HealthDataRecord> records = healthApi.getHealthDataRecordsInRange(tracker, DateTime.now()
                .minusYears(30), DateTime.now());
        HealthDataRecord record = healthApi.getHealthDataRecord(tracker, records.get(0).getRecordId());
        assertTrue("retrieved record should be same as one chosen from list.", record.getRecordId().equals(records.get(0).getRecordId()));

        ObjectNode data2 = record.getData().deepCopy();
        data2.put("systolic", 7000);
        record.setData(data2);
        IdVersionHolder holder = healthApi.updateHealthDataRecord(tracker, record);
        assertTrue("record's version should be increased by 1.", holder.getVersion() == record.getVersion() + 1);
    }

    @Test
    public void CanGetHealthDataByDateRange() {
     // Time ranges used in this test, and where they overlap with the 3 test windows or not.
        //       1        1...<2
        //       2        1............3
        //       3                                                 4............6
        //       4                     3...........................4
        //       5                                                       >5.....6
        //       6                     3............................................
        //
        //                     2__________________________________________5
        //                1____________3
        //                                                         4______5

        // Constructing DateTime objects representing six points in time.
        DateTime time1 = DateTime.now().minusYears(5);
        DateTime time2 = DateTime.now().minusYears(4);
        DateTime time3 = DateTime.now().minusYears(3);
        DateTime time4 = DateTime.now().minusYears(2);
        DateTime time5 = DateTime.now().minusYears(1);
        DateTime time6 = DateTime.now();

        // Adding Health Data Records to BridgeServer.
        List<HealthDataRecord> records = createTestRecords(time1, time2.minusMillis(1));
        List<IdVersionHolder> holders = healthApi.addHealthDataRecords(tracker, records);
        IdVersionHolder holder1 = holders.get(0);

        records = createTestRecords(time1, time3);
        holders = healthApi.addHealthDataRecords(tracker, records);
        IdVersionHolder holder2 = holders.get(0);

        records = createTestRecords(time4, time6);
        holders = healthApi.addHealthDataRecords(tracker, records);
        IdVersionHolder holder3 = holders.get(0);

        records = createTestRecords(time3, time4);
        holders = healthApi.addHealthDataRecords(tracker, records);
        IdVersionHolder holder4 = holders.get(0);

        records = createTestRecords(time5.plusMillis(1), time6);
        holders = healthApi.addHealthDataRecords(tracker, records);
        IdVersionHolder holder5 = holders.get(0);

        records = createTestRecords(time3, time6.plusMillis(1));
        holders = healthApi.addHealthDataRecords(tracker, records);
        IdVersionHolder holder6 = holders.get(0);

        // Retrieve Health Data Records, testing that the correct added records are retrieved.
        records = healthApi.getHealthDataRecordsInRange(tracker, time2, time5);
        List<IdVersionHolder> retrievedHolders = getHolders(records);
        List<IdVersionHolder> expectedHolders = Lists.newArrayList(holder2, holder3, holder4, holder6);
        List<IdVersionHolder> unexpectedHolders = Lists.newArrayList(holder1, holder5);
        assertTrue("Returns records 2,3,4 and 6.", retrievedHolders.containsAll(expectedHolders));
        assertFalse("Does not return records 1 and 5.", retrievedHolders.containsAll(unexpectedHolders));

        records = healthApi.getHealthDataRecordsInRange(tracker, time1, time3);
        retrievedHolders = getHolders(records);
        expectedHolders = Lists.newArrayList(holder1, holder2, holder4, holder6);
        unexpectedHolders = Lists.newArrayList(holder3, holder5);
        assertTrue("Returns records 1, 2, 4 and 6.", retrievedHolders.containsAll(expectedHolders));
        assertFalse("Does not return records 3 and 5.", retrievedHolders.containsAll(unexpectedHolders));

        records = healthApi.getHealthDataRecordsInRange(tracker, time4, time5);
        retrievedHolders = getHolders(records);
        expectedHolders = Lists.newArrayList(holder3, holder4, holder6);
        unexpectedHolders = Lists.newArrayList(holder1, holder2, holder5);
        assertTrue("Returns records 3, 4 and 6.", retrievedHolders.containsAll(expectedHolders));
        assertFalse("Does not return records 1, 2 and 5.", retrievedHolders.containsAll(unexpectedHolders));
    }

    private List<HealthDataRecord> createTestRecords(DateTime start, DateTime end) {
        assert start.isBefore(end);

        ObjectNode data = mapper.createObjectNode();
        data.put("medication", "Lionipril");
        data.put("dosage", "10mg");
        data.put("frequency", "1x/day");

        String uniqueId = UUID.randomUUID().toString();
        HealthDataRecord record = HealthDataRecord.valueOf(0, uniqueId, start, end, data);

        return Lists.newArrayList(record);

    }

    private List<IdVersionHolder> getHolders(List<HealthDataRecord> records) {
        assert records.size() > 0 : "records needs to be non-empty.";

        List<IdVersionHolder> holders = Lists.newArrayList();
        for (HealthDataRecord record : records) {
            holders.add(IdVersionHolder.valueOf(record.getRecordId(), record.getVersion()));
        }

        return holders;
    }
}
