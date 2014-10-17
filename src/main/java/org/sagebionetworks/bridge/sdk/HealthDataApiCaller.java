package org.sagebionetworks.bridge.sdk;

import java.io.IOException;
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

    private final String HEALTH_DATA = provider.getConfig().getHealthDataApi();


    private HealthDataApiCaller(ClientProvider provider) {
        super(provider);
    }

    static HealthDataApiCaller valueOf(ClientProvider provider) {
        return new HealthDataApiCaller(provider);
    }

    HealthDataRecord getHealthDataRecord(Tracker tracker, String recordId) {
        assert provider.isSignedIn();
        assert tracker != null && recordId != null;

        String url = HEALTH_DATA + trackerId(tracker) + recordId(recordId);
        HttpResponse response = get(url);
        String responseBody = getResponseBody(response);

        HealthDataRecord record = null;
        try {
            record = mapper.readValue(responseBody, HealthDataRecord.class);
        } catch (IOException e) {
            throw new BridgeSDKException(
                    "Something went wrong while converting Response Body JSON into HealthDataRecord: json="
                            + responseBody, e);
        }
        return record;
    }

    IdVersionHolder updateHealthDataRecord(Tracker tracker, HealthDataRecord record) {
        assert provider.isSignedIn();
        assert tracker != null && record != null;

        String url = HEALTH_DATA + trackerId(tracker) + recordId(record.getRecordId());
        String json = null;
        try {
            json = mapper.writeValueAsString(record);
        } catch (JsonProcessingException e) {
            throw new BridgeSDKException("Could not process HealthDataRecord. Are you sure it is correct? "
                    + record.toString(), e);
        }
        HttpResponse response = post(url, json);
        String responseBody = getResponseBody(response);

        IdVersionHolder holder;
        try {
            holder = mapper.readValue(responseBody, IdVersionHolder.class);
        } catch (IOException e) {
            throw new BridgeSDKException(
                    "Something went wrong while converting Response Body JSON into IdVersionHolder: responseBody="
                            + responseBody, e);
        }
        return holder;
    }

    void deleteHealthDataRecord(Tracker tracker, String recordId) {
        assert provider.isSignedIn();
        assert tracker != null;

        String url = HEALTH_DATA + trackerId(tracker) + recordId(recordId);
        delete(url);
    }

    List<HealthDataRecord> getHealthDataRecordsInRange(Tracker tracker, DateTime startDate, DateTime endDate) {
        assert provider.isSignedIn();
        assert tracker != null && startDate != null && endDate != null;
        assert startDate.isBefore(endDate);

        Map<String,String> queryParameters = new HashMap<String,String>();
        queryParameters.put("startDate", startDate.toString(ISODateTimeFormat.dateTime()));
        queryParameters.put("endDate", endDate.toString(ISODateTimeFormat.dateTime()));

        String url = HEALTH_DATA + trackerId(tracker);
        HttpResponse response = get(url, queryParameters);

        JsonNode items = getPropertyFromResponse(response, "items");
        List<HealthDataRecord> records = mapper.convertValue(items,
                mapper.getTypeFactory().constructCollectionType(List.class, HealthDataRecord.class));

        return records;
    }

    List<IdVersionHolder> addHealthDataRecords(Tracker tracker, List<HealthDataRecord> records) {
        assert provider.isSignedIn();
        assert tracker != null;
        assert records != null && records.size() > 0;

        String json;
        try {
            json = mapper.writeValueAsString(records);
        } catch (JsonProcessingException e) {
            throw new BridgeSDKException(
                    "An error occurred while processing the List of HealthDataRecords. Are you sure it is correct?", e);
        }

        String url = HEALTH_DATA + trackerId(tracker);
        HttpResponse response = post(url, json);

        JsonNode items = getPropertyFromResponse(response, "items");
        List<IdVersionHolder> holders = mapper.convertValue(items,
                mapper.getTypeFactory().constructCollectionType(List.class, IdVersionHolder.class));

        return holders;
    }

    private String trackerId(Tracker tracker) {
        return "/" + String.valueOf(tracker.getId());
    }

    private String recordId(String recordId) {
        return "/" + "record" + "/" + recordId;
    }
}
