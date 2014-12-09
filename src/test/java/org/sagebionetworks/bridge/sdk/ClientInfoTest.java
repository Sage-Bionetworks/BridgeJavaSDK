package org.sagebionetworks.bridge.sdk;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class ClientInfoTest {

    ClientInfo info;
    
    @Before
    public void before() {
        info = new ClientInfo();
    }
    
    @Test
    public void appOnly() {
        assertEquals("BridgeJavaSDK", info.toString());
    }
    
    @Test 
    public void appAndDevice() {
        info.setAppName("Belgium");
        assertEquals("Belgium BridgeJavaSDK", info.toString());
        
        info.setAppVersion("1.0");
        assertEquals("Belgium/1.0 BridgeJavaSDK", info.toString());
    }
    
    @Test
    public void deviceOnly() {
        info.setDevice("Motorola Flip-Phone");
        info.setOsName("Android");
        info.setOsVersion("14");
        
        assertEquals("(Motorola Flip-Phone; Android/14) BridgeJavaSDK", info.toString());
    }
    
    @Test
    public void everything() {
        info.setAppName("Belgium");
        info.setAppVersion("2.3");
        info.setDevice("Motorola Flip-Phone");
        info.setOsName("Android");
        info.setOsVersion("14");
        info.setSdkVersion("10");
        
        assertEquals("Belgium/2.3 (Motorola Flip-Phone; Android/14) BridgeJavaSDK/10", info.toString());
    }
}
