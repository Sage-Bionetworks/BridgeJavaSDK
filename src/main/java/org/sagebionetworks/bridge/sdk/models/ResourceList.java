package org.sagebionetworks.bridge.sdk.models;

import java.util.Iterator;
import java.util.List;

public interface ResourceList<T> extends Iterable<T> {
    public List<T> getItems();
    public int getTotal();
    public T get(int index);
    @Override public Iterator<T> iterator();
}