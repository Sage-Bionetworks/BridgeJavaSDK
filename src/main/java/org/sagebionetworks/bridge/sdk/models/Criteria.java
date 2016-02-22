package org.sagebionetworks.bridge.sdk.models;

import java.util.Objects;
import java.util.Set;

import com.google.common.collect.Sets;

public final class Criteria {
    
    private Integer minAppVersion;
    private Integer maxAppVersion;
    private Set<String> allOfGroups = Sets.newHashSet();
    private Set<String> noneOfGroups = Sets.newHashSet();

    /**
     * The object associated with these criteria should be returned to participants only if the application 
     * version supplied by the client is equal to or greater than the minAppVersion. If null, there is no 
     * minimum required version.
     */
    public Integer getMinAppVersion(){
        return minAppVersion;
    }
    public void setMinAppVersion(Integer minAppVersion) {
        this.minAppVersion = minAppVersion;
    }
    /**
     * The object associated with these criteria should be returned to participants only if the application 
     * version supplied by the client is less that or equal to the maxAppVersion. If null, there is no 
     * maximum required version.
     */
    public Integer getMaxAppVersion(){
        return maxAppVersion;
    }
    public void setMaxAppVersion(Integer maxAppVersion) {
        this.maxAppVersion = maxAppVersion;
    }
    /**
     * The object associated with these criteria should be returned to participants only if the user has 
     * all of the groups contained in this set of data groups. If the set is empty, there are no required 
     * data groups. Data groups must be defined on the study object to be included in this set, and the 
     * same data group cannot be in the allOfGroups and noneOfGroups sets at the same time. 
     */
    public Set<String> getAllOfGroups() {
        return allOfGroups;
    }
    public void setAllOfGroups(Set<String> allOfGroups) {
        this.allOfGroups = (allOfGroups != null) ? allOfGroups : Sets.<String>newHashSet();
    }
    /**
     * The object associated with these criteria should be matched only if the user has none of the 
     * groups contained in this set of data groups. If the set is empty, there are no prohibited 
     * data groups. Data groups must be defined on the study object to be included in this set, and 
     * the same data group cannot be in the allOfGroups and noneOfGroups sets at the same time.
     */
    public Set<String> getNoneOfGroups() {
        return noneOfGroups;
    }
    public void setNoneOfGroups(Set<String> noneOfGroups) {
        this.noneOfGroups = (noneOfGroups != null) ? noneOfGroups : Sets.<String>newHashSet();
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
