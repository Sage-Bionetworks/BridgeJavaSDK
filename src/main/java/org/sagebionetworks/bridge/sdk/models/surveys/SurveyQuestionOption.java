package org.sagebionetworks.bridge.sdk.models.surveys;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SurveyQuestionOption {
    
    private final String label;
    private final String detail;
    private final String value;
    private final Image image;

    @JsonCreator
    public SurveyQuestionOption(@JsonProperty("label") String label, @JsonProperty("detail") String detail, 
        @JsonProperty("value") String value, @JsonProperty("image") Image image) {
        this.label = label;
        this.detail = detail;
        this.value = value;
        this.image = image;
    }
    public SurveyQuestionOption(String label, String detail, String value) {
        this(label, detail, value, null);
    }
    public SurveyQuestionOption(String label, String value) {
        this(label, null, value, null);
    }
    public SurveyQuestionOption(String label) {
        this(label, null, label, null);
    }
    public String getLabel() {
        return label;
    }
    public String getDetail() {
        return detail;
    }
    public String getValue() {
        return value;
    }
    public Image getImage() {
        return image;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(image);
        result = prime * result + Objects.hashCode(detail);
        result = prime * result + Objects.hashCode(label);
        result = prime * result + Objects.hashCode(value);
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        SurveyQuestionOption other = (SurveyQuestionOption) obj;
        return (Objects.equals(image, other.image) && Objects.equals(detail, other.detail) 
                && Objects.equals(label, other.label) && Objects.equals(value, other.value));
    }
    @Override
    public String toString() {
        return String.format("SurveyQuestionOption [label=%s, value=%s, detail=%s, image=%s]", label, value, detail, image);
    }
}
