package org.sagebionetworks.bridge.sdk;

import java.util.Iterator;
import java.util.List;

import org.sagebionetworks.bridge.sdk.models.ResourceList;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

final class ResourceListImpl<T> implements ResourceList<T> {

    private final List<T> items;
    private final int total;
    
    @JsonCreator
    public ResourceListImpl(@JsonProperty("items") List<T> items, @JsonProperty("total") int total) {
        this.items = items;
        this.total = total;
    }
    
    public List<T> getItems() {
        return items;
    }
    
    public int getTotal() {
        return total;
    }

    @Override
    public Iterator<T> iterator() {
        return items.iterator();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + total;
        result = prime * result + ((items == null) ? 0 : items.hashCode());
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
        @SuppressWarnings("rawtypes")
        ResourceListImpl other = (ResourceListImpl) obj;
        if (total != other.total)
            return false;
        if (items == null) {
            if (other.items != null)
                return false;
        } else if (!items.equals(other.items))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ResourceListImpl [items=" + items + ", total=" + total + "]";
    }
    
}
