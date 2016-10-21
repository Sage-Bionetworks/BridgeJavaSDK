package org.sagebionetworks.bridge.sdk;

public class Tests {
    public static String unescapeJson(String json) {
        return json.replaceAll("'", "\"");
    }
}
