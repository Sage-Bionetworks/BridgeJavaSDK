package org.sagebionetworks.bridge.sdk.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class UtilitiesTest {
    @Test
    public void lastWorksWithNull() {
        assertNull(RestUtils.last(null));
    }
    
    @Test
    public void lastWorksWithEmptyList() {
        assertNull(RestUtils.last(new ArrayList<>()));
    }
    
    @Test
    public void lastWorksWithOneItemList() {
        List<String> list = new ArrayList<>();
        list.add("A");
        assertEquals("A", RestUtils.last(list));
    }
    
    @Test
    public void lastWorksWithManyItemsList() {
        List<String> list = new ArrayList<>();
        list.add("A");
        list.add("B");
        list.add("C");
        assertEquals("C", RestUtils.last(list));
        
    }
}
