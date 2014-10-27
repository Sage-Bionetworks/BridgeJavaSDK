package org.sagebionetworks.bridge.sdk;

import java.util.List;

import org.apache.http.HttpResponse;
import org.sagebionetworks.bridge.sdk.models.Tracker;

import com.fasterxml.jackson.databind.JsonNode;

class TrackerApiCaller extends BaseApiCaller {

    private TrackerApiCaller(ClientProvider provider) {
        super(provider);
    }

    static TrackerApiCaller valueOf(ClientProvider provider) {
        return new TrackerApiCaller(provider);
    }

    List<Tracker> getAllTrackers() {
        String url = provider.getConfig().getTrackerApi();
        HttpResponse response = get(url);
        JsonNode items = getPropertyFromResponse(response, "items");
        List<Tracker> trackers = mapper.convertValue(items,
                mapper.getTypeFactory().constructCollectionType(List.class, Tracker.class));

        return trackers;
    }

    String getSchema(Tracker tracker) {
        String url = tracker.getSchemaUrl();
        HttpResponse response = get(url);
        String schema = getResponseBody(response);

        return schema;
    }

}
