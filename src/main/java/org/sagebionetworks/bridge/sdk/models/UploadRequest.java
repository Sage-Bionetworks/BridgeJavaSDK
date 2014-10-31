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

    public void setName(String name) {
        this.name = name;
    }
    public void setContentMd5(String contentMd5) {
        this.contentMd5 = contentMd5;
    }
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }
}
