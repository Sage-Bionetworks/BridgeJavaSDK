package org.sagebionetworks.bridge.sdk.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.sdk.TestUserHelper;
import org.sagebionetworks.bridge.sdk.TestUserHelper.TestUser;
import org.sagebionetworks.bridge.sdk.UserClient;
import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.holders.GuidVersionHolder;
import org.sagebionetworks.bridge.sdk.models.studies.Tracker;
import org.sagebionetworks.bridge.sdk.models.users.HealthDataRecord;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;

public class HealthDataTest {

    private Tracker tracker;
    private ObjectNode data;
    private TestUser testUser;

    @Before
    public void before() {
        testUser = TestUserHelper.createAndSignInUser(HealthDataTest.class, true);

        tracker = testUser.getSession().getUserClient().getAllTrackers().getItems().get(0);

        data = JsonNodeFactory.instance.objectNode();
        data.put("systolic", 120);
        data.put("diastolic", 80);
    }

    @After
    public void after() {
        testUser.signOutAndDeleteUser();
    }

    @Test
    public void noMethodShouldSucceedIfNotSignedIn() {
        UserClient client = testUser.getSession().getUserClient();
        testUser.getSession().signOut();

        HealthDataRecord record = makeHDR(0L, "1111", DateTime.now().minusWeeks(1), DateTime.now(), data);
        List<HealthDataRecord> records = Lists.newArrayList();
        records.add(record);

        try {
            client.addHealthDataRecords(tracker, records);
            fail("If we have reached here, then we did not need to sign in to call this method => test failure.");
        } catch (IllegalStateException e) {}
        try {
            client.getHealthDataRecordsInRange(tracker, DateTime.now().minusMonths(1), DateTime.now());
            fail("If we have reached here, then we did not need to sign in to call this method => test failure.");
        } catch (IllegalStateException e) {}
        try {
            client.getHealthDataRecord(tracker, record.getGuid());
            fail("If we have reached here, then we did not need to sign in to call this method => test failure.");
        } catch (IllegalStateException e) {}
        try {
            client.updateHealthDataRecord(tracker, record);
            fail("If we have reached here, then we did not need to sign in to call this method => test failure.");
        } catch (IllegalStateException e) {}
        try {
            client.deleteHealthDataRecord(tracker, record.getGuid());
            fail("If we have reached here, then we did not need to sign in to call this method => test failure.");
        } catch (IllegalStateException e) {}
    }

    @Test
    public void canAddAndRetrieveAndDeleteRecords() {
        UserClient client = testUser.getSession().getUserClient();
        try {
            List<HealthDataRecord> records = new ArrayList<HealthDataRecord>();
            records.add(makeHDR(0L, "1111", DateTime.now().minusWeeks(1), DateTime.now(), data));
            records.add(makeHDR(1L, "2222", DateTime.now().minusWeeks(2), DateTime.now().minusWeeks(1), data));
            records.add(makeHDR(0L, "3333", DateTime.now().minusWeeks(3), DateTime.now().minusWeeks(2), data));

            ResourceList<GuidVersionHolder> holders = client.addHealthDataRecords(tracker, records);
            assertTrue("Number of holders = all records added", holders.getTotal() == records.size());
        } finally {
            ResourceList<HealthDataRecord> records = getAllRecords(client);
            for (HealthDataRecord record : records.getItems()) {
                client.deleteHealthDataRecord(tracker, record.getGuid());
            }
            records = getAllRecords(client);
            assertEquals("All records deleted", 0, records.getTotal());
        }

    }

    @Test
    public void canGetandUpdateRecords() {
        UserClient client = testUser.getSession().getUserClient();
        try {
            // Make sure there's something in Bridge so that we can test get.
            List<HealthDataRecord> add = new ArrayList<HealthDataRecord>();
            add.add(makeHDR(0L, "5555", DateTime.now().minusWeeks(1), DateTime.now(), data));
            client.addHealthDataRecords(tracker, add);

            ResourceList<HealthDataRecord> records = client.getHealthDataRecordsInRange(tracker, DateTime.now()
                    .minusYears(30), DateTime.now());
            HealthDataRecord record = client.getHealthDataRecord(tracker, records.getItems().get(0).getGuid());
            assertTrue("retrieved record should be same as one chosen from list.", record.getGuid().equals(records.getItems().get(0).getGuid()));

            ObjectNode data2 = record.getData().deepCopy();
            data2.put("systolic", 7000);
            record.setData(data2);
            GuidVersionHolder holder = client.updateHealthDataRecord(tracker, record);
            assertTrue("record's version should be increased by 1.", holder.getVersion() == record.getVersion() + 1);
        } finally {
            ResourceList<HealthDataRecord> records = getAllRecords(client);
            for (HealthDataRecord record : records.getItems()) {
                client.deleteHealthDataRecord(tracker, record.getGuid());
            }
        }
    }

    @Test
    public void canGetHealthDataByDateRange() {
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

        UserClient client = testUser.getSession().getUserClient();
        try {
            // Constructing DateTime objects representing six points in time.
            DateTime time1 = DateTime.now().minusYears(5);
            DateTime time2 = DateTime.now().minusYears(4);
            DateTime time3 = DateTime.now().minusYears(3);
            DateTime time4 = DateTime.now().minusYears(2);
            DateTime time5 = DateTime.now().minusYears(1);
            DateTime time6 = DateTime.now();

            // Adding Health Data Records to BridgeServer.
            ResourceList<HealthDataRecord> records = createTestRecords(time1, time2.minusMillis(1));
            ResourceList<GuidVersionHolder> holders = client.addHealthDataRecords(tracker, records.getItems());
            GuidVersionHolder holder1 = asTestHolder(holders.getItems().get(0));

            records = createTestRecords(time1, time3);
            holders = client.addHealthDataRecords(tracker, records.getItems());
            GuidVersionHolder holder2 = asTestHolder(holders.getItems().get(0));

            records = createTestRecords(time4, time6);
            holders = client.addHealthDataRecords(tracker, records.getItems());
            GuidVersionHolder holder3 = asTestHolder(holders.getItems().get(0));

            records = createTestRecords(time3, time4);
            holders = client.addHealthDataRecords(tracker, records.getItems());
            GuidVersionHolder holder4 = asTestHolder(holders.getItems().get(0));

            records = createTestRecords(time5.plusMillis(1), time6);
            holders = client.addHealthDataRecords(tracker, records.getItems());
            GuidVersionHolder holder5 = asTestHolder(holders.getItems().get(0));

            records = createTestRecords(time3, time6.plusMillis(1));
            holders = client.addHealthDataRecords(tracker, records.getItems());
            GuidVersionHolder holder6 = asTestHolder(holders.getItems().get(0));

            // Retrieve Health Data Records, testing that the correct added records are retrieved.
            records = client.getHealthDataRecordsInRange(tracker, time2, time5);
            List<GuidVersionHolder> retrievedHolders = getHolders(records.getItems());
            List<GuidVersionHolder> expectedHolders = Lists.newArrayList(holder2, holder3, holder4, holder6);
            List<GuidVersionHolder> unexpectedHolders = Lists.newArrayList(holder1, holder5);

            assertTrue("Returns records 2,3,4 and 6.", retrievedHolders.containsAll(expectedHolders));
            assertFalse("Does not return records 1 and 5.", retrievedHolders.containsAll(unexpectedHolders));

            records = client.getHealthDataRecordsInRange(tracker, time1, time3);
            retrievedHolders = getHolders(records.getItems());
            expectedHolders = Lists.newArrayList(holder1, holder2, holder4, holder6);
            unexpectedHolders = Lists.newArrayList(holder3, holder5);
            assertTrue("Returns records 1, 2, 4 and 6.", retrievedHolders.containsAll(expectedHolders));
            assertFalse("Does not return records 3 and 5.", retrievedHolders.containsAll(unexpectedHolders));

            records = client.getHealthDataRecordsInRange(tracker, time4, time5);
            retrievedHolders = getHolders(records.getItems());
            expectedHolders = Lists.newArrayList(holder3, holder4, holder6);
            unexpectedHolders = Lists.newArrayList(holder1, holder2, holder5);
            assertTrue("Returns records 3, 4 and 6.", retrievedHolders.containsAll(expectedHolders));
            assertFalse("Does not return records 1, 2 and 5.", retrievedHolders.containsAll(unexpectedHolders));
        } finally {
            ResourceList<HealthDataRecord> records = getAllRecords(client);
            for (HealthDataRecord record : records.getItems()) {
                client.deleteHealthDataRecord(tracker, record.getGuid());
            }
        }
    }
    
    private HealthDataRecord makeHDR(long version, String guid, DateTime startDate, DateTime endDate, JsonNode data) {
        HealthDataRecord record = new HealthDataRecord();
        record.setData(data);
        record.setStartDate(startDate);
        record.setEndDate(endDate);
        record.setVersion(version);
        return record;
    }

    private ResourceList<HealthDataRecord> getAllRecords(UserClient client) {
        return client.getHealthDataRecordsInRange(tracker, DateTime.now().minusYears(30), DateTime.now());
    }

    private ResourceList<HealthDataRecord> createTestRecords(DateTime start, DateTime end) {
        assert start.isBefore(end);

        ObjectNode data = JsonNodeFactory.instance.objectNode();
        data.put("systolic", 130);
        data.put("diastolic", 70);

        String uniqueId = UUID.randomUUID().toString();
        final HealthDataRecord record = makeHDR(0L, uniqueId, start, end, data);

        return new ResourceList<HealthDataRecord>() {
            @Override public List<HealthDataRecord> getItems() {
                return Lists.newArrayList(record);
            }
            @Override public int getTotal() {
                return 1;
            }
            @Override public HealthDataRecord get(int index) {
                return record;
            }
            @Override public Iterator<HealthDataRecord> iterator() {
                return Lists.newArrayList(record).iterator();
            }
        };
    }

    private List<GuidVersionHolder> getHolders(List<HealthDataRecord> records) {
        assert records.size() > 0 : "records needs to be non-empty.";

        List<GuidVersionHolder> list = Lists.newArrayList();
        for (final HealthDataRecord record : records) {
            list.add(new TestGuidVersionHolder(record.getGuid(), record.getVersion()));
        }
        return list;
    }

    private GuidVersionHolder asTestHolder(GuidVersionHolder holder) {
        return new TestGuidVersionHolder(holder.getGuid(), holder.getVersion());
    }

    private class TestGuidVersionHolder implements GuidVersionHolder {
        private String guid;
        private Long version;
        public TestGuidVersionHolder(String guid, Long version) {
            this.guid = guid;
            this.version = version;
        }
        @Override public String getGuid() {
            return guid;
        }
        @Override public Long getVersion() {
            return version;
        }
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((guid == null) ? 0 : guid.hashCode());
            result = prime * result + ((version == null) ? 0 : version.hashCode());
            return result;
        }
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            TestGuidVersionHolder other = (TestGuidVersionHolder) obj;
            if (!getOuterType().equals(other.getOuterType()))
                return false;
            if (guid == null) {
                if (other.guid != null)
                    return false;
            } else if (!guid.equals(other.guid))
                return false;
            if (version == null) {
                if (other.version != null)
                    return false;
            } else if (!version.equals(other.version))
                return false;
            return true;
        }
        private HealthDataTest getOuterType() {
            return HealthDataTest.this;
        }
        @Override
        public String toString() {
            return "TestGuidVersionHolder [guid=" + guid + ", version=" + version + "]";
        }
    }
}
