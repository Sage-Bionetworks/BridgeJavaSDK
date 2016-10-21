package org.sagebionetworks.bridge.sdk.models;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.junit.Test;

import org.sagebionetworks.bridge.Tests;
import org.sagebionetworks.bridge.sdk.utils.BridgeUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;

public class DateRangeResourceListTest {

    private static final TypeReference<DateRangeResourceList<String>> STRING_LIST_TYPE = 
            new TypeReference<DateRangeResourceList<String>>() {};
    private static final ArrayList<String> RESOURCE_ITEMS = Lists.newArrayList("A", "B", "C");
    private static final LocalDate START_DATE = LocalDate.parse("2016-02-03");
    private static final LocalDate END_DATE = LocalDate.parse("2016-02-23");
    
    @Test
    public void canSerialize() throws Exception {
        String json = Tests.unescapeJson("{'items':['A','B','C'],'total':'3','startDate':'2016-02-03','endDate':'2016-02-23','type':'DateRangeResourceList'}");

        JsonNode node = BridgeUtils.getMapper().readTree(json);
        assertEquals("2016-02-03", node.get("startDate").asText());
        assertEquals("2016-02-23", node.get("endDate").asText());
        assertEquals(3, node.get("total").asInt());
        assertEquals("DateRangeResourceList", node.get("type").asText());
        assertEquals("A", node.get("items").get(0).asText());
        assertEquals("B", node.get("items").get(1).asText());
        assertEquals("C", node.get("items").get(2).asText());
        assertEquals(5, node.size());
        
        DateRangeResourceList<String> desList = BridgeUtils.getMapper().readValue(node.toString(), STRING_LIST_TYPE);
        
        assertEquals(RESOURCE_ITEMS, desList.getItems());
        assertEquals(RESOURCE_ITEMS.size(), desList.getTotal());
        assertEquals(START_DATE, desList.getStartDate());
        assertEquals(END_DATE, desList.getEndDate());
    }
    

}
