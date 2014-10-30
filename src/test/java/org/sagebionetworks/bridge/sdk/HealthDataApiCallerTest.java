package org.sagebionetworks.bridge.sdk;

import java.util.List;
import java.util.UUID;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.sagebionetworks.bridge.sdk.TestUserHelper.TestUser;
import org.sagebionetworks.bridge.sdk.models.HealthDataRecord;
import org.sagebionetworks.bridge.sdk.models.IdVersionHolder;
import org.sagebionetworks.bridge.sdk.models.Tracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;

public class HealthDataApiCallerTest {

    private ObjectMapper mapper = Utilities.getMapper();
    private HealthDataApiCaller healthApi;
    private Tracker tracker;
    private List<HealthDataRecord> records;
    private ObjectNode data;

    private TestUser testUser;

    @Before
    public void before() {
        testUser = TestUserHelper.createAndSignInUser(HealthDataApiCallerTest.class, true);
        healthApi = HealthDataApiCaller.valueOf(testUser.getSession());

        TrackerApiCaller trackerApi = TrackerApiCaller.valueOf(testUser.getSession());
        tracker = trackerApi.getAllTrackers().get(0);

        data = mapper.createObjectNode();
        data.put("systolic", 120);
        data.put("diastolic", 80);
    }

    @After
    public void teardown() {
        testUser.signOutAndDeleteUser();
    }

    private List<HealthDataRecord> createTestRecords(DateTime start, DateTime end) {
        assert start.isBefore(end);

        ObjectNode data = mapper.createObjectNode();
        data.put("systolic", 130);
        data.put("diastolic", 70);

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
