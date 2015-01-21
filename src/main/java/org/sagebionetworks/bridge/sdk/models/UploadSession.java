package org.sagebionetworks.bridge.sdk.models;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UploadSession {

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
        result = prime * result + ((expires == null) ? 0 : expires.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UploadSession other = (UploadSession) obj;
        if (expires == null) {
            if (other.expires != null)
                return false;
        } else if (!expires.equals(other.expires))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (url == null) {
            if (other.url != null)
                return false;
        } else if (!url.equals(other.url))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "UploadRequest[ id=" + id + "url=" + url + ", expires=" + expires + "]";
    }
}
