package org.sagebionetworks.bridge.sdk;

import java.util.Iterator;
import java.util.List;

import org.sagebionetworks.bridge.sdk.models.ResourceList;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

final class ResourceListImpl<T> implements ResourceList<T> {

    private final List<T> items;
    private final int count;
    
    @JsonCreator
    public ResourceListImpl(@JsonProperty("items") List<T> items, @JsonProperty("count") int count) {
        this.items = items;
        this.count = count;
    }
    
    public List<T> getItems() {
        return items;
    }
    
    public int getCount() {
        return count;
    }

    @Override
    public Iterator<T> iterator() {
        return items.iterator();
    }
    
}
