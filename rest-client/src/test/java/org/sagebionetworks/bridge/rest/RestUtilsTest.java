package org.sagebionetworks.bridge.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.sagebionetworks.bridge.rest.Tests.setVariableValueInObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.sagebionetworks.bridge.rest.model.ClientInfo;
import org.sagebionetworks.bridge.rest.model.ConsentStatus;
import org.sagebionetworks.bridge.rest.model.UserSessionInfo;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class RestUtilsTest {
    
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
    
    @Test
    public void userAgentFormatting() {
        assertInfo("appName", new ClientInfo().appName("appName"));
        assertInfo("/2", new ClientInfo().appVersion(2));
        assertInfo("appName/2", new ClientInfo().appName("appName").appVersion(2));
        
        assertInfo("appName/2 sdkName", new ClientInfo().appName("appName").appVersion(2).sdkName("sdkName"));
        assertInfo("appName/2 /4", new ClientInfo().appName("appName").appVersion(2).sdkVersion(4));
        assertInfo("appName/2 sdkName/4",
                new ClientInfo().appName("appName").appVersion(2).sdkName("sdkName").sdkVersion(4));
        
        assertInfo("appName (osName)", new ClientInfo().appName("appName").osName("osName"));
        assertInfo("appName (/osVersion)", new ClientInfo().appName("appName").osVersion("osVersion"));
        assertInfo("appName (osName/osVersion)",
                new ClientInfo().appName("appName").osName("osName").osVersion("osVersion"));
        
        assertInfo("appName (deviceName; osName)",
                new ClientInfo().appName("appName").deviceName("deviceName").osName("osName"));
        assertInfo("appName (deviceName; /osVersion)",
                new ClientInfo().appName("appName").deviceName("deviceName").osVersion("osVersion"));
        assertInfo("appName (deviceName; osName/osVersion)",
                new ClientInfo().appName("appName").deviceName("deviceName").osName("osName").osVersion("osVersion"));
        
        assertInfo("appName (osName) sdkName", new ClientInfo().appName("appName").osName("osName").sdkName("sdkName"));
        assertInfo("appName (osName) /4", new ClientInfo().appName("appName").osName("osName").sdkVersion(4));
        assertInfo("appName (osName) sdkName/4",
                new ClientInfo().appName("appName").osName("osName").sdkName("sdkName").sdkVersion(4));
        assertInfo("appName (deviceName; osName/osVersion) sdkName/4", new ClientInfo().appName("appName")
                .deviceName("deviceName").osName("osName").osVersion("osVersion").sdkName("sdkName").sdkVersion(4));
        
        // Other examples from prior versions of this test.
        assertInfo("AppName/1 (Device Name; iPhone OS/2.0.0) BridgeJavaSDK/3", new ClientInfo().appName("AppName").appVersion(1).deviceName("Device Name")
                .osName("iPhone OS").osVersion("2.0.0").sdkName("BridgeJavaSDK").sdkVersion(3));
        
        assertInfo("AppName/1 (iPhone OS/2.0.0) BridgeJavaSDK/3", new ClientInfo().appName("AppName").appVersion(1)
                .osName("iPhone OS").osVersion("2.0.0").sdkName("BridgeJavaSDK").sdkVersion(3));
        
        assertInfo("AppName/1 (Device Name; /2.0.0) BridgeJavaSDK/3", new ClientInfo().appName("AppName")
                .appVersion(1).deviceName("Device Name").osVersion("2.0.0").sdkName("BridgeJavaSDK").sdkVersion(3));
        
        assertInfo("AppName/1 (Device Name; iPhone OS) BridgeJavaSDK/3", new ClientInfo().appName("AppName")
                .appVersion(1).deviceName("Device Name").osName("iPhone OS").sdkName("BridgeJavaSDK").sdkVersion(3));
        
        assertInfo("AppName/1 BridgeJavaSDK/3",
                new ClientInfo().appName("AppName").appVersion(1).sdkName("BridgeJavaSDK").sdkVersion(3));
        
        assertInfo("AppName/1", new ClientInfo().appName("AppName").appVersion(1));
    }
    
    private void assertInfo(String expected, ClientInfo info) {
        assertEquals(expected, RestUtils.getUserAgent(info));
    }
    
    @Test
    public void userAgentBlankWithNoInfo() {
        assertNull(RestUtils.getUserAgent(new ClientInfo()));
    }
    
    @Test
    public void isUserConsentedSuccess() {
        Map<String,ConsentStatus> map = map(
            requiredConsent("A", true, true),
            requiredConsent("B", true, true)
        );
        assertTrue(RestUtils.isUserConsented(session(map)));
    }
    
    public void isUserConsentedFailure() {
        Map<String,ConsentStatus> map = map(
            requiredConsent("A", true, true),
            requiredConsent("B", false, false)
        );
        assertFalse(RestUtils.isUserConsented(session(map)));
    }
    
    @Test
    public void isUserConsentedEmpty() {
        Map<String,ConsentStatus> map = Maps.newHashMap();
        assertFalse(RestUtils.isUserConsented(session(map)));
    }
    
    @Test
    public void isConsentCurrentSuccess() {
        Map<String,ConsentStatus> map = map(
            requiredConsent("A", true, true),
            requiredConsent("B", true, true)
        );
        assertTrue(RestUtils.isConsentCurrent(session(map)));
    }
    
    @Test
    public void isConsentCurrentFailure() {
        Map<String,ConsentStatus> map = map(
            requiredConsent("A", true, true),
            requiredConsent("B", true, false)
        );
        assertFalse(RestUtils.isConsentCurrent(session(map)));
    }
    
    @Test
    public void isConsentCurrentEmpty() {
        Map<String,ConsentStatus> map = Maps.newHashMap();
        assertFalse(RestUtils.isConsentCurrent(session(map)));
    }
    
    @Test
    public void optionalConsentIgnored() {
        Map<String,ConsentStatus> map = map(
            requiredConsent("A", true, true),
            optConsent("B", false, false)
        );
        assertTrue(RestUtils.isConsentCurrent(session(map)));
        assertTrue(RestUtils.isUserConsented(session(map)));
    }

    @Test
    public void optionalConsentAloneIgnored() {
        Map<String,ConsentStatus> map = map(
            requiredConsent("A", true, true),
            optConsent("B", false, false)
        );
        assertTrue(RestUtils.isConsentCurrent(session(map)));
        assertTrue(RestUtils.isUserConsented(session(map)));
    }
    
    @Test
    public void acceptLanguageNull() {
        assertNull(RestUtils.getAcceptLanguage(null));
    }
    
    @Test
    public void acceptLanguageEmptyList() {
        assertNull(RestUtils.getAcceptLanguage(Lists.<String>newArrayList()));
    }
    
    @Test
    public void acceptLanguageWorks() {
        assertEquals("de,en", RestUtils.getAcceptLanguage(Lists.<String>newArrayList("de","en")));
    }
    
    @Test
    public void acceptLanguageNullsEmptyRemoved() {
        assertEquals("de,en", RestUtils.getAcceptLanguage(Lists.<String>newArrayList("","de",null,"en")));
    }
    
    private Map<String,ConsentStatus> map(ConsentStatus status1, ConsentStatus status2) {
        Map<String,ConsentStatus> map = Maps.newHashMap();
        map.put(status1.getSubpopulationGuid(), status1);
        map.put(status2.getSubpopulationGuid(), status2);
        return map;
    }
    
    private ConsentStatus requiredConsent(String guid, boolean isConsented, boolean isRecent) {
        ConsentStatus status = new ConsentStatus();
        setVariableValueInObject(status, "subpopulationGuid", guid);
        setVariableValueInObject(status, "consented", isConsented);
        setVariableValueInObject(status, "required", true);
        setVariableValueInObject(status, "signedMostRecentConsent", isRecent);
        return status;
    }
    
    private ConsentStatus optConsent(String guid, boolean isConsented, boolean isRecent) {
        ConsentStatus status = new ConsentStatus();
        setVariableValueInObject(status, "subpopulationGuid", guid);
        setVariableValueInObject(status, "consented", isConsented);
        setVariableValueInObject(status, "required", false);
        setVariableValueInObject(status, "signedMostRecentConsent", isRecent);
        return status;
    }
    
    private UserSessionInfo session(Map<String,ConsentStatus> statuses) {
        UserSessionInfo session = new UserSessionInfo();
        try {
            Tests.setVariableValueInObject(session, "consentStatuses", statuses);    
        } catch(Exception e) {
        }
        return session;
    }
    
}
