package org.sagebionetworks.bridge.sdk.models.upload;

import java.util.Objects;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class UploadSession {

    private final String id;
    private final String url;
    private final DateTime expires;

    @JsonCreator
    UploadSession(@JsonProperty("id") String id, @JsonProperty("url") String url,
            @JsonProperty("expires") DateTime expires) {
        this.id = id;
        this.expires = expires;
        this.url = url;
    }

    public String getId() { return id; }
    public String getUrl() { return url; }
    public DateTime getExpires() { return expires; }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(expires);
        result = prime * result + Objects.hashCode(id);
        result = prime * result + Objects.hashCode(url);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        UploadSession other = (UploadSession) obj;
        return (Objects.equals(expires, other.expires) && Objects.equals(id, other.id) && Objects.equals(url, other.url));
    }

    @Override
    public String toString() {
        return String.format("UploadRequest [id=%s, url=%s, expires=%s]", id, url, expires);
    }
}
