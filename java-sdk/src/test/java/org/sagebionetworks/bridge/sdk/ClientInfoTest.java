package org.sagebionetworks.bridge.sdk;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class ClientInfoTest {

    ClientInfo info;
    
    @Before
    public void before() {
        info = new ClientInfo.Builder().build(); // no default values
    }
    
    @Test 
    public void appInfo() {
        info = new ClientInfo.Builder().withAppName("Belgium").withAppVersion(3).withSdkVersion(3).build();
        assertEquals("Belgium/3 BridgeJavaSDK/3", info.toString());
    }
    
    @Test
    public void systemInfo() {
        info = new ClientInfo.Builder().withDevice("Motorola Flip-Phone").withOsName("Android").withOsVersion("14")
                .withSdkVersion(3).build();
        assertEquals("(Motorola Flip-Phone; Android/14) BridgeJavaSDK/3", info.toString());
    }
    
    @Test
    public void everything() {
        info = new ClientInfo.Builder().withAppName("Belgium").withAppVersion(2).withDevice("Motorola Flip-Phone")
                    .withOsName("Android").withOsVersion("14").withSdkVersion(10).build();
        assertEquals("Belgium/2 (Motorola Flip-Phone; Android/14) BridgeJavaSDK/10", info.toString());
    }
}
