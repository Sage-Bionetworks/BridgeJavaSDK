package org.sagebionetworks.bridge.sdk;

import java.util.List;

import org.apache.http.HttpResponse;
import org.sagebionetworks.bridge.sdk.models.Tracker;

import com.fasterxml.jackson.databind.JsonNode;

class TrackerApiCaller extends BaseApiCaller {

    private final String TRACKER = provider.getConfig().getTrackerApi();

    private TrackerApiCaller(ClientProvider provider) {
        super(provider);
    }

    static TrackerApiCaller valueOf(ClientProvider provider) {
        return new TrackerApiCaller(provider);
    }

    List<Tracker> getAllTrackers() {
        assert provider.isSignedIn();

        HttpResponse response = get(TRACKER);
        JsonNode items = getPropertyFromResponse(response, "items");
        List<Tracker> trackers = mapper.convertValue(items,
                mapper.getTypeFactory().constructCollectionType(List.class, Tracker.class));

        return trackers;
    }

    String getSchema(Tracker tracker) {
        assert provider.isSignedIn();
        assert tracker != null;

        String url = tracker.getSchemaUrl(); // remove beginning /
        HttpResponse response = get(url);
        String schema = getResponseBody(response);

        return schema;
    }

}
