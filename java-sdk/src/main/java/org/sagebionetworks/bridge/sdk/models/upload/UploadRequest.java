package org.sagebionetworks.bridge.sdk.models.upload;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.io.Files;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import org.sagebionetworks.bridge.sdk.exceptions.InvalidEntityException;

/** Represents an upload request to the Bridge Server. */
@JsonDeserialize(builder = UploadRequest.Builder.class)
public final class UploadRequest {
    private final String name;
    private final long contentLength;
    private final String contentMd5;
    private final String contentType;

    /** Private constructor. Construction of an UploadRequest should go through the Builder. */
    private UploadRequest(String name, long contentLength, String contentMd5, String contentType) {
        this.name = name;
        this.contentLength = contentLength;
        this.contentMd5 = contentMd5;
        this.contentType = contentType;
    }

    /** File name, always non-null and non-empty. */
    public String getName() {
        return this.name;
    }

    /** File hash, as a string with the Base64-encoding of the file's MD5 hash. Always non-null and non-empty. */
    public String getContentMd5() {
        return this.contentMd5;
    }

    /** File's MIME type, always non-null and non-empty. */
    public String getContentType() {
        return this.contentType;
    }

    /** File's length in bytes, always non-negative. */
    public long getContentLength() {
        return this.contentLength;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(contentLength);
        result = prime * result + Objects.hashCode(contentMd5);
        result = prime * result + Objects.hashCode(contentType);
        result = prime * result + Objects.hashCode(name);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        UploadRequest other = (UploadRequest) obj;
        return Objects.equals(contentLength, other.contentLength) && Objects.equals(contentMd5, other.contentMd5)
                && Objects.equals(contentType, other.contentType) && Objects.equals(name, other.name);
    }

    @Override
    public String toString() {
        return String.format("UploadRequest [name=%s, contentMd5=%s, contentType=%s, contentLength=%s]", 
                name, contentMd5, contentType, contentLength);
    }

    /** Builder for UploadRequest. */
    public static class Builder {
        private String name;
        private long contentLength;
        private String contentMd5;
        private String contentType;

        /** Sets the name, content length, and content MD5 from the given file. This does not set the content type. */
        public Builder withFile(File file) throws IOException {
            name = file.getName();
            contentLength = file.length();

            byte[] fileBytes = Files.toByteArray(file);
            contentMd5 = Base64.encodeBase64String(DigestUtils.md5(fileBytes));

            return this;
        }

        /** @see org.sagebionetworks.bridge.sdk.models.upload.UploadRequest#getName */
        public String getName() {
            return name;
        }

        /** @see org.sagebionetworks.bridge.sdk.models.upload.UploadRequest#getName */
        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        /** @see org.sagebionetworks.bridge.sdk.models.upload.UploadRequest#getContentLength */
        public long getContentLength() {
            return contentLength;
        }

        /** @see org.sagebionetworks.bridge.sdk.models.upload.UploadRequest#getContentLength */
        public Builder withContentLength(long contentLength) {
            this.contentLength = contentLength;
            return this;
        }

        /** @see org.sagebionetworks.bridge.sdk.models.upload.UploadRequest#getContentMd5 */
        public String getContentMd5() {
            return contentMd5;
        }

        /** @see org.sagebionetworks.bridge.sdk.models.upload.UploadRequest#getContentMd5 */
        public Builder withContentMd5(String contentMd5) {
            this.contentMd5 = contentMd5;
            return this;
        }

        /** @see org.sagebionetworks.bridge.sdk.models.upload.UploadRequest#getContentType */
        public String getContentType() {
            return contentType;
        }

        /** @see org.sagebionetworks.bridge.sdk.models.upload.UploadRequest#getContentType */
        public Builder withContentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        /**
         * Builds and validates an UploadRequest. This validates that name is non-null and non-empty, content length is
         * non-negative, content MD5 is non-null and non-empty, and content type is non-null and non-empty. This method
         * does not validate that the named file exists or that its length and MD5 are accurate. To ensure account
         * length and MD5, use {@link #withFile}.
         *
         * @return validated UploadRequest
         * @throws InvalidEntityException
         *         if called with invalid fields
         */
        public UploadRequest build() throws InvalidEntityException {
            if (StringUtils.isBlank(name)) {
                throw new InvalidEntityException("name cannot be blank");
            }
            if (contentLength < 0) {
                throw new InvalidEntityException("content length cannot be negative");
            }
            if (StringUtils.isBlank(contentMd5)) {
                throw new InvalidEntityException("contentMd5 cannot be blank");
            }
            if (StringUtils.isBlank(contentType)) {
                throw new InvalidEntityException("contentType cannot be blank");
            }

            return new UploadRequest(name, contentLength, contentMd5, contentType);
        }
    }
}
