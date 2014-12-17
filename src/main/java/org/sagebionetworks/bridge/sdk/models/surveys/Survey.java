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
public class Survey implements GuidCreatedOnVersionHolder {

    private String guid;
    private DateTime createdOn;
    private DateTime modifiedOn;
    private Long version;
    private String name;
    private String identifier;
    private boolean published;
    private List<SurveyQuestion> questions = Lists.newLinkedList();

    @JsonCreator
    private Survey(@JsonProperty("guid") String guid, @JsonProperty("createdOn") DateTime createdOn,
            @JsonProperty("modifiedOn") DateTime modifiedOn, @JsonProperty("version") Long version,
            @JsonProperty("name") String name, @JsonProperty("identifier") String identifier,
            @JsonProperty("published") boolean published, @JsonProperty("questions") List<SurveyQuestion> questions) {
        this.guid = guid;
        this.createdOn = createdOn;
        this.modifiedOn = modifiedOn;
        this.version = version;
        this.name = name;
        this.identifier = identifier;
        this.published = published;
        this.questions = questions;
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

    public List<SurveyQuestion> getQuestions() {
        return questions;
    }

    public SurveyQuestion getQuestionByIdentifier(String identifier) {
        for (SurveyQuestion question : questions) {
            if (question.getIdentifier().equals(identifier)) {
                return question;
            }
        }
        return null;
    }

    public SurveyQuestion getQuestionByGUID(String guid) {
        for (SurveyQuestion question : questions) {
            if (question.getGuid().equals(guid)) {
                return question;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (getClass() != obj.getClass()) {
            return false;
        }
        final Survey that = (Survey) obj;
        return Objects.equals(this.guid, that.guid) && Objects.equals(this.createdOn, that.createdOn)
                && Objects.equals(this.modifiedOn, that.modifiedOn) && Objects.equals(this.version, that.version)
                && Objects.equals(this.name, that.name) && Objects.equals(this.identifier, that.identifier)
                && Objects.equals(this.published, that.published) && Objects.equals(this.questions, that.questions);
    }

    @Override
    public String toString() {
        return "Survey [guid=" + guid + ", createdOn=" + createdOn + ", modifiedOn=" + modifiedOn + ", version="
                + version + ", name=" + name + ", identifier=" + identifier + ", published=" + published
                + ", questions=" + questions + "]";
    }

}
