package org.sagebionetworks.bridge.sdk.models;

import org.joda.time.DateTime;

public class GuidVersionHolder {
    
    private final String guid;
    private final DateTime version;
    
    public GuidVersionHolder(String guid, DateTime version) {
        this.guid = guid;
        this.version = version;
    }

    public String getGuid() {
        return guid;
    }

    public DateTime getVersion() {
        return version;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((guid == null) ? 0 : guid.hashCode());
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
        GuidVersionHolder other = (GuidVersionHolder) obj;
        if (guid == null) {
            if (other.guid != null)
                return false;
        } else if (!guid.equals(other.guid))
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
        return "GuidVersionHolder [guid=" + guid + ", version=" + version + "]";
    }
    
}
