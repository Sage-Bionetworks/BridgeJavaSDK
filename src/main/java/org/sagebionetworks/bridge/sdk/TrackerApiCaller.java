package org.sagebionetworks.bridge.sdk;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.fluent.Response;
import org.apache.http.util.EntityUtils;
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


        Response response = authorizedGet(TRACKER);
        StatusLine statusLine = null;
        JsonNode json = null;
        try {
            HttpResponse hr = response.returnResponse();
            statusLine = hr.getStatusLine();
            String jsonString = EntityUtils.toString(hr.getEntity());

            json = mapper.readTree(jsonString);
        } catch (IOException e) {
            throw new BridgeServerException(e, statusLine, getFullUrl(TRACKER));
        }

        assert json != null : "JSON cannot be null, earlier HTTP call failed.";
        JsonNode items = json.get("items");
        List<Tracker> trackers = mapper.convertValue(items,
                mapper.getTypeFactory().constructCollectionType(List.class, Tracker.class));

        return trackers;
    }

    String getSchema(Tracker tracker) {
        assert provider.isSignedIn();
        assert tracker != null;

        Response response = authorizedGet(tracker.getSchemaUrl());
        StatusLine statusLine = null;
        String schema = null;
        try {
            HttpResponse hr = response.returnResponse();
            statusLine = hr.getStatusLine();
            schema = EntityUtils.toString(hr.getEntity());
        } catch (IOException e) {
            throw new BridgeServerException(e, statusLine, getFullUrl(TRACKER));
        }

        return schema;
    }

}
