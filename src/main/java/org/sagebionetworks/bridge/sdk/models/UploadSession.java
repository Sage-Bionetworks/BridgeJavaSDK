package org.sagebionetworks.bridge.sdk.models;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UploadSession {

    private final String id;
    private final String url;
    private final DateTime expires;

    @JsonCreator
    private UploadSession(@JsonProperty("id") String id, @JsonProperty("url") String url,
            @JsonProperty("expires") DateTime expires) {
        this.id = id;
        this.expires = expires;
        this.url = url;
    }

    public String getId() { return id; }
    public String getUrl() { return url; }
    public DateTime getExpires() { return expires; }

    @Override
    public String toString() {
        return "UploadRequest[ id=" + id + "url=" + url + ", expires=" + expires + "]";
    }
}
