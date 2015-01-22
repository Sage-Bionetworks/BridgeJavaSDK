package org.sagebionetworks.bridge.sdk.models.studies;

import java.util.List;

import org.sagebionetworks.bridge.sdk.models.holders.VersionHolder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as=Study.class)
public final class Study implements VersionHolder {
    
    private String name;
    private String identifier;
    private Long version;
    private String researcherRole;
    private int minAgeOfConsent;
    private int maxNumOfParticipants;
    private List<String> trackers;
    private String hostname;

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

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getResearcherRole() {
        return researcherRole;
    }

    public void setResearcherRole(String researcherRole) {
        this.researcherRole = researcherRole;
    }

    public int getMinAgeOfConsent() {
        return minAgeOfConsent;
    }

    public void setMinAgeOfConsent(int minAgeOfConsent) {
        this.minAgeOfConsent = minAgeOfConsent;
    }

    public int getMaxNumOfParticipants() {
        return maxNumOfParticipants;
    }

    public void setMaxNumOfParticipants(int maxParticipants) {
        this.maxNumOfParticipants = maxParticipants;
    }

    public List<String> getTrackers() {
        return trackers;
    }

    public void setTrackers(List<String> trackers) {
        this.trackers = trackers;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((hostname == null) ? 0 : hostname.hashCode());
        result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
        result = prime * result + maxNumOfParticipants;
        result = prime * result + minAgeOfConsent;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((researcherRole == null) ? 0 : researcherRole.hashCode());
        result = prime * result + ((trackers == null) ? 0 : trackers.hashCode());
        result = prime * result + ((version == null) ? 0 : version.hashCode());
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
        Study other = (Study) obj;
        if (hostname == null) {
            if (other.hostname != null)
                return false;
        } else if (!hostname.equals(other.hostname))
            return false;
        if (identifier == null) {
            if (other.identifier != null)
                return false;
        } else if (!identifier.equals(other.identifier))
            return false;
        if (maxNumOfParticipants != other.maxNumOfParticipants)
            return false;
        if (minAgeOfConsent != other.minAgeOfConsent)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (researcherRole == null) {
            if (other.researcherRole != null)
                return false;
        } else if (!researcherRole.equals(other.researcherRole))
            return false;
        if (trackers == null) {
            if (other.trackers != null)
                return false;
        } else if (!trackers.equals(other.trackers))
            return false;
        if (version == null) {
            if (other.version != null)
                return false;
        } else if (!version.equals(other.version))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Study [name=" + name + ", identifier=" + identifier + ", version=" + version + ", researcherRole="
                + researcherRole + ", minAgeOfConsent=" + minAgeOfConsent + ", maxNumOfParticipants="
                + maxNumOfParticipants + ", trackers=" + trackers + ", hostname=" + hostname + "]";
    }

}
