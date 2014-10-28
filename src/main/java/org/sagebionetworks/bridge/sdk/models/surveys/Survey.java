package org.sagebionetworks.bridge.sdk.models.surveys;

import java.util.List;
import java.util.Objects;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

public class Survey {

    private final String guid;
    private final DateTime versionedOn;
    private final DateTime modifiedOn;
    private final long version;
    private final String name;
    private final String identifier;
    private final boolean published;
    private final List<SurveyQuestion> questions;

    @JsonCreator
    private Survey(@JsonProperty("guid") String guid, @JsonProperty("versionedOn") DateTime versionedOn,
            @JsonProperty("modifiedOn") DateTime modifiedOn, @JsonProperty("version") long version,
            @JsonProperty("name") String name, @JsonProperty("identifier") String identifier,
            @JsonProperty("published") boolean published, @JsonProperty("questions") List<SurveyQuestion> questions) {
        if (questions == null) {
            questions = Lists.newLinkedList();
        }

        this.guid = guid;
        this.versionedOn = versionedOn;
        this.modifiedOn = modifiedOn;
        this.version = version;
        this.name = name;
        this.identifier = identifier;
        this.published = published;
        this.questions = questions;
    }

    public static Survey valueOf(String guid, DateTime versionedOn, DateTime modifiedOn, long version, String name,
            String identifier, boolean published, List<SurveyQuestion> questions) {
        return new Survey(guid, versionedOn, modifiedOn, version, name, identifier, published, questions);
    }

    public String getGuid() { return guid; }
    public DateTime getVersionedOn() { return versionedOn; }
    public DateTime getModifiedOn() { return modifiedOn; }
    public Long getVersion() { return version; }
    public String getName() { return name; }
    public String getIdentifier() { return identifier; }
    public boolean isPublished() { return published; }
    public List<SurveyQuestion> getQuestions() { return questions; }

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
        return Objects.equals(this.guid, that.guid) &&
                Objects.equals(this.versionedOn, that.versionedOn) &&
                Objects.equals(this.modifiedOn, that.modifiedOn) &&
                Objects.equals(this.version, that.version) &&
                Objects.equals(this.name, that.name) &&
                Objects.equals(this.identifier, that.identifier) &&
                Objects.equals(this.published, that.published) &&
                Objects.equals(this.questions, that.questions);
    }

    @Override
    public String toString() {
        return "Survey [guid=" + guid + ", versionedOn=" + versionedOn + ", modifiedOn=" + modifiedOn + ", version="
                + version + ", name=" + name + ", identifier=" + identifier + ", published=" + published
                + ", questions=" + questions + "]";
    }

}
