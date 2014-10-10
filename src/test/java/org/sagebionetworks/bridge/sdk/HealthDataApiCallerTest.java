package org.sagebionetworks.bridge.sdk;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sagebionetworks.bridge.sdk.models.HealthDataRecord;
import org.sagebionetworks.bridge.sdk.models.IdVersionHolder;
import org.sagebionetworks.bridge.sdk.models.SignInCredentials;
import org.sagebionetworks.bridge.sdk.models.Tracker;

public class HealthDataApiCallerTest {

    // TODO running into 412 precondition failed issues, because I'm running tests against the admin who is not technically consented.
    // need an admin controller on BridgePF so that I can create users for the tests.

    private static ClientProvider provider;
    private static HealthDataApiCaller healthApi;
    private List<Tracker> trackers;
    private List<HealthDataRecord> records;

    @BeforeClass
    public static void initialSetup() {
        provider = ClientProvider.valueOf();
        healthApi = HealthDataApiCaller.valueOf(provider);
    }

    @Before
    public void before() {
        if (!provider.isSignedIn()) {
            String adminEmail = provider.getConfig().getAdminEmail();
            String adminPassword = provider.getConfig().getAdminPassword();
            provider.signIn(SignInCredentials.valueOf(adminEmail, adminPassword));
        }

        TrackerApiCaller trackerApi = TrackerApiCaller.valueOf(provider);
        trackers = trackerApi.getAllTrackers();
    }

    @Test
    public void noMethodShouldSucceedIfNotSignedIn() {
        provider.signOut();

        HealthDataRecord record = HealthDataRecord.valueOf(0, 1111, DateTime.now().minusWeeks(1), DateTime.now(), "data");
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
            healthApi.getHealthDataRecord(trackers.get(0), record.getId());
            fail("If we have reached here, then we did not need to sign in to call this method => test failure.");
        } catch (Throwable t) {}
        try {
            healthApi.updateHealthDataRecord(trackers.get(0), record);
            fail("If we have reached here, then we did not need to sign in to call this method => test failure.");
        } catch (Throwable t) {}
        try {
            healthApi.deleteHealthDataRecord(trackers.get(0), record.getId());
            fail("If we have reached here, then we did not need to sign in to call this method => test failure.");
        } catch (Throwable t) {}
    }

    @Test
    public void canAddAndRetrieveAndDeleteRecords() {
        System.out.println("get health data records in range");
        List<HealthDataRecord> records = healthApi.getHealthDataRecordsInRange(trackers.get(0), DateTime.now().minusYears(20), DateTime.now());

        List<HealthDataRecord> recordsToAdd = new ArrayList<HealthDataRecord>();
        recordsToAdd.add(HealthDataRecord.valueOf(0, 1111, DateTime.now().minusWeeks(1), DateTime.now(), "data"));
        recordsToAdd.add(HealthDataRecord.valueOf(1, 2222, DateTime.now().minusWeeks(2), DateTime.now().minusWeeks(1), "data"));
        recordsToAdd.add(HealthDataRecord.valueOf(0, 3333, DateTime.now().minusWeeks(3), DateTime.now().minusWeeks(2), "data"));

        System.out.println("add data records.");
        List<IdVersionHolder> holders = healthApi.addHealthDataRecords(trackers.get(0), recordsToAdd);
        assertTrue("", holders.size() == records.size() + recordsToAdd.size());

        for (HealthDataRecord record : recordsToAdd) {
            System.out.println("delete data record.");
            boolean success = healthApi.deleteHealthDataRecord(trackers.get(0), record.getId());
            if (success == false) {
                fail("Deleting health data record was unsuccessful, test failed.");
            }
        }
    }

    @Test
    public void canGetandUpdateRecords() {
        System.out.println("get health data records in range from 20 years ago to now.");
        List<HealthDataRecord> records = healthApi.getHealthDataRecordsInRange(trackers.get(0), DateTime.now()
                .minusYears(30), DateTime.now());
        System.out.println("get single data record");
        HealthDataRecord record = healthApi.getHealthDataRecord(trackers.get(0), records.get(0).getId());
        assertTrue("retrieved record should be same as one chosen from list.", record.getId() == records.get(0).getId());

        record.setData("data2");
        System.out.println("update data record.");
        IdVersionHolder holder = healthApi.updateHealthDataRecord(trackers.get(0), record);
        assertTrue("record's version should be increased by 1.", holder.getVersion() == record.getVersion() + 1);
    }
}
