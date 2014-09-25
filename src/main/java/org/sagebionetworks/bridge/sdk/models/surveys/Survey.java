package org.sagebionetworks.bridge.sdk.models.surveys;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.Lists;

@JsonIgnoreProperties(value={"type"})
public class Survey {

    private String guid;
    private long versionedOn;
    private long modifiedOn;
    private Long version;
    private String name;
    private String identifier;
    private boolean published;
    private List<SurveyQuestion> questions;

    public Survey() {
        questions = Lists.newLinkedList();
    }
    
    public String getGuid() {
        return guid;
    }
    public void setGuid(String guid) {
        this.guid = guid;
    }
    public long getVersionedOn() {
        return versionedOn;
    }
    @JsonDeserialize(using = DateTimeDeserializer.class)
    public void setVersionedOn(long versionedOn) {
        this.versionedOn = versionedOn;
    }
    public long getModifiedOn() {
        return modifiedOn;
    }
    @JsonDeserialize(using = DateTimeDeserializer.class)
    public void setModifiedOn(long modifiedOn) {
        this.modifiedOn = modifiedOn;
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
    public void setPublished(boolean published) {
        this.published = published;
    }
    public List<SurveyQuestion> getQuestions() {
        return questions;
    }
    public void setQuestions(List<SurveyQuestion> questions) {
        this.questions = questions;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((guid == null) ? 0 : guid.hashCode());
        result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
        result = prime * result + (int) (modifiedOn ^ (modifiedOn >>> 32));
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + (published ? 1231 : 1237);
        result = prime * result + ((questions == null) ? 0 : questions.hashCode());
        result = prime * result + ((version == null) ? 0 : version.hashCode());
        result = prime * result + (int) (versionedOn ^ (versionedOn >>> 32));
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
        Survey other = (Survey) obj;
        if (guid == null) {
            if (other.guid != null)
                return false;
        } else if (!guid.equals(other.guid))
            return false;
        if (identifier == null) {
            if (other.identifier != null)
                return false;
        } else if (!identifier.equals(other.identifier))
            return false;
        if (modifiedOn != other.modifiedOn)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (published != other.published)
            return false;
        if (questions == null) {
            if (other.questions != null)
                return false;
        } else if (!questions.equals(other.questions))
            return false;
        if (version == null) {
            if (other.version != null)
                return false;
        } else if (!version.equals(other.version))
            return false;
        if (versionedOn != other.versionedOn)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Survey [guid=" + guid + ", versionedOn=" + versionedOn + ", modifiedOn="
                + modifiedOn + ", version=" + version + ", name=" + name + ", identifier=" + identifier
                + ", published=" + published + ", questions=" + questions + "]";
    }
    
}
