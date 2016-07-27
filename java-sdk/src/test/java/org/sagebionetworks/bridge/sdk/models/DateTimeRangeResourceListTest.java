package org.sagebionetworks.bridge.sdk.models;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.joda.time.DateTime;
import org.junit.Test;

import org.sagebionetworks.bridge.Tests;
import org.sagebionetworks.bridge.sdk.utils.Utilities;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;

public class DateTimeRangeResourceListTest {
    private static final TypeReference<DateTimeRangeResourceList<String>> STRING_LIST_TYPE = 
            new TypeReference<DateTimeRangeResourceList<String>>() {};
    private static final ArrayList<String> RESOURCE_ITEMS = Lists.newArrayList("A", "B", "C");
    private static final DateTime START_TIME = DateTime.parse("2016-02-03T10:10:10.000Z");
    private static final DateTime END_TIME = DateTime.parse("2016-02-23T14:40:40.000Z");
    
    @Test
    public void canSerialize() throws Exception {
        String json = Tests.unescapeJson("{'items':['A','B','C'],'total':'3',"+
                "'startTime':'2016-02-03T10:10:10.000Z','endTime':'2016-02-23T14:40:40.000Z',"+
                "'type':'DateTimeRangeResourceList'}");

        JsonNode node = Utilities.getMapper().readTree(json);
        assertEquals("2016-02-03T10:10:10.000Z", node.get("startTime").asText());
        assertEquals("2016-02-23T14:40:40.000Z", node.get("endTime").asText());
        assertEquals(3, node.get("total").asInt());
        assertEquals("DateTimeRangeResourceList", node.get("type").asText());
        assertEquals("A", node.get("items").get(0).asText());
        assertEquals("B", node.get("items").get(1).asText());
        assertEquals("C", node.get("items").get(2).asText());
        assertEquals(5, node.size());
        
        DateTimeRangeResourceList<String> desList = Utilities.getMapper().readValue(node.toString(), STRING_LIST_TYPE);
        
        assertEquals(RESOURCE_ITEMS, desList.getItems());
        assertEquals(RESOURCE_ITEMS.size(), desList.getTotal());
        assertEquals(START_TIME, desList.getStartTime());
        assertEquals(END_TIME, desList.getEndTime());
    }
    
}
