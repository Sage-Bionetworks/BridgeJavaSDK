package org.sagebionetworks.bridge.sdk;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.sagebionetworks.bridge.sdk.exceptions.BridgeSDKException;
import org.sagebionetworks.bridge.sdk.models.holders.IdVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.SimpleIdVersionHolder;
import org.sagebionetworks.bridge.sdk.models.studies.Tracker;
import org.sagebionetworks.bridge.sdk.models.users.HealthDataRecord;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

class HealthDataApiCaller extends BaseApiCaller {

    private HealthDataApiCaller(Session session) {
        super(session);
    }

    static HealthDataApiCaller valueOf(Session session) {
        return new HealthDataApiCaller(session);
    }

    HealthDataRecord getHealthDataRecord(Tracker tracker, String recordId) {
        String trackerId = String.valueOf(tracker.getId());
        String url = config.getHealthDataTrackerRecordApi(trackerId, recordId);
        HttpResponse response = get(url);

        return getResponseBodyAsType(response, HealthDataRecord.class);
    }

    SimpleIdVersionHolder updateHealthDataRecord(Tracker tracker, HealthDataRecord record) {
        String json = null;
        try {
            json = mapper.writeValueAsString(record);
        } catch (JsonProcessingException e) {
            throw new BridgeSDKException("Could not process HealthDataRecord. Are you sure it is correct? "
                    + record.toString(), e);
        }
        String trackerId = String.valueOf(tracker.getId());
        String url = config.getHealthDataTrackerRecordApi(trackerId, record.getId());
        HttpResponse response = post(url, json);

        return getResponseBodyAsType(response, SimpleIdVersionHolder.class);
    }

    void deleteHealthDataRecord(Tracker tracker, String recordId) {
        String trackerId = String.valueOf(tracker.getId());
        String url = config.getHealthDataTrackerRecordApi(trackerId, recordId);
        delete(url);
    }

    List<HealthDataRecord> getHealthDataRecordsInRange(Tracker tracker, DateTime startDate, DateTime endDate) {
        Map<String,String> queryParameters = new HashMap<String,String>();
        queryParameters.put("startDate", startDate.toString(ISODateTimeFormat.dateTime()));
        queryParameters.put("endDate", endDate.toString(ISODateTimeFormat.dateTime()));

        String trackerId = String.valueOf(tracker.getId());
        String url = config.getHealthDataTrackerApi(trackerId);
        HttpResponse response = get(url, queryParameters);

        JsonNode items = getPropertyFromResponse(response, "items");
        List<HealthDataRecord> records = mapper.convertValue(items,
                mapper.getTypeFactory().constructCollectionType(List.class, HealthDataRecord.class));

        return records;
    }

    List<IdVersionHolder> addHealthDataRecords(Tracker tracker, List<HealthDataRecord> records) {
        String json;
        try {
            json = mapper.writeValueAsString(records);
        } catch (JsonProcessingException e) {
            throw new BridgeSDKException(
                    "An error occurred while processing the List of HealthDataRecords. Are you sure it is correct?", e);
        }

        String trackerId = String.valueOf(tracker.getId());
        String url = config.getHealthDataTrackerApi(trackerId);
        HttpResponse response = post(url, json);

        JsonNode items = getPropertyFromResponse(response, "items");
        List<IdVersionHolder> holders = mapper.convertValue(items,
                mapper.getTypeFactory().constructCollectionType(List.class, SimpleIdVersionHolder.class));

        return holders;
    }
}
