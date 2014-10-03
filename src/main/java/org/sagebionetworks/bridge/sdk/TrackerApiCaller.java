package org.sagebionetworks.bridge.sdk;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.fluent.Response;
import org.sagebionetworks.bridge.sdk.models.Tracker;

import com.fasterxml.jackson.databind.JsonNode;

class TrackerApiCaller extends BaseApiCaller {

    private static final String TRACKER = "/api/v1/trackers";

    private TrackerApiCaller(ClientProvider provider) {
        super(provider);
    }

    static TrackerApiCaller valueOf(ClientProvider provider) {
        return new TrackerApiCaller(provider);
    }

    List<Tracker> getAllTrackers() {
        assert provider.isSignedIn();

        Response response = authorizedGet(TRACKER);
        JsonNode json = null;
        try {
            String jsonString = response.returnContent().asString();
            json = mapper.readTree(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert json != null;
        JsonNode items = json.get("items");
        List<Tracker> trackers = mapper.convertValue(items,
                mapper.getTypeFactory().constructCollectionType(List.class, Tracker.class));

        return trackers;
    }

    String getSchema(Tracker tracker) {
        assert provider.isSignedIn();
        assert tracker != null;

        Response response = authorizedGet(tracker.getSchemaUrl());
        String schema = null;
        try {
            schema = response.returnContent().asString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return schema;
    }

}
