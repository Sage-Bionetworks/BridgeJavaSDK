package org.sagebionetworks.bridge.sdk.models.subpopulations;

import static org.sagebionetworks.bridge.sdk.utils.Utilities.TO_STRING_STYLE;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;

import org.sagebionetworks.bridge.sdk.models.holders.GuidVersionHolder;

import com.google.common.collect.ImmutableSet;

public final class Subpopulation implements SubpopulationGuid {
    
    private String name;
    private String description;
    private String guid;
    private boolean required;
    private boolean defaultGroup;
    private Long version;
    private Integer minAppVersion;
    private Integer maxAppVersion;
    private Set<String> allOfGroups;
    private Set<String> noneOfGroups;
    
    public Subpopulation() {
        // This is different than elsewhere in the SDK...
        this.allOfGroups = ImmutableSet.of();
        this.noneOfGroups = ImmutableSet.of();
    }
    
    public void setHolder(GuidVersionHolder holder) {
        checkNotNull(holder);
        this.guid = holder.getGuid();
        this.version = holder.getVersion();
    }
    
    public String getGuid() {
        return guid;
    }
    public void setGuid(String guid) {
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
     * The minimum application version that can be submitted by the participant's application 
     * that will be considered part of this subpopulation. When combined with maxAppVersion, 
     * this defines a required range of versions.  
     * @return
     */
    public Integer getMinAppVersion() {
        return minAppVersion;
    }
    /**
     * The maximum application version that can be submitted by the participant's application 
     * that will be considered part of this subpopulation. When combined with minAppVersion, 
     * this defines a required range of versions. 
     * @return
     */
    public void setMinAppVersion(Integer minAppVersion) {
        this.minAppVersion = minAppVersion;
    }
    public Integer getMaxAppVersion() {
        return maxAppVersion;
    }
    public void setMaxAppVersion(Integer maxAppVersion) {
        this.maxAppVersion = maxAppVersion;
    }
    public Long getVersion() {
        return version;
    }
    public void setVersion(Long version) {
        this.version = version;
    }
    /**
     * If set (not null and not empty), the user must have <i>all</i> of the indicated data group tags to be 
     * considered a member of this subpopulation. 
     * @param allOfGroups
     */
    public Set<String> getAllOfGroups() {
        return allOfGroups;
    }
    public void setAllOfGroups(Set<String> allOfGroups) {
        this.allOfGroups = (allOfGroups == null) ? ImmutableSet.<String>of() : ImmutableSet.copyOf(allOfGroups);
    }
    /**
     * If set (not null and not empty), the user must <i>not</i> have <i>any</i> of the indicated data group tags to be 
     * considered a member of this subpopulation. 
     * @param allOfGroups
     */
    public Set<String> getNoneOfGroups() {
        return noneOfGroups;
    }
    public void setNoneOfGroups(Set<String> noneOfGroups) {
        this.noneOfGroups = (noneOfGroups == null) ? ImmutableSet.<String> of() : ImmutableSet.copyOf(noneOfGroups);
    }
    @Override
    public int hashCode() {
        return Objects.hash(allOfGroups, description, guid, maxAppVersion, minAppVersion, name, noneOfGroups, required,
                defaultGroup, version);
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Subpopulation other = (Subpopulation) obj;
        return Objects.equals(description, other.description) && Objects.equals(guid, other.guid)
                && Objects.equals(maxAppVersion, other.maxAppVersion)
                && Objects.equals(minAppVersion, other.minAppVersion) && Objects.equals(name, other.name)
                && Objects.equals(allOfGroups, other.allOfGroups) && Objects.equals(noneOfGroups, other.noneOfGroups)
                && Objects.equals(required, other.required) && Objects.equals(defaultGroup, other.defaultGroup) 
                && Objects.equals(version, other.version);
    }
    @Override
    public String toString() {
        return new ToStringBuilder(this, TO_STRING_STYLE).append("name", name).append("description", description)
                .append("guid", guid).append("required", required).append("defaultGroup", defaultGroup)
                .append("version", version).append("minAppVersion", minAppVersion)
                .append("maxAppVersion", maxAppVersion).append("allOfGroups", allOfGroups)
                .append("noneOfGroups", noneOfGroups).build();
    }
}
