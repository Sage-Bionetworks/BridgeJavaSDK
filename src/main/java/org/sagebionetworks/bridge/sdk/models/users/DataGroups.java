package org.sagebionetworks.bridge.sdk.models.users;

import java.util.Objects;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

public final class DataGroups {
    
    private Set<String> dataGroups = ImmutableSet.of();
    
    public Set<String> getDataGroups() {
        return dataGroups;
    }
    
    public void setDataGroups(Set<String> dataGroups) {
        if (dataGroups != null) {
            this.dataGroups = ImmutableSet.copyOf(dataGroups);    
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(dataGroups);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        DataGroups other = (DataGroups) obj;
        return Objects.equals(dataGroups, other.dataGroups);
    }

    @Override
    public String toString() {
        return "DataGroups [dataGroups=" + dataGroups + "]";
    }

}
