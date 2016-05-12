package org.sagebionetworks.bridge.sdk.models.surveys;

import java.util.List;
import java.util.Objects;

import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.models.holders.GuidCreatedOnVersionHolder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.Lists;

@JsonDeserialize(as=Survey.class)
public final class Survey implements GuidCreatedOnVersionHolder {

    private String guid;
    private DateTime createdOn;
    private DateTime modifiedOn;
    private Long version;
    private String name;
    private String identifier;
    private boolean published;
    private List<SurveyElement> elements = Lists.newLinkedList();

    @JsonCreator
    private Survey(@JsonProperty("guid") String guid, @JsonProperty("createdOn") DateTime createdOn,
            @JsonProperty("modifiedOn") DateTime modifiedOn, @JsonProperty("version") Long version,
            @JsonProperty("name") String name, @JsonProperty("identifier") String identifier,
            @JsonProperty("published") boolean published, @JsonProperty("elements") List<SurveyElement> elements) {
        this.guid = guid;
        this.createdOn = createdOn;
        this.modifiedOn = modifiedOn;
        this.version = version;
        this.name = name;
        this.identifier = identifier;
        this.published = published;
        this.elements = elements;    
    }

    public Survey() {
    }
    
    public void setGuidCreatedOnVersionHolder(GuidCreatedOnVersionHolder keys) {
    	this.guid = keys.getGuid();
    	this.createdOn = keys.getCreatedOn();
    	this.version = keys.getVersion();
    }

    @Override
    public String getGuid() {
        return guid;
    }
    
    public DateTime getCreatedOn() {
        return createdOn;
    }

    public DateTime getModifiedOn() {
        return modifiedOn;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public boolean isPublished() {
        return published;
    }

    public List<SurveyElement> getElements() {
        return elements;
    }

    public SurveyElement getElementByIdentifier(String identifier) {
        for (SurveyElement element : elements) {
            if (element.getIdentifier().equals(identifier)) {
                return element;
            }
        }
        return null;
    }

    public SurveyElement getElementByGUID(String guid) {
        for (SurveyElement element : elements) {
            if (element.getGuid().equals(guid)) {
                return element;
            }
        }
        return null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(createdOn);
        result = prime * result + Objects.hashCode(elements);
        result = prime * result + Objects.hashCode(guid);
        result = prime * result + Objects.hashCode(identifier);
        result = prime * result + Objects.hashCode(modifiedOn);
        result = prime * result + Objects.hashCode(name);
        result = prime * result + Objects.hashCode(version);
        result = prime * result + (published ? 1231 : 1237);
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Survey that = (Survey) obj;
        return Objects.equals(this.guid, that.guid) && Objects.equals(this.createdOn, that.createdOn)
                && Objects.equals(this.modifiedOn, that.modifiedOn) && Objects.equals(this.version, that.version)
                && Objects.equals(this.name, that.name) && Objects.equals(this.identifier, that.identifier)
                && Objects.equals(this.published, that.published) && Objects.equals(this.elements, that.elements);
    }

    @Override
    public String toString() {
        return String.format("Survey [guid=%s, createdOn=%s, modifiedOn=%s, version=%s, name=%s, identifier=%s, published=%s, elements=%s]", 
                guid, createdOn, modifiedOn, version, name, identifier, published, elements);
    }

}
