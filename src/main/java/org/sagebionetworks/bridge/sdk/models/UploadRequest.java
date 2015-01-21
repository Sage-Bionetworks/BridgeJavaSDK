package org.sagebionetworks.bridge.sdk.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UploadRequest {

    private String name;
    private long contentLength;
    private String contentMd5;
    private String contentType;

    @JsonCreator
    private UploadRequest(@JsonProperty("name") String name, @JsonProperty("contentLength") long contentLength,
            @JsonProperty("contentMd5") String contentMd5, @JsonProperty("contentType") String contentType) {
        this.name = name;
        this.contentLength = contentLength;
        this.contentMd5 = contentMd5;
        this.contentType = contentType;
    }

    public UploadRequest() {
    }

    public String getName() { return this.name; }
    public String getContentMd5() { return this.contentMd5; }
    public String getContentType() { return this.contentType; }
    public long getContentLength() { return this.contentLength; }

    public UploadRequest setName(String name) {
        this.name = name;
        return this;
    }
    public UploadRequest setContentMd5(String contentMd5) {
        this.contentMd5 = contentMd5;
        return this;
    }
    public UploadRequest setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }
    public UploadRequest setContentLength(long contentLength) {
        this.contentLength = contentLength;
        return this;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (contentLength ^ (contentLength >>> 32));
        result = prime * result + ((contentMd5 == null) ? 0 : contentMd5.hashCode());
        result = prime * result + ((contentType == null) ? 0 : contentType.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        UploadRequest other = (UploadRequest) obj;
        if (contentLength != other.contentLength)
            return false;
        if (contentMd5 == null) {
            if (other.contentMd5 != null)
                return false;
        } else if (!contentMd5.equals(other.contentMd5))
            return false;
        if (contentType == null) {
            if (other.contentType != null)
                return false;
        } else if (!contentType.equals(other.contentType))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "UploadRequest[ name=" + name +
                "contentMd5=" + contentMd5 +
                ", contentType=" + contentType +
                ", contentLength=" + contentLength + "]";
    }
}
