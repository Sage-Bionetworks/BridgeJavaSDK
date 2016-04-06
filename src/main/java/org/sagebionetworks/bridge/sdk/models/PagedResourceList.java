package org.sagebionetworks.bridge.sdk.models;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class PagedResourceList<T> {
    private static final String OFFSET_KEY_FILTER = "offsetKey";
    private final List<T> items;
    private final Integer offsetBy;
    private final int pageSize;
    private final int total;
    private final Map<String,String> filters = Maps.newHashMap();

    @JsonCreator
    PagedResourceList(@JsonProperty("items") List<T> items, @JsonProperty("offsetBy") Integer offsetBy,
            @JsonProperty("pageSize") int pageSize, @JsonProperty("total") int total) {
        this.items = items;
        this.offsetBy = offsetBy;
        this.pageSize = pageSize;
        this.total = total;
    }
    public List<T> getItems() {
        return items;
    }
    public Integer getOffsetBy() {
        return offsetBy;
    }
    public String getOffsetKey() {
        return filters.get(OFFSET_KEY_FILTER);
    }
    public int getPageSize() {
        return pageSize;
    }
    public int getTotal() {
        return total;
    }
    @JsonAnyGetter
    public Map<String, String> getFilters() {
        return ImmutableMap.copyOf(filters);
    }
    @JsonAnySetter
    void setFilter(String key, String value) {
        if (isNotBlank(key) && isNotBlank(value)) {
            filters.put(key, value);    
        }
    }
    @Override
    public String toString() {
        return "PagedResourceList [items=" + items + ", offsetBy=" + offsetBy + ", pageSize=" + pageSize + ", total="
                + total + ", filters=" + filters + "]";
    }
}
