package org.sagebionetworks.bridge.sdk.models.surveys;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Image {
    private final String source;
    private final int width;
    private final int height;

    @JsonCreator
    public Image(@JsonProperty("source") String source, @JsonProperty("width") int width,
            @JsonProperty("height") int height) {
        this.source = source;
        this.width = width;
        this.height = height;
    }

    public String getSource() {
        return source;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + height;
        result = prime * result + ((source == null) ? 0 : source.hashCode());
        result = prime * result + width;
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
        Image other = (Image) obj;
        return (Objects.equals(height, other.height) && Objects.equals(width, other.width) && Objects.equals(source,
                other.source));
    }

    @Override
    public String toString() {
        return "Image [source=" + source + ", width=" + width + ", height=" + height + "]";
    }

}
