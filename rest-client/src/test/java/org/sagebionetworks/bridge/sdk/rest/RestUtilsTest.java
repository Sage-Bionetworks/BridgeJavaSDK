package org.sagebionetworks.bridge.sdk.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import org.sagebionetworks.bridge.sdk.rest.model.ClientInfo;
import org.sagebionetworks.bridge.sdk.rest.model.ConsentStatus;
import org.sagebionetworks.bridge.sdk.rest.model.UserSessionInfo;

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
    public void userAgentWithFullClientInfo() {
        ClientInfo clientInfo = new ClientInfo();
        clientInfo.setAppName("AppName");
        clientInfo.setAppVersion(1);
        clientInfo.setDeviceName("Device Name");
        clientInfo.setOsName("iPhone OS");
        clientInfo.setOsVersion("2.0.0");
        clientInfo.setSdkName("BridgeJavaSDK");
        clientInfo.setSdkVersion(3);
        
        String userAgent = RestUtils.getUserAgent(clientInfo);
        assertEquals("AppName/1 (Device Name; iPhone OS/2.0.0) BridgeJavaSDK/3", userAgent);
    }
    
    @Test
    public void userAgentWithSomeDeviceInfo() {
        ClientInfo clientInfo = new ClientInfo();
        clientInfo.setAppName("AppName");
        clientInfo.setAppVersion(1);
        //no device name
        clientInfo.setOsName("iPhone OS");
        clientInfo.setOsVersion("2.0.0");
        clientInfo.setSdkName("BridgeJavaSDK");
        clientInfo.setSdkVersion(3);
        String userAgent = RestUtils.getUserAgent(clientInfo);
        assertEquals("AppName/1 BridgeJavaSDK/3", userAgent);
        
        clientInfo = new ClientInfo();
        clientInfo.setAppName("AppName");
        clientInfo.setAppVersion(1);
        clientInfo.setDeviceName("Device Name");
        //no OS name
        clientInfo.setOsVersion("2.0.0");
        clientInfo.setSdkName("BridgeJavaSDK");
        clientInfo.setSdkVersion(3);
        userAgent = RestUtils.getUserAgent(clientInfo);
        assertEquals("AppName/1 BridgeJavaSDK/3", userAgent);
        
        clientInfo = new ClientInfo();
        clientInfo.setAppName("AppName");
        clientInfo.setAppVersion(1);
        clientInfo.setDeviceName("Device Name");
        clientInfo.setOsName("iPhone OS");
        //no OS version
        clientInfo.setSdkName("BridgeJavaSDK");
        clientInfo.setSdkVersion(3);
        userAgent = RestUtils.getUserAgent(clientInfo);
        assertEquals("AppName/1 BridgeJavaSDK/3", userAgent);
    }
    
    @Test
    public void userAgentWithAppInfoAndSdk() {
        ClientInfo clientInfo = new ClientInfo();
        clientInfo.setAppName("AppName");
        clientInfo.setAppVersion(1);
        clientInfo.setSdkName("BridgeJavaSDK");
        clientInfo.setSdkVersion(3);
        
        assertEquals("AppName/1 BridgeJavaSDK/3", RestUtils.getUserAgent(clientInfo));
    }
    
    @Test
    public void userAgentWithAppInfo() {
        ClientInfo clientInfo = new ClientInfo();
        clientInfo.setAppName("AppName");
        clientInfo.setAppVersion(1);
        
        assertEquals("AppName/1", RestUtils.getUserAgent(clientInfo));
    }
    
    @Test
    public void userAgentBlankWithNoInfo() {
        assertEquals("", RestUtils.getUserAgent(new ClientInfo()));
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
    
    private Map<String,ConsentStatus> map(ConsentStatus status1, ConsentStatus status2) {
        Map<String,ConsentStatus> map = Maps.newHashMap();
        map.put(status1.getSubpopulationGuid(), status1);
        map.put(status2.getSubpopulationGuid(), status2);
        return map;
    }
    
    private ConsentStatus requiredConsent(String guid, boolean isConsented, boolean isRecent) {
        ConsentStatus status = new ConsentStatus();
        status.setSubpopulationGuid(guid);
        status.setConsented(isConsented);
        status.setRequired(true);
        status.setSignedMostRecentConsent(isRecent);
        return status;
    }
    
    private ConsentStatus optConsent(String guid, boolean isConsented, boolean isRecent) {
        ConsentStatus status = new ConsentStatus();
        status.setSubpopulationGuid(guid);
        status.setConsented(isConsented);
        status.setRequired(false);
        status.setSignedMostRecentConsent(isRecent);
        return status;
    }
    
    private UserSessionInfo session(Map<String,ConsentStatus> statuses) {
        UserSessionInfo session = new UserSessionInfo();
        session.setConsentStatuses(statuses);
        return session;
    }
    
}
