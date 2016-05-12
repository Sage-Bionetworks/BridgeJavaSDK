package org.sagebionetworks.bridge.sdk.models.subpopulations;

import static org.sagebionetworks.bridge.sdk.utils.Utilities.TO_STRING_STYLE;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;

import org.sagebionetworks.bridge.sdk.json.SubpopulationGuidDeserializer;
import org.sagebionetworks.bridge.sdk.models.Criteria;
import org.sagebionetworks.bridge.sdk.models.holders.GuidVersionHolder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public final class Subpopulation {
    
    private String name;
    private String description;
    @JsonDeserialize(using=SubpopulationGuidDeserializer.class)
    private SubpopulationGuid guid;
    private boolean required;
    private boolean defaultGroup;
    private Long version;
    private Criteria criteria;
    
    public void setHolder(GuidVersionHolder holder) {
        checkNotNull(holder);
        this.guid = new SubpopulationGuid(holder.getGuid());
        this.version = holder.getVersion();
    }
    public SubpopulationGuid getGuid() {
        return guid;
    }
    public void setGuid(SubpopulationGuid guid) {
        this.guid = guid;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    /** 
     * Does this subpopulation have a consent that must be agreed to by the participant in 
     * order to access the study?
     * @return
     */
    public boolean isRequired() {
        return required;
    }
    public void setRequired(boolean required) {
        this.required = required;
    }
    /**
     * Is this the default subpopulation provided with every study (this subpopulation cannot
     * be deleted). This value is readonly.
     * @return
     */
    public boolean isDefaultGroup() {
        return defaultGroup;
    }
    void setDefaultGroup(boolean defaultGroup) {
        this.defaultGroup = defaultGroup;
    }
    /**
     * If a criteria object is set for this subpopulation, it will only match requests that meet the criteria. 
     * For users that do not fit the criteria, the subpopulation does not exist, and any requirements it places 
     * on consent do not apply to that participant. 
     */
    public Criteria getCriteria() {
        return criteria;
    }
    public void setCriteria(Criteria criteria) {
        this.criteria = criteria;
    }
    public Long getVersion() {
        return version;
    }
    public void setVersion(Long version) {
        this.version = version;
    }
    @Override
    public int hashCode() {
        return Objects.hash(criteria, description, guid, name, required, defaultGroup, version);
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Subpopulation other = (Subpopulation) obj;
        return Objects.equals(description, other.description)
                && Objects.equals(criteria, other.criteria)
                && Objects.equals(guid, other.guid)
                && Objects.equals(name, other.name)
                && Objects.equals(required, other.required) && Objects.equals(defaultGroup, other.defaultGroup) 
                && Objects.equals(version, other.version);
    }
    @Override
    public String toString() {
        return new ToStringBuilder(this, TO_STRING_STYLE).append("name", name).append("description", description)
                .append("guid", guid).append("required", required).append("defaultGroup", defaultGroup)
                .append("version", version).append("criteria", criteria).build();
    }
}
