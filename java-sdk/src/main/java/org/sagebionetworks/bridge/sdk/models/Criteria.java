package org.sagebionetworks.bridge.sdk.models;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.sagebionetworks.bridge.sdk.models.studies.OperatingSystem;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public final class Criteria {
    
    private Set<String> allOfGroups = Sets.newHashSet();
    private Set<String> noneOfGroups = Sets.newHashSet();
    private Map<OperatingSystem, Integer> minAppVersions = Maps.newHashMap();
    private Map<OperatingSystem, Integer> maxAppVersions = Maps.newHashMap();

    /**
     * The object associated with these criteria should be returned to participants only if the application 
     * version supplied by the client is equal to or greater than the minAppVersion, for a given platform. 
     * If there is no value for the current application's platform, there is no minimum required version.
     * 
     * @return a map of minimal operating system versions per operating system
     */
    public Map<OperatingSystem,Integer> getMinAppVersions() {
        return minAppVersions;
    }
    /**
     * @param minAppVersions
     *      a map of minimal operating system versions per operating system
     */
    public void setMinAppVersions(Map<OperatingSystem,Integer> minAppVersions) {
        this.minAppVersions = (minAppVersions != null) ? minAppVersions : new HashMap<OperatingSystem,Integer>();
    }
    /**
     * The object associated with these criteria should be returned to participants only if the application 
     * version supplied by the client is equal to or less than the maxAppVersion, for a given platform. 
     * If there is no value for the current application's platform, there is no maximum allowed version.
     *  
     * @return a map of maximum operating system versions per operating system
     */
    public Map<OperatingSystem,Integer> getMaxAppVersions(){
        return maxAppVersions;
    }
    /**
     * @param maxAppVersions
     *      a map of maximum operating system versions per operating system
     */
    public void setMaxAppVersions(Map<OperatingSystem,Integer> maxAppVersions) {
        this.maxAppVersions = (maxAppVersions != null) ? maxAppVersions : new HashMap<OperatingSystem,Integer>();
    }
    /**
     * The object associated with these criteria should be returned to participants only if the user has 
     * all of the groups contained in this set of data groups. If the set is empty, there are no required 
     * data groups. Data groups must be defined on the study object to be included in this set, and the 
     * same data group cannot be in the allOfGroups and noneOfGroups sets at the same time.
     * 
     * @return
     *      get a set of all data group strings that must be present when selecting a participant
     */
    public Set<String> getAllOfGroups() {
        return allOfGroups;
    }
    /**
     * @param allOfGroups
     *      a set of data groups that must be present when selecting a participant
     */
    public void setAllOfGroups(Set<String> allOfGroups) {
        this.allOfGroups = (allOfGroups != null) ? allOfGroups : Sets.<String>newHashSet();
    }
    /**
     * The object associated with these criteria should be matched only if the user has none of the 
     * groups contained in this set of data groups. If the set is empty, there are no prohibited 
     * data groups. Data groups must be defined on the study object to be included in this set, and 
     * the same data group cannot be in the allOfGroups and noneOfGroups sets at the same time.
     * @return
     *      get a set of all data group strings that must be absent when selecting
     */
    public Set<String> getNoneOfGroups() {
        return noneOfGroups;
    }
    /**
     * @param noneOfGroups
     *      a set of data groups, none of which can be present when selecting a participant
     */
    public void setNoneOfGroups(Set<String> noneOfGroups) {
        this.noneOfGroups = (noneOfGroups != null) ? noneOfGroups : Sets.<String>newHashSet();
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(minAppVersions, maxAppVersions, allOfGroups, noneOfGroups);
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
                Objects.equals(minAppVersions, other.minAppVersions) && 
                Objects.equals(maxAppVersions, other.maxAppVersions);
    }
    @Override
    public String toString() {
        return "Criteria [allOfGroups=" + allOfGroups + ", noneOfGroups=" + noneOfGroups
                + ", minAppVersions=" + minAppVersions + ", maxAppVersions=" + maxAppVersions + "]";
    }

}
