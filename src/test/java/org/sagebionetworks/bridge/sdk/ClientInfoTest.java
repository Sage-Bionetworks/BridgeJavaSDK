package org.sagebionetworks.bridge.sdk;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class ClientInfoTest {

    ClientInfo info;
    
    @Before
    public void before() {
        info = new ClientInfo(false); // no default values
    }
    
    @Test
    public void sdkOnly() {
        assertEquals("BridgeJavaSDK", info.toString());
    }
    
    @Test 
    public void appOnly() {
        info.withAppName("Belgium");
        assertEquals("Belgium BridgeJavaSDK", info.toString());
        
        info.withAppVersion(1);
        assertEquals("Belgium/1 BridgeJavaSDK", info.toString());
    }
    
    @Test
    public void deviceAndOs() {
        info.withDevice("Motorola Flip-Phone").withOsName("Android").withOsVersion("14");
        assertEquals("(Motorola Flip-Phone; Android 14) BridgeJavaSDK", info.toString());
    }
    
    @Test
    public void deviceOnly() {
        info.withDevice("Motorola Flip");
        assertEquals("(Motorola Flip) BridgeJavaSDK", info.toString());
    }
    
    @Test
    public void osOnly() {
        info.withOsName("Mac OSX").withOsVersion("10.5.1");
        assertEquals("(Mac OSX; 10.5.1) BridgeJavaSDK", info.toString());
    }
    
    @Test
    public void everything() {
        info.withAppName("Belgium");
        info.withAppVersion(2);
        info.withDevice("Motorola Flip-Phone");
        info.withOsName("Android");
        info.withOsVersion("14");
        info.withSdkVersion("10");
        
        assertEquals("Belgium/2 (Motorola Flip-Phone; Android 14) BridgeJavaSDK/10", info.toString());
    }
}
