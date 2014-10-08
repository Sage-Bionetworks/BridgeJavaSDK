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
import com.fasterxml.jackson.databind.node.ObjectNode;

public class HealthDataApiCaller extends BaseApiCaller {

    private final String HEALTH_DATA = provider.getConfig().getHealthDataApi();


    private HealthDataApiCaller(ClientProvider provider) {
        super(provider);
    }

    static HealthDataApiCaller valueOf(ClientProvider provider) {
        return new HealthDataApiCaller(provider);
    }

    HealthDataRecord getHealthDataRecord(Tracker tracker, long recordId) {
        assert provider.isSignedIn();
        assert tracker != null && recordId >= 0;

        String url = getFullUrl(HEALTH_DATA) + trackerId(tracker.getId()) + recordId(recordId);
        HttpResponse response = authorizedGet(url);
        String responseBody = getResponseBody(response);

        return HealthDataRecord.valueOf(responseBody);
    }

    IdVersionHolder updateHealthDataRecord(Tracker tracker, HealthDataRecord record) {
        assert provider.isSignedIn();
        assert tracker != null && record != null;

        String url = getFullUrl(HEALTH_DATA) + trackerId(tracker.getId()) + recordId(record.getId());
        String json = null;
        try {
            json = mapper.writeValueAsString(record);
        } catch (JsonProcessingException e) {
            throw new BridgeSDKException("Could not process HealthDataRecord. Are you sure it is correct? "
                    + record.toString(), e);
        }
        HttpResponse response = post(url, json);
        String responseBody = getResponseBody(response);

        return IdVersionHolder.valueOf(responseBody);
    }

    boolean deleteHealthDataRecord(Tracker tracker, long recordId) {
        assert provider.isSignedIn();
        assert tracker != null;

        String url = getFullUrl(HEALTH_DATA) + trackerId(tracker.getId()) + recordId(recordId);
        HttpResponse response = delete(url);

        return getPropertyFromResponse(response, "message").equals("Entry deleted.") ? true : false;
    }

    List<HealthDataRecord> getHealthDataRecordsInRange(Tracker tracker, DateTime startDate, DateTime endDate) {
        assert provider.isSignedIn();
        assert tracker != null && startDate != null && endDate != null;
        assert startDate.isBefore(endDate);

        Map<String,String> queryParameters = new HashMap<String,String>();
        queryParameters.put("startDate", startDate.toString(ISODateTimeFormat.dateTime()));
        queryParameters.put("endDate", endDate.toString(ISODateTimeFormat.dateTime()));

        String url = getFullUrl(HEALTH_DATA) + addQueryParameters(queryParameters);
        HttpResponse response = authorizedGet(url);

        String items = getPropertyFromResponse(response, "items");
        List<HealthDataRecord> records = mapper.convertValue(items,
                mapper.getTypeFactory().constructCollectionType(List.class, HealthDataRecord.class));

        return records;
    }

    List<IdVersionHolder> addHealthDataRecords(Tracker tracker, List<HealthDataRecord> records) {
        assert provider.isSignedIn();
        assert tracker != null;
        assert records != null && records.size() > 0;

        ObjectNode json = mapper.createObjectNode();
        try {
            json.put("items", mapper.writeValueAsString(records));
            json.put("size", records.size());
        } catch (JsonProcessingException e) {
            throw new BridgeSDKException(
                    "An error occurred while processing the List of HealthDataRecords. Are you sure it is correct?", e);
        }

        String url = getFullUrl(HEALTH_DATA) + trackerId(tracker.getId());
        HttpResponse response = post(url, json.asText());

        String items = getPropertyFromResponse(response, "items");
        List<IdVersionHolder> holders = mapper.convertValue(items,
                mapper.getTypeFactory().constructCollectionType(List.class, IdVersionHolder.class));

        return holders;
    }

    private String trackerId(long trackerId) {
        return "/" + String.valueOf(trackerId);
    }

    private String recordId(long recordId) {
        return "/" + "record" + "/" + String.valueOf(recordId);
    }
}
