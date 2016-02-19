package org.sagebionetworks.bridge.sdk.models;

import java.util.Objects;
import java.util.Set;

import com.google.common.collect.Sets;

public final class Criteria {
    
    private Integer minAppVersion;
    private Integer maxAppVersion;
    private Set<String> allOfGroups = Sets.newHashSet();
    private Set<String> noneOfGroups = Sets.newHashSet();

    public void setMinAppVersion(Integer minAppVersion) {
        this.minAppVersion = minAppVersion;
    }
    public Integer getMinAppVersion(){
        return minAppVersion;
    }
    public void setMaxAppVersion(Integer maxAppVersion) {
        this.maxAppVersion = maxAppVersion;
    }
    public Integer getMaxAppVersion(){
        return maxAppVersion;
    }
    public void setAllOfGroups(Set<String> allOfGroups) {
        this.allOfGroups = allOfGroups;
    }
    public Set<String> getAllOfGroups() {
        return allOfGroups;
    }
    public void setNoneOfGroups(Set<String> noneOfGroups) {
        this.noneOfGroups = noneOfGroups;
    }
    public Set<String> getNoneOfGroups() {
        return noneOfGroups;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(minAppVersion, maxAppVersion, allOfGroups, noneOfGroups);
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Criteria other = (Criteria) obj;
        return Objects.equals(noneOfGroups, other.noneOfGroups) && 
                Objects.equals(allOfGroups, other.allOfGroups) && 
                Objects.equals(minAppVersion, other.minAppVersion) && 
                Objects.equals(maxAppVersion, other.maxAppVersion);
    }
    @Override
    public String toString() {
        return "Criteria [allOfGroups=" + allOfGroups + ", noneOfGroups=" + noneOfGroups
                + ", minAppVersion=" + minAppVersion + ", maxAppVersion=" + maxAppVersion + "]";
    }

}
