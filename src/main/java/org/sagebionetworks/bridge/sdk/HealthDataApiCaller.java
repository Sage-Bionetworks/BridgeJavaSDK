package org.sagebionetworks.bridge.sdk;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.sagebionetworks.bridge.sdk.models.HealthDataRecord;
import org.sagebionetworks.bridge.sdk.models.IdVersionHolder;
import org.sagebionetworks.bridge.sdk.models.Tracker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

class HealthDataApiCaller extends BaseApiCaller {

    private HealthDataApiCaller(ClientProvider provider) {
        super(provider);
    }

    static HealthDataApiCaller valueOf(ClientProvider provider) {
        return new HealthDataApiCaller(provider);
    }

    HealthDataRecord getHealthDataRecord(Tracker tracker, String recordId) {
        String trackerId = String.valueOf(tracker.getId());
        String url = provider.getConfig().getHealthDataTrackerRecordApi(trackerId, recordId);
        HttpResponse response = get(url);

        return getResponseBodyAsType(response, HealthDataRecord.class);
    }

    IdVersionHolder updateHealthDataRecord(Tracker tracker, HealthDataRecord record) {
        String json = null;
        try {
            json = mapper.writeValueAsString(record);
        } catch (JsonProcessingException e) {
            throw new BridgeSDKException("Could not process HealthDataRecord. Are you sure it is correct? "
                    + record.toString(), e);
        }
        String trackerId = String.valueOf(tracker.getId());
        String url = provider.getConfig().getHealthDataTrackerRecordApi(trackerId, record.getRecordId());
        HttpResponse response = post(url, json);

        return getResponseBodyAsType(response, IdVersionHolder.class);
    }

    void deleteHealthDataRecord(Tracker tracker, String recordId) {
        String trackerId = String.valueOf(tracker.getId());
        String url = provider.getConfig().getHealthDataTrackerRecordApi(trackerId, recordId);
        delete(url);
    }

    List<HealthDataRecord> getHealthDataRecordsInRange(Tracker tracker, DateTime startDate, DateTime endDate) {
        Map<String,String> queryParameters = new HashMap<String,String>();
        queryParameters.put("startDate", startDate.toString(ISODateTimeFormat.dateTime()));
        queryParameters.put("endDate", endDate.toString(ISODateTimeFormat.dateTime()));

        String trackerId = String.valueOf(tracker.getId());
        String url = provider.getConfig().getHealthDataTrackerApi(trackerId);
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
        String url = provider.getConfig().getHealthDataTrackerApi(trackerId);
        HttpResponse response = post(url, json);

        JsonNode items = getPropertyFromResponse(response, "items");
        List<IdVersionHolder> holders = mapper.convertValue(items,
                mapper.getTypeFactory().constructCollectionType(List.class, IdVersionHolder.class));

        return holders;
    }
}
