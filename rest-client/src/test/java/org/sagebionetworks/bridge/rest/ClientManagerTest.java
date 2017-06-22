package org.sagebionetworks.bridge.rest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import org.sagebionetworks.bridge.rest.Config.Props;
import org.sagebionetworks.bridge.rest.model.ClientInfo;
import org.sagebionetworks.bridge.rest.model.Environment;
import org.sagebionetworks.bridge.rest.model.SignIn;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Config.class})
public class ClientManagerTest {

    @Mock
    Config config;
    
    @Mock
    ClientManager.ClientSupplier supplier;
    
    @Test
    public void testInitFromConfig() {
        SignIn accountSignIn = new SignIn().study("study-identifier").email("account@email.com")
                .password("account-password");
        SignIn adminSignIn = new SignIn().study("study-identifier").email("admin@email.com").password("admin-password");
        
        doReturn(accountSignIn).when(config).getAccountSignIn();
        doReturn(adminSignIn).when(config).getAdminSignIn();
        doReturn("study-identifier").when(config).getStudyIdentifier();
        doReturn("1").when(config).getSdkVersion();
        doReturn("account@email.com").when(config).getAccountEmail();
        doReturn("account-password").when(config).getAccountPassword();
        doReturn("admin@email.com").when(config).getAdminEmail();
        doReturn("admin-password").when(config).getAdminPassword();
        doReturn("fr,en").when(config).getLanguages();
        doReturn("dudeski").when(config).getDevName();
        doReturn(Environment.PRODUCTION).when(config).getEnvironment();
        doReturn("debug").when(config).getLogLevel();
        
        ClientManager manager = new ClientManager.Builder().withConfig(config)
                .withClientSupplier(supplier).build();
        
        assertEquals("study-identifier", manager.getConfig().getStudyIdentifier());
        assertEquals("1", manager.getConfig().getSdkVersion());
        assertEquals(accountSignIn, manager.getConfig().getAccountSignIn());
        assertEquals(adminSignIn, manager.getConfig().getAdminSignIn());
        assertEquals(Lists.newArrayList("fr","en"), manager.getAcceptedLanguages());
        assertEquals(Environment.PRODUCTION, manager.getConfig().getEnvironment());
        assertEquals("dudeski", manager.getConfig().getDevName());
        assertEquals("debug", manager.getConfig().getLogLevel());
    }
    
    @Test
    public void testClientInfoLoadedFromPropsFile() {
        SignIn signIn = new SignIn().study("study-identifier").email("account@email.com")
                .password("account-password");
        
        ClientManager manager = new ClientManager.Builder().withSignIn(signIn).withClientSupplier(supplier).build();
        
        // These values are set in the test bridge-sdk.properties file, and we want to verify they
        // are used to construct the clientInfo object, so this doesn't have to be hard-wired into
        // the application.
        
        ClientInfo info = manager.getClientInfo();
        assertEquals("testName", info.getAppName());
        assertEquals((Integer)33, info.getAppVersion());
        assertEquals("Swankie Device", info.getDeviceName());
        assertEquals("webOS", info.getOsName());
        assertEquals("ultimate", info.getOsVersion());
        assertEquals("BridgeJavaSDK", info.getSdkName());
        assertEquals((Integer)5, info.getSdkVersion());
    }

    @Test
    public void testGetUrl() {
        String localUrl = ClientManager.getUrl(Environment.LOCAL);
        assertEquals("http://localhost:9000", localUrl);

        String devUrl = ClientManager.getUrl(Environment.DEVELOP);
        assertEquals("https://webservices-develop.sagebridge.org", devUrl);

        String stagingUrl = ClientManager.getUrl(Environment.STAGING);
        assertEquals("https://webservices-staging.sagebridge.org", stagingUrl);

        String prodUrl = ClientManager.getUrl(Environment.PRODUCTION);
        assertEquals("https://webservices.sagebridge.org", prodUrl);
    }
    
    @Test
    public void canOverrideHostUrl() {
        Config config = new Config();
        config.set(Props.HOST, "https://aws.bridgeserver.com");
        config.set(Environment.PRODUCTION);
        SignIn signIn = config.getAdminSignIn();
        ClientManager manager = new ClientManager.Builder()
                .withSignIn(signIn)
                .withClientSupplier(supplier)
                .withConfig(config)
                .build();
        
        assertEquals("https://aws.bridgeserver.com", manager.getHostUrl());
    }
}
