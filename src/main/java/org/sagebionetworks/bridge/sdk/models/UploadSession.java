package org.sagebionetworks.bridge.sdk.models;

import java.net.MalformedURLException;
import java.net.URL;

import org.sagebionetworks.bridge.sdk.BridgeSDKException;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UploadSession {

    private final String id;
    private final URL url;
    private final long expires;

    private UploadSession(@JsonProperty("id") String id, @JsonProperty("url") String url, @JsonProperty("expires") long expires) {
        this.id = id;
        this.expires = expires;

        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            throw new BridgeSDKException("URL is malformed: url=" + url, e);
        }
    }

    public String getId() { return id; }
    public URL getURL() { return url; }
    public long getExpires() { return expires; }
}
