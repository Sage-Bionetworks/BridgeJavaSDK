package org.sagebionetworks.bridge.rest;

public class Tests {
    public static String unescapeJson(String json) {
        return json.replaceAll("'", "\"");
    }
}
